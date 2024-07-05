package com.example.demo.service;

import com.example.demo.model.FileData;
import com.example.demo.repository.FileDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Service
public class ImageService {
    @Autowired
    private FileDataRepository fileDataRepository;
    private final String FOLDER_PATH="/Users/HP/IdeaProjects/demo/images";


    /*public String uploadImage(MultipartFile multipartFile, Long announcementId) throws IOException {
        FileData fileData = imageRepository.save(FileData.builder()
                .name(multipartFile.getName())
                .type(multipartFile.getContentType())
                .announcementId(announcementId)
                .imageData(ImageUtils.compressImage(multipartFile.getBytes()))
                .build());
        if (fileData != null) {
            return "file uploaded successfully: " + multipartFile.getOriginalFilename();
        }
        return null;
    }

    public byte[] downloadImage(String fileName) {
        Optional<FileData> imageData = imageRepository.findByName(fileName);
        return ImageUtils.decompressImage(imageData.get().getImageData());
    }*/

    /*public List<byte[]> downloadImages(Long announcementId) {
        List<FileData> fileDataList = imageRepository.findAllByAnnouncementId(announcementId);
        List<byte[]> images = new ArrayList<>();
        for (FileData item : fileDataList) {
            byte[] image = ImageUtils.decompressImage(item.getImageData());
            images.add(image);
        }
        return images;
    }*/

    public String uploadImageToFileSystem(MultipartFile file, Integer announcementId) throws IOException {
        String filePath = FOLDER_PATH+file.getOriginalFilename();
        FileData fileData=fileDataRepository.save(FileData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .announcementId(announcementId)
                .filePath(filePath).build());
        file.transferTo(new File(filePath));
        if (fileData != null) {
            return "file uploaded successfully : " + filePath;
        }
        return null;
    }

    public byte[] downloadImageFromFileSystem(String fileName) throws IOException {
        Optional<FileData> fileData = fileDataRepository.findByName(fileName);
        String filePath=fileData.get().getFilePath();
        byte[] images = Files.readAllBytes(new File(filePath).toPath());
        return images;
    }
}
