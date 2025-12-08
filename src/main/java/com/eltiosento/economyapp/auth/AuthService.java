package com.eltiosento.economyapp.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eltiosento.economyapp.error.UserExistException;
import com.eltiosento.economyapp.error.UserNotFoundException;
import com.eltiosento.economyapp.jwt.JwtService;
import com.eltiosento.economyapp.model.User;
import com.eltiosento.economyapp.repository.RoleRepository;
import com.eltiosento.economyapp.repository.UserRepository;

@Service
public class AuthService {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private RoleRepository roleRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private JwtService jwtService;

        @Autowired
        private AuthenticationManager authenticationManager;

        public AuthResponse login(LoginRequest userCredentials) {

                try {
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(userCredentials.getUsername(),
                                                        userCredentials.getPassword()));
                } catch (Exception e) {
                        throw new UserExistException("Bad credentials");
                }

                User user = userRepository.findByUsername(userCredentials.getUsername())
                                .orElseThrow(() -> new UserNotFoundException("User not found"));

                String token = jwtService.generateToken(user);

                return AuthResponse.builder()
                                .token(token)
                                .message("Welcome " + user.getUsername())
                                .build();

        }

        public AuthResponse register(RegisterRequest newUser) {

                if (userRepository.existsByUsername(newUser.getUsername())) {
                        throw new UserExistException(
                                        "User with username " + newUser.getUsername() + " already exists.");
                }

                if (newUser.getEmail() != null && userRepository.existsByEmail(newUser.getEmail())) {
                        throw new UserExistException("User with email " + newUser.getEmail() + " already exists.");
                }

                User user = User.builder()
                                .username(newUser.getUsername().trim())
                                .password(passwordEncoder.encode(newUser.getPassword().trim()))
                                .email(newUser.getEmail() != null ? newUser.getEmail().trim() : null)
                                .firstName(newUser.getFirstName() != null ? newUser.getFirstName().trim() : null)
                                .lastName(newUser.getLastName() != null ? newUser.getLastName().trim() : null)
                                .profileImage(newUser.getProfileImage() != null ? newUser.getProfileImage().trim()
                                                : null)
                                .role(roleRepository.findById((long) 4)
                                                .get())
                                .build();

                userRepository.save(user);

                return AuthResponse.builder()
                                .token(jwtService.generateToken(user))
                                .message("Welcome " + user.getUsername())
                                .build();

        }

}
