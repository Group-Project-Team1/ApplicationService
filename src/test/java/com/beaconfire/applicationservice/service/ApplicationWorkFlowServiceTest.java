package com.beaconfire.applicationservice.service;

import com.beaconfire.applicationservice.dao.ApplicationWorkFlowDAO;
import com.beaconfire.applicationservice.domain.entity.ApplicationWorkFlow;
import com.beaconfire.applicationservice.repo.EmployeeRepo;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationWorkFlowServiceTest {

    @Mock
    private EmployeeRepo employeeRepo;

    @Mock
    private ApplicationWorkFlowDAO applicationWorkFlowDAO;

    @InjectMocks
    private ApplicationWorkFlowService applicationWorkFlowService;

    @Test
    public void test_updateApplicationStatus_shouldCallUpdateApplicationInDAO() {
        Integer employeeId = 1;

        applicationWorkFlowService.updateApplicationStatus(employeeId);

        verify(applicationWorkFlowDAO).updateApplication(employeeId);
    }

    @Test
    public void test_getApplicationsByStatus_shouldCallGetApplicationsByStatusInDAO() {
        String status = "pending";
        applicationWorkFlowService.getApplicationsByStatus(status);

        verify(applicationWorkFlowDAO).getApplicationsByStatus(status);
    }

    @Test(expected = NullPointerException.class)
    public void test_getApplicationByEmployeeId_shouldThrowNullPointerExceptionIfApplicationNotFound() {
        // Arrange
        Integer employeeId = 1;
        when(applicationWorkFlowDAO.getApplicationByEmployeeId(employeeId)).thenReturn(null);

        // Act
        applicationWorkFlowService.getApplicationByEmployeeId(employeeId);

        // Assert (expected exception)
    }

    @Test
    public void test_getApplicationByEmployeeId_shouldReturnApplicationFromDAOIfItExists() {
        Integer employeeId = 1;
        ApplicationWorkFlow applicationWorkFlow = new ApplicationWorkFlow();
        when(applicationWorkFlowDAO.getApplicationByEmployeeId(employeeId)).thenReturn(applicationWorkFlow);

        ApplicationWorkFlow result = applicationWorkFlowService.getApplicationByEmployeeId(employeeId);

        assertEquals(applicationWorkFlow, result);
    }
    @Test
    public void test_approveApplication_shouldCallApproveApplicationInDAO() {
        Integer applicationId = 1;

        applicationWorkFlowService.approveApplication(applicationId);

        verify(applicationWorkFlowDAO).approveApplication(applicationId);
    }

    @Test
    public void test_rejectApplication_shouldCallRejectApplicationInDAO() {
        Integer applicationId = 1;
        String feedback = "Not enough experience";

        applicationWorkFlowService.rejectApplication(applicationId, feedback);

        verify(applicationWorkFlowDAO).rejectApplication(applicationId, feedback);
    }


}
