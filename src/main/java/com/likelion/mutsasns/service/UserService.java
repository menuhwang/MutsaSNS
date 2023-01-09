package com.likelion.mutsasns.service;

import com.likelion.mutsasns.domain.user.Role;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.user.*;
import com.likelion.mutsasns.exception.badrequest.UpdateUserRoleException;
import com.likelion.mutsasns.exception.conflict.DuplicateUsernameException;
import com.likelion.mutsasns.exception.notfound.UserNotFoundException;
import com.likelion.mutsasns.exception.unauthorized.InvalidPasswordException;
import com.likelion.mutsasns.repository.UserRepository;
import com.likelion.mutsasns.security.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUserName()).orElseThrow(UserNotFoundException::new);
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
            throw new InvalidPasswordException();
        return new LoginResponse(jwtProvider.generateToken(user));
    }

    public JoinResponse join(JoinRequest joinRequest) {
        if (userRepository.existsByUsername(joinRequest.getUserName())) throw new DuplicateUsernameException();
        String encoded = passwordEncoder.encode(joinRequest.getPassword());
        User saved = userRepository.save(joinRequest.toEntity(encoded));
        return JoinResponse.of(saved);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    public UserDetailResponse updateRole(String adminUsername, Long id, UpdateUserRoleRequest updateUserRoleRequest) {
        User admin = userRepository.findByUsername(adminUsername).orElseThrow(UserNotFoundException::new);
        if (id.equals(admin.getId())) throw new UpdateUserRoleException();
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        user.updateRole(Role.of(updateUserRoleRequest.getRole().toUpperCase()));
        return UserDetailResponse.of(user);
    }

    public UserDetailResponse findByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        return UserDetailResponse.of(user);
    }
}
