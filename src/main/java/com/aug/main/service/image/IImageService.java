package com.aug.main.service.image;

import com.aug.main.dto.ImageDto;
import com.aug.main.model.Image;
import com.aug.main.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {
    Image getImageById(Long id);
    void deleteImageById(Long id);
    List<ImageDto> saveImage(List<MultipartFile> files, Long productId);
    void updateImage(MultipartFile file, Long imageId);
}
