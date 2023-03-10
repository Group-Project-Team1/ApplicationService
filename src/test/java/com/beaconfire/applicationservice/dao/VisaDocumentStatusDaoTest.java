package com.beaconfire.applicationservice.dao;

import com.beaconfire.applicationservice.domain.entity.VisaDocumentStatus;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class VisaDocumentStatusDaoTest {
    @Autowired
    private VisaDocumentStatusDao visaDocumentStatusDao;

    @Autowired
    private SessionFactory sessionFactory;

    private Session session;

    @Before
    public void setUp() {
        session = sessionFactory.getCurrentSession();
    }

    @After
    public void tearDown() {
        session.getTransaction().rollback();
    }

    @Test
    public void testCreateVisaDocumentStatusRecord() {
//        session.createSQLQuery("truncate table visaDocumentStatus").executeUpdate();
        // Create a new VisaDocumentStatus record and save it
        visaDocumentStatusDao.createVisaDocumentStatusRecord(1, "https://example.com/file1");
        session.flush();
        session.clear();

        // Retrieve the record from the database and verify its properties
        VisaDocumentStatus visaDocumentStatus = visaDocumentStatusDao.getVisaDocumentStatusByEmployeeId(1);
        assertNotNull(visaDocumentStatus);
        assertEquals(1, visaDocumentStatus.getEmployeeId().intValue());
        assertEquals("pending", visaDocumentStatus.getStatus());
        assertEquals("https://example.com/file1", visaDocumentStatus.getPath());
    }

    @Test
    public void testUpdateVisaDocumentStatus() {
        // Create a new VisaDocumentStatus record and save it
        visaDocumentStatusDao.createVisaDocumentStatusRecord(1, "https://example.com/file1");
        session.flush();
        session.clear();

        // Update the record's status and save it
        visaDocumentStatusDao.updateVisaDocumentStatus(1, "approved", -1);
        session.flush();
        session.clear();

        // Retrieve the record from the database and verify its status has been updated
        VisaDocumentStatus visaDocumentStatus = visaDocumentStatusDao.getVisaDocumentStatusByEmployeeId(1);
        assertNotNull(visaDocumentStatus);
        assertEquals("approved", visaDocumentStatus.getStatus());
    }

    @Test
    public void testUpdateVisaDocumentStatusURL() {
        // Given
        Integer employeeId = 1;
        String url = "https://example.com/document2.pdf";
        visaDocumentStatusDao.createVisaDocumentStatusRecord(employeeId, "https://example.com/document.pdf");

        // When
        visaDocumentStatusDao.updateVisaDocumentStatusURL(employeeId, url);

        // Then
        VisaDocumentStatus visaDocumentStatus = visaDocumentStatusDao.getVisaDocumentStatusByEmployeeId(employeeId);
        assertNotNull(visaDocumentStatus);
        assertEquals(employeeId, visaDocumentStatus.getEmployeeId());
        assertEquals(url, visaDocumentStatus.getPath());
    }

    @Test
    public void testGetDocumentIdByEmployeeId() {
        // Given
        Integer employeeId = 1;
        Integer fileId = 2;
        visaDocumentStatusDao.createVisaDocumentStatusRecord(employeeId, "https://example.com/document.pdf");
        visaDocumentStatusDao.updateVisaDocumentStatus(employeeId, "approved", fileId);

        // When
        Integer result = visaDocumentStatusDao.getDocumentIdByEmployeeId(employeeId);

        // Then
        assertEquals(fileId, result);
    }

    @Test
    public void testAddRejectFeedback() {
        // Prepare test data
        Integer employeeId = 123;
        String feedback = "Document rejected due to incomplete information.";
        VisaDocumentStatus visaDocumentStatus = new VisaDocumentStatus();
        visaDocumentStatus.setEmployeeId(employeeId);
        visaDocumentStatus.setStatus("pending");
        visaDocumentStatus.setFileId(1);
        visaDocumentStatus.setPath("/path/to/document.pdf");
        visaDocumentStatusDao.createVisaDocumentStatusRecord(employeeId, visaDocumentStatus.getPath());

        // Execute test
        visaDocumentStatusDao.addRejectFeedback(employeeId, feedback);

        // Assert result
        VisaDocumentStatus updatedVisaDocumentStatus = visaDocumentStatusDao.getVisaDocumentStatusByEmployeeId(employeeId);
        assertEquals(feedback, updatedVisaDocumentStatus.getComment());
    }

    @Test
    public void testGetAllPendingVisaDocuments() {
        // Prepare test data
        Integer employeeId1 = 123;
        Integer employeeId2 = 456;
        VisaDocumentStatus visaDocumentStatus1 = new VisaDocumentStatus();
        visaDocumentStatus1.setEmployeeId(employeeId1);
        visaDocumentStatus1.setStatus("pending");
        visaDocumentStatus1.setFileId(1);
        visaDocumentStatus1.setPath("/path/to/document1.pdf");
        visaDocumentStatusDao.createVisaDocumentStatusRecord(employeeId1, visaDocumentStatus1.getPath());
        VisaDocumentStatus visaDocumentStatus2 = new VisaDocumentStatus();
        visaDocumentStatus2.setEmployeeId(employeeId2);
        visaDocumentStatus2.setStatus("approved");
        visaDocumentStatus2.setFileId(2);
        visaDocumentStatus2.setPath("/path/to/document2.pdf");
        visaDocumentStatusDao.createVisaDocumentStatusRecord(employeeId2, visaDocumentStatus2.getPath());

        // Execute test
        List<VisaDocumentStatus> pendingDocuments = visaDocumentStatusDao.getAllPendingVisaDocuments();

        // Assert result
        assertEquals(2, pendingDocuments.size());
        assertEquals(employeeId1, pendingDocuments.get(0).getEmployeeId());
    }


}
