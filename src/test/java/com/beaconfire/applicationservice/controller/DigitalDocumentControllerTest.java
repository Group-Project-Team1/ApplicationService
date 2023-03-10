package com.beaconfire.applicationservice.controller;

import com.beaconfire.applicationservice.domain.entity.DigitalDocument;
import com.beaconfire.applicationservice.repo.EmployeeRepo;
import com.beaconfire.applicationservice.service.DigitalDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.*;
import static net.bytebuddy.matcher.ElementMatchers.is;


import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class DigitalDocumentControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private DigitalDocumentController digitalDocumentController;

    @MockBean
    private DigitalDocumentService digitalDocumentService;

    @MockBean
    private EmployeeRepo employeeRepo;

    @BeforeEach
    public void setUp() {

        MockitoAnnotations.openMocks(this);
        this.digitalDocumentController = new DigitalDocumentController(digitalDocumentService);
        this.digitalDocumentService = new DigitalDocumentService(employeeRepo);
    }


    private static final List<DigitalDocument> DOCUMENT_LIST = Arrays.asList(
            new DigitalDocument(1, true, "OPT","file1.pdf", "pdf", "12345"),
            new DigitalDocument(2, true, "EAD", "file2.docx", "docx", "67890")
    );

//    @Test
//    public void testGetDocuments() throws Exception {
//        // Mock the service method to return a list of DigitalDocument objects
//        List<DigitalDocument> expectedDocuments = Arrays.asList(
//                new DigitalDocument(1, true, "OPT","file1.pdf", "pdf", "12345"),
//                new DigitalDocument(2, true, "EAD", "file2.docx", "docx", "67890"));
//
//        when(digitalDocumentService.getDocuments()).thenReturn(expectedDocuments);
//
//        // Verify that the service method was called once
//        verify(digitalDocumentService, times(1)).getDocuments();
//    }
//
//    @Test
//    public void testDownloadFile() throws Exception {
//        byte[] fileContent = "dummy file content".getBytes();
//        String fileName = "file.pdf";
//        given(digitalDocumentService.downloadFile(fileName)).willReturn(fileContent);
//
//        mockMvc.perform(get("/all/download/{fileTitle}", fileName))
//                .andExpect(status().isOk())
//                .andExpect((ResultMatcher) content().contentType(MediaType.APPLICATION_OCTET_STREAM))
//                .andExpect(MockMvcResultMatchers.content().json("[{\"id\":1,\"fileTitle\":\"file.pdf\",\"fileType\":\"pdf\"}]"));
//    }
}
