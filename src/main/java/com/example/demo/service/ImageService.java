package com.example.demo.service;

import com.example.demo.controller.AnnouncementController;
import com.example.demo.model.FileData;
import com.example.demo.repository.FileDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {
    @Autowired
    private FileDataRepository fileDataRepository;

    Logger logger = LoggerFactory.getLogger(ImageService.class.getName());


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
        String FOLDER_PATH = "C:/Users/HP/IdeaProjects/demo/images/";
        String filePath = FOLDER_PATH + file.getOriginalFilename();
        fileDataRepository.save(FileData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .announcementId(announcementId)
                .filePath(filePath).build());
        file.transferTo(new File(filePath));
        return "file uploaded successfully : " + filePath;
    }

    public byte[] downloadImageFromFileSystem(String fileName) throws IOException {
        try {
            List<FileData> fileDataList = fileDataRepository.findByName(fileName);
            if (fileDataList.isEmpty()) {
                throw new FileNotFoundException("File not found: " + fileName);
            }
            FileData fileData = fileDataList.get(0);
            String filePath = fileData.getFilePath();
            return Files.readAllBytes(new File(filePath).toPath());
        } catch (FileNotFoundException e) {
            logger.error("File not found: {}", fileName);
            throw new FileNotFoundException("File not found: " + fileName);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<String> getImageFileNamesFromFileSystem(Integer announcementId) {
        List<FileData> fileDataList = fileDataRepository.findByAnnouncementId(announcementId);
        List<String> fileNames = new ArrayList<>();
        for (FileData fileData : fileDataList) {
            fileNames.add(fileData.getName());
        }
        return fileNames;
    }
}
