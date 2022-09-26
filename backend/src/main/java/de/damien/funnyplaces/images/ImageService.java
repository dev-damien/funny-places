package de.damien.funnyplaces.images;

import de.damien.funnyplaces.accounts.AccountService;
import de.damien.funnyplaces.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Long uploadImage(MultipartFile file, String token) throws IOException, AuthenticationException {
        if (AccountService.getAccountByToken(token) == null) {
            throw new AuthenticationException("Invalid token");
        }
        Image image = imageRepository.save(Image.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes()))
                .build());
        return image.getImageId();
    }

    public byte[] downloadImage(Long fileId, String token) throws NoSuchFileException, AuthenticationException {
        if (AccountService.getAccountByToken(token) == null) {
            throw new AuthenticationException("Invalid token");
        }
        Optional<Image> imageOptional = imageRepository.findById(fileId);
        if (imageOptional.isEmpty()) {
            throw new NoSuchFileException("");
        }
        Image image = imageOptional.get();
        byte[] imageData = image.getImageData();
        return ImageUtils.decompressImage(imageData);
    }
}
