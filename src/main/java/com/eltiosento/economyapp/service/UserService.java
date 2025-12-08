package com.eltiosento.economyapp.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.eltiosento.economyapp.dto.ChangePasswordDTO;
import com.eltiosento.economyapp.dto.UserDTO;

public interface UserService {

    UserDTO getUserById(Long id, Authentication authentication);

    List<UserDTO> getAllUsers();

    UserDTO updateUser(Long id, UserDTO UserDTO, Authentication authentication);

    boolean updatePassword(Long id, ChangePasswordDTO passwordDTO, Authentication authentication);

    UserDTO deleteUser(Long id, Authentication authentication);

}
