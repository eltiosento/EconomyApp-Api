package com.eltiosento.economyapp.uploads;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;

public class UploadFile {

    @Schema(description = "Fitxer d'imatge", type = "string", format = "binary")
    public MultipartFile file;
}
