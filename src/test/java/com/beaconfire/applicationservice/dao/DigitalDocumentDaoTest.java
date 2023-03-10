package com.beaconfire.applicationservice.dao;

import com.beaconfire.applicationservice.domain.entity.DigitalDocument;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.After;
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
public class DigitalDocumentDaoTest {
    @Autowired
    private DigitalDocumentDAO digitalDocumentDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private Session session;

    @Before
    public void setUp() {
        session = sessionFactory.getCurrentSession();
        session.createQuery("DELETE FROM DigitalDocument").executeUpdate();
        // Insert test data
        session.save(new DigitalDocument(1, true, "OPT","file1.pdf", "pdf", "12345"));
        session.save(new DigitalDocument(2, true, "EAD", "file2.docx", "pdf", "67890"));
        session.save(new DigitalDocument(3, true, "STEM OPT", "file3.docx", "pdf", "67890"));
        session.flush();

    }


    @After
    public void tearDown() {
        // Delete test data
        session.createQuery("DELETE FROM DigitalDocument").executeUpdate();
    }

    @Test
    public void testGetDocuments() {
        List<DigitalDocument> documents = digitalDocumentDAO.getDocuments();
        assertNotNull(documents);
        assertEquals(3, documents.size());
    }
}
