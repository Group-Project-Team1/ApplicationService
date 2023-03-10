package com.beaconfire.applicationservice.controller;
import antlr.build.Tool;

import com.beaconfire.applicationservice.domain.entity.ApplicationWorkFlow;
import com.beaconfire.applicationservice.domain.entity.VisaDocumentStatus;
import com.beaconfire.applicationservice.security.AuthUserDetail;
import com.beaconfire.applicationservice.service.ApplicationWorkFlowService;
import com.beaconfire.applicationservice.service.DigitalDocumentService;
import com.beaconfire.applicationservice.service.VisaDocumentStatusService;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class ApplicationControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private ApplicationWorkFlowService applicationWorkFlowService;

    @MockBean
    private DigitalDocumentService digitalDocumentService;

    @MockBean
    private VisaDocumentStatusService visaDocumentStatusService;

    @MockBean
    private AuthUserDetail authUserDetail;
    @MockBean
    private SecurityContext securityContext;

    @Autowired
    private ApplicationController applicationController;

    private MockMvc mockMvc;

    private final int TEST_EMPLOYEE_ID = 123;
    private final String TEST_OPT_RECEIPT_URL = "https://test.opt/receipt";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.applicationController = new ApplicationController(applicationWorkFlowService, digitalDocumentService, visaDocumentStatusService);
    }

    @Test
    public void submitApplicationForm_shouldCreateVisaDocumentStatusRecord() {
        // Arrange
        Integer employeeId = 123;
        String OPTReceiptURL = "http://example.com/OPTReceipt";

        ApplicationWorkFlow applicationWorkFlow = new ApplicationWorkFlow();
        applicationWorkFlow.setId(1);
        applicationWorkFlow.setEmployeeId(employeeId);
        applicationWorkFlow.setStatus("never submitted");

        when(applicationWorkFlowService.getApplicationByEmployeeId(employeeId)).thenReturn(applicationWorkFlow);
        when(visaDocumentStatusService.getVisaDocumentStatusByEmployeeId(employeeId)).thenReturn(null);

        // Act
        applicationController.submitApplicationForm(employeeId, OPTReceiptURL);

        // Assert
        verify(visaDocumentStatusService, times(1)).createVisaDocumentStatusRecord(employeeId, OPTReceiptURL);
    }

    @Test
    @WithMockUser(authorities = {"employee"})
    public void submitApplicationForm_ShouldUpdateVisaDocumentStatusRecord() throws Exception {
        // given
        Integer employeeId = 1;
        String OPTReceiptURL = "https://example.com/opt-receipt.pdf";

        ApplicationWorkFlow applicationWorkFlow = new ApplicationWorkFlow();
        applicationWorkFlow.setId(1);
        applicationWorkFlow.setEmployeeId(employeeId);
        applicationWorkFlow.setStatus("rejected");

        when(applicationWorkFlowService.getApplicationByEmployeeId(employeeId)).thenReturn(applicationWorkFlow);
        // mock behavior of visaDocumentStatusService
        VisaDocumentStatus visaDocumentStatus = new VisaDocumentStatus();
        visaDocumentStatus.setId(1);
        visaDocumentStatus.setEmployeeId(employeeId);
        visaDocumentStatus.setStatus("approved");
        visaDocumentStatus.setPath("https://example.com/opt-receipt.pdf");
        when(visaDocumentStatusService.getVisaDocumentStatusByEmployeeId(employeeId)).thenReturn(visaDocumentStatus);

        mockMvc = MockMvcBuilders.standaloneSetup(applicationController).build();
        mockMvc.perform(post("/employee/{employeeId}/applicationForm", employeeId)
                        .param("OPTReceiptURL", OPTReceiptURL))
                .andExpect(status().isOk());

        // then
        verify(visaDocumentStatusService, times(1)).updateVisaDocumentStatus(employeeId, "pending", 1);
        verify(visaDocumentStatusService, times(1)).updateVisaDocumentStatusURL(employeeId, OPTReceiptURL);
    }


    @Test
    @WithMockUser(authorities = {"employee"})
    public void testSubmitApplication() throws Exception {
        // mock the application workflow service
        ApplicationWorkFlow applicationWorkflow = new ApplicationWorkFlow();
        applicationWorkflow.setStatus("never submitted");
        when(applicationWorkFlowService.getApplicationByEmployeeId(1)).thenReturn(applicationWorkflow);

        // setup the mock MVC environment
        mockMvc = MockMvcBuilders.standaloneSetup(applicationController).build();

        // make a request to the endpoint
        mockMvc.perform(MockMvcRequestBuilders.post("/employee/1/application"))
                .andExpect(status().isOk());

        // verify that the application status was updated
        verify(applicationWorkFlowService).updateApplicationStatus(1);
    }

    @Test
    public void getApplicationByEmployeeId_ValidInput_Success() throws Exception {
        // Arrange
        ApplicationWorkFlow applicationWorkFlow = new ApplicationWorkFlow();
        applicationWorkFlow.setId(1);
        when(applicationWorkFlowService.getApplicationByEmployeeId(1)).thenReturn(applicationWorkFlow);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(1);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        mockMvc = MockMvcBuilders.standaloneSetup(applicationController).build();
        mockMvc.perform(get("/all/1/application")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(1)));

        // Assert
        Mockito.verify(applicationWorkFlowService, Mockito.times(1)).getApplicationByEmployeeId(1);
    }

    @Test
    public void createNewApplication_ValidInput_Success() throws Exception {
        // Arrange
        Integer employeeId = 1;

        // Act
        mockMvc = MockMvcBuilders.standaloneSetup(applicationController).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/createApplication/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Assert
        Mockito.verify(applicationWorkFlowService, Mockito.times(1)).createNewApplication(employeeId);
    }

    @Test
    @WithMockUser(authorities = "hr")
    public void testGetApplicationsByStatus() throws Exception {
        String status = "pending";

        // Create mock application workflow objects
        ApplicationWorkFlow app1 = new ApplicationWorkFlow();
        app1.setId(1);
        app1.setStatus("pending");
        app1.setEmployeeId(1);

        ApplicationWorkFlow app2 = new ApplicationWorkFlow();
        app2.setId(2);
        app2.setStatus("approved");
        app2.setEmployeeId(2);

        // Mock the applicationWorkFlowService to return the list of applications
        Mockito.when(applicationWorkFlowService.getApplicationsByStatus(status))
                .thenReturn(Arrays.asList(app1, app2));

        // Send the GET request to the endpoint
        mockMvc = MockMvcBuilders.standaloneSetup(applicationController).build();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/hr/" + status + "/applications"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Extract the response body
        String responseBody = mvcResult.getResponse().getContentAsString();

        // Deserialize the response body into a list of ApplicationWorkFlow objects
        ObjectMapper objectMapper = new ObjectMapper();
        List<ApplicationWorkFlow> applications = objectMapper.readValue(responseBody, new TypeReference<List<ApplicationWorkFlow>>() {});

        // Assert that the list contains the expected applications
        Assert.assertEquals(2, applications.size());
        Assert.assertEquals(1, applications.get(0).getId());
        Assert.assertEquals(2, applications.get(1).getId());
    }

    @Test
    @WithMockUser(authorities = {"hr"})
    public void testReviewApplication_approve() throws Exception {
        // Mock data
        Integer employeeId = 123;
        String action = "approve";

        ApplicationWorkFlow applicationWorkFlow = new ApplicationWorkFlow();
        applicationWorkFlow.setId(1);
        applicationWorkFlow.setEmployeeId(employeeId);
        applicationWorkFlow.setStatus("pending");

        // Mock service method
        when(applicationWorkFlowService.getApplicationByEmployeeId(employeeId)).thenReturn(applicationWorkFlow);

        // Perform request
        mockMvc = MockMvcBuilders.standaloneSetup(applicationController).build();
        mockMvc.perform(post("/hr/viewApplication/{employeeId}", employeeId)
                        .param("action", action).param("feedback", ""))
                .andExpect(status().isOk());

        // Verify that the service method was called with the correct arguments
        verify(applicationWorkFlowService, times(1)).approveApplication(applicationWorkFlow.getId());
    }


    @Test
    @WithMockUser(authorities = "hr")
    public void testReviewApplication_reject() throws Exception {
        Integer employeeId = 1;
        String action = "reject";

        ApplicationWorkFlow applicationWorkFlow = new ApplicationWorkFlow();
        applicationWorkFlow.setId(1);
        applicationWorkFlow.setEmployeeId(employeeId);
        applicationWorkFlow.setStatus("pending");

        // Mock service method
        when(applicationWorkFlowService.getApplicationByEmployeeId(employeeId)).thenReturn(applicationWorkFlow);

        // Perform request
        mockMvc = MockMvcBuilders.standaloneSetup(applicationController).build();
        mockMvc.perform(post("/hr/viewApplication/{employeeId}", employeeId)
                        .param("action", action).param("feedback", "wrong file"))
                .andExpect(status().isOk());

        // Verify that the service method was called with the correct arguments
        verify(applicationWorkFlowService, times(1)).rejectApplication(applicationWorkFlow.getId(), "wrong file");
    }

}



