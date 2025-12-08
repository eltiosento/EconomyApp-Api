package com.eltiosento.economyapp.uploads;

import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.eltiosento.economyapp.dto.UserDTO;

public interface StorageService {

    void init();

    UserDTO store(Long userId, MultipartFile file, Authentication authentication);

    Resource loadAsResource(String filename);

}
