package com.example.demo.service;

import com.example.demo.model.ImageData;
import com.example.demo.repository.ImageRepository;
import com.example.demo.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    public String uploadImage(MultipartFile multipartFile, Long announcementId) throws IOException {
        ImageData imageData = imageRepository.save(ImageData.builder()
                .name(multipartFile.getName())
                .type(multipartFile.getContentType())
                .announcementId(announcementId)
                .imageData(ImageUtils.compressImage(multipartFile.getBytes()))
                .build());
        if (imageData != null) {
            return "file uploaded successfully: " + multipartFile.getOriginalFilename();
        }
        return null;
    }

    public byte[] downloadImage(String fileName) {
        Optional<ImageData> imageData = imageRepository.findByName(fileName);
        return ImageUtils.decompressImage(imageData.get().getImageData());
    }

    public List<byte[]> downloadImages(Long announcementId) {
        List<ImageData> imageDataList = imageRepository.findAllByAnnouncementId(announcementId);
        List<byte[]> images = new ArrayList<>();
        for (ImageData item : imageDataList) {
            byte[] image = ImageUtils.decompressImage(item.getImageData());
            images.add(image);
        }
        return images;
    }
}
