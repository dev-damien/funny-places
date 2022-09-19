package de.damien.funnyplaces.images;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    public ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping
    public Long uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            return imageService.uploadImage(file);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> downloadImage(@PathVariable("id") Long id) {
        try {
            byte[] imageData = imageService.downloadImage(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/jpeg"))
                    .body(imageData);
        } catch (NoSuchFileException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

}
