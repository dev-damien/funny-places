package de.damien.funnyplaces.images;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.AuthenticationException;
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
    public Long uploadImage(@RequestParam("image") MultipartFile file, @RequestHeader("token") String token) {
        try {
            return imageService.uploadImage(file, token);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> downloadImage(@PathVariable("id") Long id, @RequestHeader("token") String token) {
        try {
            byte[] imageData = imageService.downloadImage(id, token);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/jpeg"))
                    .body(imageData);
        } catch (NoSuchFileException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

}
