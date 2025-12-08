package com.eltiosento.economyapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eltiosento.economyapp.dto.ChangePasswordDTO;
import com.eltiosento.economyapp.dto.UserDTO;
import com.eltiosento.economyapp.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, Authentication authentication) {
        UserDTO userDTO = userService.getUserById(id, authentication);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO,
            Authentication authentication) {
        UserDTO modifiUserDTO = userService.updateUser(id, userDTO, authentication);

        return ResponseEntity.ok(modifiUserDTO);
    }

    @PutMapping("user/{id}/password")
    public ResponseEntity<?> updateUserPassword(@PathVariable Long id,
            @Valid @RequestBody ChangePasswordDTO passwordDTO,
            Authentication authentication) {
        boolean updatedPassword = userService.updatePassword(id, passwordDTO, authentication);

        return updatedPassword ? ResponseEntity.ok("Password updated successfully")
                : ResponseEntity.badRequest().body("Failed to update password");

    }

    @DeleteMapping("user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        UserDTO userDTO = userService.deleteUser(id, authentication);

        return ResponseEntity.ok(userDTO);
    }
}
