package com.fc_board.service;

import com.fc_board.exception.user.UserAlreadyExistsException;
import com.fc_board.exception.user.UserNotFoundException;
import com.fc_board.model.entity.UserEntity;
import com.fc_board.model.user.User;
import com.fc_board.model.user.UserAuthenticationResponse;
import com.fc_board.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
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
}
