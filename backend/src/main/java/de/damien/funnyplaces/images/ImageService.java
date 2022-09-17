package de.damien.funnyplaces.images;

import de.damien.funnyplaces.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Optional;

@Service
public class ImageService {

    private ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public String uploadImage(MultipartFile file) throws IOException {
        Image image = imageRepository.save(Image.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes()))
                .build());
        return image.getName();
    }

    public byte[] downloadImage(Long fileId) throws NoSuchFileException {
        Optional<Image> imageOptional = imageRepository.findById(fileId);
        if (imageOptional.isEmpty()) {
            throw new NoSuchFileException("");
        }
        Image image = imageOptional.get();
        byte[] imageData = image.getImageData();
        return ImageUtils.decompressImage(imageData);
    }
}
