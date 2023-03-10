package com.beaconfire.applicationservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.beaconfire.applicationservice.dao.DigitalDocumentDAO;
import com.beaconfire.applicationservice.repo.EmployeeRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;



@ExtendWith(MockitoExtension.class)
public class DigitalDocumentServiceTest {
    @InjectMocks
    DigitalDocumentService digitalDocumentService;

    @Mock
    DigitalDocumentDAO digitalDocumentDAO;

    @Mock
    EmployeeRepo repository;

    @Mock
    AmazonS3 amazonS3;


    private static final String BUCKET_NAME = "my-bucket";
    private static final String FILE_NAME = "my-file.txt";

    @Test
    public void testConvertMultiPartFileToFile() throws IOException {
        DigitalDocumentService digitalDocumentService = new DigitalDocumentService(repository);

        String fileName = "test-file.txt";
        String content = "This is a test file.";

        MockMultipartFile multipartFile = new MockMultipartFile(fileName, fileName, "text/plain", content.getBytes());
        File file = digitalDocumentService.convertMultiPartFileToFile(multipartFile);

        assertTrue(file.exists(), "File should exist");
        assertEquals(fileName, file.getName(), "File name should match");
        assertEquals(content, new String(java.nio.file.Files.readAllBytes(file.toPath())), "File content should match");

        // Clean up
        file.delete();
    }


    @Test
    public void test_UploadFile() throws IOException {

        // Create a new instance of DigitalDocumentService
        DigitalDocumentService service = new DigitalDocumentService(repository);
        service.setDigitalDocumentDAO(digitalDocumentDAO);
        service.amazonS3 = amazonS3;

        // Create a mock MultipartFile object with file contents
        byte[] bytes = "Test String".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("testFile", "testFile.txt", "text/plain", bytes);
        File file = service.convertMultiPartFileToFile(mockMultipartFile);


        // Mock the AmazonS3 client to return the mock PutObjectRequest object
        when(amazonS3.putObject(any(PutObjectRequest.class))).thenReturn(null);

        // Call the uploadFile method with the mock MultipartFile object
        String result = service.uploadFile(mockMultipartFile);


        // Verify that the uploaded file name is returned
        assertEquals("testFile.txt", result);
    }


}
