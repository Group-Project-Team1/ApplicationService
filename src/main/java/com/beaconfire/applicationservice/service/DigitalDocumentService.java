package com.beaconfire.applicationservice.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.beaconfire.applicationservice.dao.DigitalDocumentDAO;
import com.beaconfire.applicationservice.domain.entity.DigitalDocument;
import com.beaconfire.applicationservice.domain.entity.Employee;
import com.beaconfire.applicationservice.domain.entity.PersonalDocument;
import com.beaconfire.applicationservice.repo.EmployeeRepo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

@Service
public class DigitalDocumentService {

    private DigitalDocumentDAO digitalDocumentDAO;
    private final EmployeeRepo repository;
    private static final Logger log = LoggerFactory.getLogger(DigitalDocumentService.class);
    @Autowired
    private AmazonS3 amazonS3;
    @Value("${aws.s3.bucket}")
    private String s3BucketName;


    @Autowired
    public DigitalDocumentService(EmployeeRepo repository) {
        this.repository = repository;
    }

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
    public String uploadFile(MultipartFile multipartFile) {
        String fileName = "";
        try {
            File file = convertMultiPartFileToFile(multipartFile);
            fileName = LocalDateTime.now() + "_" + file.getName();

            log.info("Uploading file with name {}", fileName);
            PutObjectRequest putObjectRequest = new PutObjectRequest(s3BucketName, fileName, file);
            amazonS3.putObject(putObjectRequest);
            Files.delete(file.toPath()); // Remove the file locally created in the project folder
        } catch (AmazonServiceException e) {
            log.error("Error {} occurred while uploading file", e.getLocalizedMessage());
        } catch (IOException ex) {
            log.error("Error {} occurred while deleting temporary file", ex.getLocalizedMessage());
        }
        return fileName;
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

    public URL getFileUrl(String fileName){
        DateTime expiration = DateUtil.offsetHour(new Date(), 1);
        URL url = amazonS3.generatePresignedUrl(new GeneratePresignedUrlRequest(s3BucketName, fileName).withExpiration(expiration).withMethod(HttpMethod.GET));
        return url;
    }


    public void updatePersonalDocuments(Integer employeeId, String fileName, String fileTitle){
        PersonalDocument personalDocument = new PersonalDocument();
        URL url = getFileUrl(fileName);
        personalDocument.setTitle(fileTitle);
        personalDocument.setPath(url.toString());
        personalDocument.setCreateDate(Timestamp.valueOf(LocalDateTime.now()));

        Employee employee = repository.findEmployeeByUserId(employeeId).get(0);
        List<PersonalDocument> personalDocumentList = employee.getPersonalDocuments();
        personalDocumentList.add(personalDocument);
        employee.setPersonalDocuments(personalDocumentList);
        repository.save(employee);
    }



    /**
     * get all digital documents
     * @return
     */
    @Transactional
    public List<DigitalDocument> getDocuments(){
        return digitalDocumentDAO.getDocuments();
    }


}
