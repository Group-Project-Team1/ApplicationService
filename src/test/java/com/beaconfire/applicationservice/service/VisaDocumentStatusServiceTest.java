package com.beaconfire.applicationservice.service;

import com.beaconfire.applicationservice.dao.VisaDocumentStatusDao;
import com.beaconfire.applicationservice.dao.VisaDocumentsDao;
import com.beaconfire.applicationservice.domain.entity.VisaDocumentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class VisaDocumentStatusServiceTest {

    @Mock
    private VisaDocumentStatusDao visaDocumentStatusDao;
    @Mock
    private VisaDocumentsDao visaDocumentsDao;
    @InjectMocks
    private VisaDocumentStatusService visaDocumentStatusService;


    @Test
    public void test_createVisaDocumentStatusRecord() {
        // Arrange
        Integer employeeId = 1;
        String url = "http://example.com";

        // Act
        visaDocumentStatusService.createVisaDocumentStatusRecord(employeeId, url);

        // Assert
        verify(visaDocumentStatusDao).createVisaDocumentStatusRecord(employeeId, url);
    }

    @Test
    public void test_updateVisaDocumentStatus() {
        // Arrange
        Integer employeeId = 1;
        String status = "approved";
        Integer fileId = 1;

        // Act
        visaDocumentStatusService.updateVisaDocumentStatus(employeeId, status, fileId);

        // Assert
        verify(visaDocumentStatusDao).updateVisaDocumentStatus(employeeId, status, fileId);
    }

    @Test
    public void test_updateVisaDocumentStatusURL() {
        // Arrange
        Integer employeeId = 1;
        String url = "http://example.com";

        // Act
        visaDocumentStatusService.updateVisaDocumentStatusURL(employeeId, url);

        // Assert
        verify(visaDocumentStatusDao).updateVisaDocumentStatusURL(employeeId, url);
    }

    @Test
    public void test_approveSubmittedDocument() {
        // Arrange
        Integer employeeId = 1;
        int documentTotalNum = 2;
        int currentDocumentId = 1;
        when(visaDocumentsDao.getDocumentsTotalNum()).thenReturn(documentTotalNum);
        when(visaDocumentStatusDao.getDocumentIdByEmployeeId(employeeId)).thenReturn(currentDocumentId);

        // Act
        visaDocumentStatusService.approveSubmittedDocument(employeeId);

        // Assert
        if (currentDocumentId < documentTotalNum) {
            verify(visaDocumentStatusDao).updateVisaDocumentStatus(employeeId, "never submitted", currentDocumentId + 1);
        } else {
            verify(visaDocumentStatusDao).updateVisaDocumentStatus(employeeId, "approved", -1);
        }
    }

    @Test
    public void test_rejectSubmittedDocument() {
        // Arrange
        Integer employeeId = 1;
        String feedback = "Rejected";
        int currentDocumentId = 1;
        when(visaDocumentStatusDao.getDocumentIdByEmployeeId(employeeId)).thenReturn(currentDocumentId);

        // Act
        visaDocumentStatusService.rejectSubmittedDocument(employeeId, feedback);

        // Assert
        verify(visaDocumentStatusDao).updateVisaDocumentStatus(employeeId, "rejected", currentDocumentId);
    }

    @Test
    void test_getVisaDocumentStatusByEmployeeId() {
        VisaDocumentStatus expectedVisaDocumentStatus = new VisaDocumentStatus();
        expectedVisaDocumentStatus.setId(1);
        expectedVisaDocumentStatus.setEmployeeId(123);
        expectedVisaDocumentStatus.setFileId(1);
        expectedVisaDocumentStatus.setPath("https://example.com");
        expectedVisaDocumentStatus.setStatus("approved");

        when(visaDocumentStatusDao.getVisaDocumentStatusByEmployeeId(anyInt())).thenReturn(expectedVisaDocumentStatus);

        VisaDocumentStatusService visaDocumentStatusService = new VisaDocumentStatusService(visaDocumentStatusDao, visaDocumentsDao);
        VisaDocumentStatus actualVisaDocumentStatus = visaDocumentStatusService.getVisaDocumentStatusByEmployeeId(123);

        assertThat(actualVisaDocumentStatus).isEqualTo(expectedVisaDocumentStatus);
    }

    @Test
    void getDocumentTypeByIdTest() {
        String expectedDocumentType = "passport";

        when(visaDocumentsDao.getDocumentTypeById(anyInt())).thenReturn(expectedDocumentType);

        VisaDocumentStatusService visaDocumentStatusService = new VisaDocumentStatusService(visaDocumentStatusDao, visaDocumentsDao);
        String actualDocumentType = visaDocumentStatusService.getDocumentTypeById(1);

        assertThat(actualDocumentType).isEqualTo(expectedDocumentType);
    }

    @Test
    void getAllPendingVisaDocumentsTest() {
        List<VisaDocumentStatus> expectedVisaDocumentStatusList = new ArrayList<>();
        VisaDocumentStatus visaDocumentStatus1 = new VisaDocumentStatus();
        visaDocumentStatus1.setId(1);
        visaDocumentStatus1.setEmployeeId(123);
        visaDocumentStatus1.setFileId(1);
        visaDocumentStatus1.setPath("https://example.com");
        visaDocumentStatus1.setStatus("pending");

        VisaDocumentStatus visaDocumentStatus2 = new VisaDocumentStatus();
        visaDocumentStatus2.setId(2);
        visaDocumentStatus2.setEmployeeId(456);
        visaDocumentStatus2.setFileId(2);
        visaDocumentStatus2.setPath("https://example.org");
        visaDocumentStatus2.setStatus("pending");

        expectedVisaDocumentStatusList.add(visaDocumentStatus1);
        expectedVisaDocumentStatusList.add(visaDocumentStatus2);

        when(visaDocumentStatusDao.getAllPendingVisaDocuments()).thenReturn(expectedVisaDocumentStatusList);

        VisaDocumentStatusService visaDocumentStatusService = new VisaDocumentStatusService(visaDocumentStatusDao, visaDocumentsDao);
        List<VisaDocumentStatus> actualVisaDocumentStatusList = visaDocumentStatusService.getAllPendingVisaDocuments();

        assertThat(actualVisaDocumentStatusList).isEqualTo(expectedVisaDocumentStatusList);
    }



}

