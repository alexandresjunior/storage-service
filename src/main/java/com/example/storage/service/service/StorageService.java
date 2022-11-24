package com.example.storage.service.service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.storage.service.model.File;
import com.example.storage.service.model.Image;
import com.example.storage.service.service.persistence.FileRepository;
import com.example.storage.service.service.persistence.ImageRepository;
import com.example.storage.service.util.ImageUtils;

@Service
public class StorageService {

    public String uploadImageToDatabase(MultipartFile multipartFile) throws IOException {
        Image image = new Image();

        image.setName(multipartFile.getOriginalFilename());
        image.setType(multipartFile.getContentType());
        image.setImageData(ImageUtils.compressImage(multipartFile.getBytes()));

        image = imageRepository.save(image);

        if (Objects.nonNull(image)) {
            return String.format("File upload successfully: %s", image.getName());
        }

        return String.format("File upload failed!");
    }

    public byte[] downloadImageFromDatabase(String fileName) {
        Optional<Image> image = imageRepository.findByName(fileName);

        return ImageUtils.decompressImage(image.get().getImageData());
    }

    public String uploadImageToFileSystem(MultipartFile multipartFile) throws IOException {
        String filePath = FOLDER_PATH + multipartFile.getOriginalFilename();

        File file = new File();

        file.setName(multipartFile.getOriginalFilename());
        file.setType(multipartFile.getContentType());
        file.setFilePath(filePath);

        multipartFile.transferTo(new java.io.File(filePath));

        file = fileRepository.save(file);

        if (Objects.nonNull(file)) {
            return String.format("File uploaded successfully: %s", file.getName());
        }

        return String.format("File upload failed!");
    }

    public byte[] downloadImageFromFileSystem(String fileName) throws IOException {
        Optional<File> file = fileRepository.findByName(fileName);

        String filePath = file.get().getFilePath();

        return Files.readAllBytes(new java.io.File(filePath).toPath());
    }

    private final String FOLDER_PATH = "C:\\Users\\alexandresouzajr\\storage-service\\";

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private FileRepository fileRepository;

}
