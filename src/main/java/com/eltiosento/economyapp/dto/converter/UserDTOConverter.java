package com.eltiosento.economyapp.dto.converter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eltiosento.economyapp.dto.UserDTO;
import com.eltiosento.economyapp.model.User;

@Component
public class UserDTOConverter {

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Converts a User entity to a UserDTO
     * 
     * @param user the User entity to convert
     * @return the UserDTO
     */
    public UserDTO convertToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
