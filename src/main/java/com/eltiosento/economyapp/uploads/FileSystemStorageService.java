package com.eltiosento.economyapp.uploads;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.eltiosento.economyapp.dto.UserDTO;
import com.eltiosento.economyapp.error.MediaInternalError;
import com.eltiosento.economyapp.service.UserService;

import jakarta.annotation.PostConstruct;

@Service
public class FileSystemStorageService implements StorageService {

    @Value("${media.location}")
    private String mediaLocation;

    private Path rootLocation;

    @Autowired
    private UserService userService;

    @Override
    @PostConstruct
    public void init() {

        this.rootLocation = Paths.get(mediaLocation);
        try {
            Files.createDirectories(rootLocation);
        } catch (Exception e) {
            throw new MediaInternalError("Could not initialize storage");
        }
    }

    @Override
    public UserDTO store(Long userId, MultipartFile file, Authentication authentication) {

        // En el userService és on es fa la validació de si l'usuari és qui fa la
        // solicitud o si es un superadmin
        // que pot fer qualsevol acció sobre qualsevol usuari.
        UserDTO user = userService.getUserById(userId, authentication);

        try {
            if (file.isEmpty()) {
                throw new MediaInternalError("Failed to store because the file was empty");
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || fileName.isBlank()) {
                throw new MediaInternalError("File name is invalid or null");
            }

            fileName = fileName.replaceAll(fileName,
                    user.getUsername() + "_profile"
                            + fileName.substring(fileName.lastIndexOf(".")));
            Path destinationFile = rootLocation.resolve(
                    Paths.get(fileName)).normalize().toAbsolutePath();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                throw new MediaInternalError("Failed to store file " + file.getOriginalFilename());

            }
            /*
             * // Aquest codi ens permiteix posar la url completa per accedir a la imatge.
             * String url = ServletUriComponentsBuilder
             * .fromCurrentContextPath()
             * .path("/api/media/image-profile/")
             * .path(fileName)
             * .toUriString();
             * 
             * // user.setProfileImage(url);
             */
            user.setProfileImage(fileName);

            return userService.updateUser(userId, user, authentication);

        } catch (Exception e) {
            throw new MediaInternalError("Failed to store file " + file.getOriginalFilename());
        }

    }

    @Override
    public Resource loadAsResource(String filename) {

        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new MediaInternalError("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new MediaInternalError("Could not read file: " + filename);
        }

    }

}
