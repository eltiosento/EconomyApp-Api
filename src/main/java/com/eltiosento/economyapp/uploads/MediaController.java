package com.eltiosento.economyapp.uploads;

import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/media")
// @CrossOrigin(origins = "*", maxAge = 3600)
public class MediaController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/upload-profile/user/{userId}")
    @Operation(summary = "Pujar imatge de perfil per un usuari", description = "Puja una imatge associada a un usuari per ID.", requestBody = @RequestBody(required = true, content = @Content(mediaType = "multipart/form-data", schema = @Schema(implementation = UploadFile.class))))
    public ResponseEntity<?> uploadFile(@PathVariable Long userId,
            @RequestParam("file") MultipartFile multipartFile, Authentication authentication) {

        return ResponseEntity.ok(storageService.store(userId, multipartFile, authentication));
    }

    @GetMapping("/image-profile/{fileName:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) throws IOException {

        Resource file = storageService.loadAsResource(fileName);

        String contentType = Files.probeContentType(file.getFile().toPath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(file);

    }
}
