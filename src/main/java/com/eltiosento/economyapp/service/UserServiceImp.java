package com.eltiosento.economyapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eltiosento.economyapp.dto.ChangePasswordDTO;
import com.eltiosento.economyapp.dto.UserDTO;
import com.eltiosento.economyapp.dto.converter.UserDTOConverter;
import com.eltiosento.economyapp.error.UnauthorizedException;
import com.eltiosento.economyapp.error.UserBadRequestException;
import com.eltiosento.economyapp.error.UserExistException;
import com.eltiosento.economyapp.error.UserNotFoundException;
import com.eltiosento.economyapp.model.User;
import com.eltiosento.economyapp.repository.UserRepository;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDTOConverter userDTOConverter;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Passem un id d'usuari perque si un SUPERADMIN vol entrar a qualsevol usuari
    // ho puga fer, proporsionat l'id de l'usuari que vulga vore.
    // Si ho gestionarem tot amb el authentication, i no passarem l'id, el
    // SUPERADMIN no podrà accedir a l'usuari que vulga, sino que només podrà
    // accedir a l'usuari proporcionat pel token.
    @Override
    public UserDTO getUserById(Long id, Authentication authentication) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        if (!isAuthorizedUser(user, authentication)) {

            throw new UnauthorizedException("You are not authorized to access this user!");
        }

        return userDTOConverter.convertToDTO(user);

    }

    @Override
    public List<UserDTO> getAllUsers() {

        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new UserNotFoundException();
        }

        List<UserDTO> listUsersDTO = users.stream().map(user -> userDTOConverter.convertToDTO(user))
                .collect(Collectors.toList());

        return listUsersDTO;

    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO, Authentication authentication) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        if (!isAutenticUser(user, authentication)) {

            throw new UnauthorizedException("You are not authorized!");
        }

        if (userDTO.getUsername() != null) {

            if (!userDTO.getUsername().equals(user.getUsername())
                    && userRepository.existsByUsername(userDTO.getUsername())) {
                throw new UserExistException("User with username " + userDTO.getUsername() + " already exists.");
            }
            if (!userDTO.getUsername().isBlank())
                user.setUsername(userDTO.getUsername().trim());
        }

        if (userDTO.getEmail() != null) {

            if (!userDTO.getEmail().equals(user.getEmail())
                    && userRepository.existsByEmail(userDTO.getEmail())) {
                throw new UserExistException("User with email " + userDTO.getEmail() + " already exists.");
            }
            if (!userDTO.getEmail().isBlank())
                user.setEmail(userDTO.getEmail().trim());
        }

        if (userDTO.getFirstName() != null && !userDTO.getFirstName().isBlank())
            user.setFirstName(userDTO.getFirstName().trim());

        if (userDTO.getLastName() != null && !userDTO.getLastName().isBlank())
            user.setLastName(userDTO.getLastName().trim());

        // Ha d'esatr perque per a pujar la imatge desde el FileSystemStorageService,
        // fem us d'aquest mètode
        if (userDTO.getProfileImage() != null && !userDTO.getProfileImage().trim().isEmpty())
            user.setProfileImage(userDTO.getProfileImage().trim());

        userRepository.save(user);

        return userDTOConverter.convertToDTO(user);
    }

    @Override
    public boolean updatePassword(Long id, ChangePasswordDTO passwordDTO, Authentication authentication) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        if (!isAutenticUser(user, authentication)) {

            throw new UnauthorizedException("You are not authorized!");
        }

        if (!passwordDTO.getNewPassword().contentEquals(passwordDTO.getNewPassword2())) {
            throw new UserBadRequestException("The new password and the confirmation password do not match.");
        }

        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    @Override
    public UserDTO deleteUser(Long id, Authentication authentication) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        if (!isAuthorizedUser(user, authentication)) {

            throw new UnauthorizedException("You are not authorized to access this user!");
        }

        UserDTO userDTO = userDTOConverter.convertToDTO(user);
        userRepository.delete(user);
        return userDTO;

    }

    private boolean isAuthorizedUser(User user, Authentication authentication) {
        String usernameFromToken = authentication.getName();
        String userRoleFromToken = authentication.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User role not found"))
                .getAuthority();

        // Si el rol no és SUPERADMN, verifiquem que el nom d'usuari sigua el mateix que
        // el del token
        if (!userRoleFromToken.equals("SUPERADMIN")) {
            return user.getUsername().equals(usernameFromToken);
        }
        // Si el rol és SUPERADMIN, no cal verificar el nom d'usuari
        // i es considera que l'usuari és autoritzat, per tant, retornem true.
        return true;
    }

    private boolean isAutenticUser(User user, Authentication authentication) {

        String usernameFromToken = authentication.getName();
        return user.getUsername().equals(usernameFromToken);
    }

}
