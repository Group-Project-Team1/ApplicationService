package com.beaconfire.applicationservice.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.beaconfire.applicationservice.dao.DigitalDocumentDAO;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DigitalDocumentService {

    private DigitalDocumentDAO digitalDocumentDAO;

    private static final Logger log = LoggerFactory.getLogger(DigitalDocumentService.class);
    @Autowired
    private AmazonS3 amazonS3;
    @Value("${aws.s3.bucket}")
    private String s3BucketName;


    @Autowired
    public void setDigitalDocumentDAO(DigitalDocumentDAO digitalDocumentDAO) {
        this.digitalDocumentDAO = digitalDocumentDAO;
    }


    /**
     * create a temporary file
     * @param multipartFile
     * @return
     */
    private File convertMultiPartFileToFile(MultipartFile multipartFile) {
        File file = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (IOException e) {
            log.error("Error {} occurred while converting the multipart file", e.getLocalizedMessage());
        }
        return file;
    }

    /**
     * upload file
     * @param multipartFile
     * @return
     */
    public void uploadFile(MultipartFile multipartFile) {
        try {
            File file = convertMultiPartFileToFile(multipartFile);
//            String fileName = LocalDateTime.now() + "_" + file.getName();
            String fileName = file.getName();
            log.info("Uploading file with name {}", fileName);
            PutObjectRequest putObjectRequest = new PutObjectRequest(s3BucketName, fileName, file);
            amazonS3.putObject(putObjectRequest);
            Files.delete(file.toPath()); // Remove the file locally created in the project folder
        } catch (AmazonServiceException e) {
            log.error("Error {} occurred while uploading file", e.getLocalizedMessage());
        } catch (IOException ex) {
            log.error("Error {} occurred while deleting temporary file", ex.getLocalizedMessage());
        }
    }


    /**
     * download file
     * @param fileName
     * @return
     */
    public byte[] downloadFile(String fileName) {
        try {
            log.info("Downloading file with name {}", fileName);
            GetObjectRequest getObjectRequest = new GetObjectRequest(s3BucketName, fileName);
            S3Object s3object = amazonS3.getObject(getObjectRequest);
            InputStream inputStream = s3object.getObjectContent();
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (AmazonServiceException serviceException) {
            log.info("AmazonServiceException Message: " + serviceException.getMessage());
            throw serviceException;
        } catch (AmazonClientException clientException) {
            log.info("AmazonClientException Message: " + clientException.getMessage());
            throw clientException;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFileFromS3Bucket(String fileName) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(s3BucketName, fileName);
        amazonS3.deleteObject(deleteObjectRequest);
        URL url = amazonS3.generatePresignedUrl(new GeneratePresignedUrlRequest(s3BucketName, fileName));
        System.out.println(url);
    }




}
