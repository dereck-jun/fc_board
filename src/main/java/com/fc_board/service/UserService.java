package com.fc_board.service;

import com.fc_board.exception.follow.FollowAlreadyExistsException;
import com.fc_board.exception.follow.FollowNotFoundException;
import com.fc_board.exception.follow.InvalidFollowException;
import com.fc_board.exception.post.PostNotFoundException;
import com.fc_board.exception.user.UserAlreadyExistsException;
import com.fc_board.exception.user.UserNotAllowedException;
import com.fc_board.exception.user.UserNotFoundException;
import com.fc_board.model.entity.FollowEntity;
import com.fc_board.model.entity.LikeEntity;
import com.fc_board.model.entity.PostEntity;
import com.fc_board.model.entity.UserEntity;
import com.fc_board.model.user.*;
import com.fc_board.repository.FollowRepository;
import com.fc_board.repository.LikeRepository;
import com.fc_board.repository.PostRepository;
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
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
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

    public List<Follower> getFollowersByUsername(String username, UserEntity currentUser) {
        var following = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        var followEntities = followRepository.findByFollowing(following);
        return followEntities.stream()
                .map(follow -> Follower.from(
                        getUserWithFollowingStatus(currentUser, follow.getFollower()), follow.getCreatedDateTime()
                ))
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

    public List<LikedUser> getLikedUsersByPostId(Long postId, UserEntity currentUser) {
        var postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        var postEntities = likeRepository.findByPost(postEntity);
        return postEntities.stream()
                .map(likeEntity -> getLikedUserWithFollowingStatus(likeEntity, postEntity, currentUser))
                .toList();
    }

    public List<LikedUser> getLikedUsersByUser(String username, UserEntity currentUser) {
        var userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        var postEntities = postRepository.findByUser(userEntity);
        return postEntities.stream()
                .flatMap(postEntity -> likeRepository.findByPost(postEntity)
                        .stream()
                        .map(likeEntity -> getLikedUserWithFollowingStatus(likeEntity, postEntity, currentUser)))
                .toList();
    }

    // 현재 로그인한 사용자(currentUser)와 대상 사용자(userEntity) 간의 팔로우 관계를 확인하여 User 객체를 반환
    private User getUserWithFollowingStatus(UserEntity currentUser, UserEntity userEntity) {
        var isFollowing = followRepository.findByFollowerAndFollowing(currentUser, userEntity)
                .isPresent();
        return User.from(userEntity, isFollowing);
    }

    // 특정 게시물에 좋아요를 누른 사용자의 정보와 현재 사용자의 팔로우 상태, 좋아요가 생성된 시간을 포함한 LikedUser 객체를 반환
    private LikedUser getLikedUserWithFollowingStatus(LikeEntity likeEntity, PostEntity postEntity, UserEntity currentUser) {
        var likedUserEntity = likeEntity.getUser();
        var userWithFollowingStatus = getUserWithFollowingStatus(currentUser, likedUserEntity);
        return LikedUser.from(userWithFollowingStatus, postEntity.getId(), likeEntity.getCreatedDateTime());
    }
}
