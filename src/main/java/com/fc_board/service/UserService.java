package com.fc_board.service;

import com.fc_board.exception.follow.FollowAlreadyExistsException;
import com.fc_board.exception.follow.FollowNotFoundException;
import com.fc_board.exception.follow.InvalidFollowException;
import com.fc_board.exception.user.UserAlreadyExistsException;
import com.fc_board.exception.user.UserNotAllowedException;
import com.fc_board.exception.user.UserNotFoundException;
import com.fc_board.model.entity.FollowEntity;
import com.fc_board.model.entity.UserEntity;
import com.fc_board.model.user.User;
import com.fc_board.model.user.UserAuthenticationResponse;
import com.fc_board.model.user.UserPatchRequestBody;
import com.fc_board.repository.FollowRepository;
import com.fc_board.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public User signUp(String username, String password) {
        userRepository.findByUsername(username).ifPresent(user -> {
            throw new UserAlreadyExistsException();
        });

        var savedUserEntity = userRepository.save(UserEntity.of(username, passwordEncoder.encode(password)));
        return User.from(savedUserEntity);
    }

    public UserAuthenticationResponse authenticate(@NotBlank String username, @NotBlank String password) {
        var userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (passwordEncoder.matches(password, userEntity.getPassword())) {
            var accessToken = jwtService.generateAccessToken(userEntity);
            return new UserAuthenticationResponse(accessToken);
        } else {
            throw new UserNotFoundException();
        }
    }

    public List<User> getUsers(String query, UserEntity currentUser) {
        List<UserEntity> userEntities;

        if (query != null && !query.isBlank()) {
            userEntities = userRepository.findByUsernameContaining(query);
        } else {
            userEntities = userRepository.findAll();
        }

        return userEntities.stream()
                .map(userEntity -> getUserWithFollowingStatus(currentUser, userEntity))
                .toList();
    }

    public User getUser(String username, UserEntity currentUser) {
        var userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return getUserWithFollowingStatus(currentUser, userEntity);
    }

    public User updateUser(String username, UserPatchRequestBody requestBody, UserEntity currentUser) {
        var userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (!userEntity.equals(currentUser)) {
            throw new UserNotAllowedException();
        }

        if (requestBody.description() != null) {
            userEntity.setDescription(requestBody.description());
        }

        return User.from(userRepository.save(userEntity));
    }

    @Transactional
    public User follow(String username, UserEntity currentUser) {
        var following = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (following.equals(currentUser)) {
            throw new InvalidFollowException("본인 스스로를 팔로우 할 수 없습니다.");
        }

        followRepository.findByFollowerAndFollowing(currentUser, following)
                .ifPresent(
                        follow -> {
                            throw new FollowAlreadyExistsException(currentUser, following);
                        });

        followRepository.save(FollowEntity.of(currentUser, following));

        following.setFollowingsCount(following.getFollowingsCount() + 1);
        currentUser.setFollowersCount(currentUser.getFollowersCount() + 1);

        userRepository.saveAll(List.of(following, currentUser));

        return User.from(following, true);
    }

    @Transactional
    public User unFollow(String username, UserEntity currentUser) {
        var following = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (following.equals(currentUser)) {
            throw new InvalidFollowException("본인 스스로를 언팔로우 할 수 없습니다.");
        }

        var followEntity = followRepository.findByFollowerAndFollowing(currentUser, following)
                .orElseThrow(() -> new FollowNotFoundException(currentUser, following));

        followRepository.delete(followEntity);

        following.setFollowingsCount(Math.max(0, following.getFollowingsCount() - 1));
        currentUser.setFollowersCount(Math.max(0, currentUser.getFollowersCount() - 1));

        userRepository.saveAll(List.of(following, currentUser));

        return User.from(following, false);
    }

    public List<User> getFollowersByUsername(String username, UserEntity currentUser) {
        var following = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        var followEntities = followRepository.findByFollowing(following);
        return followEntities.stream()
                .map(follow -> getUserWithFollowingStatus(currentUser, follow.getFollower()))
                .toList();
    }

    public List<User> getFollowingsByUsername(String username, UserEntity currentUser) {
        var follower = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        var followEntities = followRepository.findByFollower(follower);
        return followEntities.stream()
                .map(follow -> getUserWithFollowingStatus(currentUser, follow.getFollowing()))
                .toList();
    }

    // 현재 로그인한 사용자(follower) 기준으로 검색한 대상(following)
    private User getUserWithFollowingStatus(UserEntity currentUser, UserEntity userEntity) {
        var isFollowing = followRepository.findByFollowerAndFollowing(currentUser, userEntity).isPresent();
        return User.from(userEntity, isFollowing);
    }
}
