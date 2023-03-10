package com.beaconfire.applicationservice.dao;

import com.beaconfire.applicationservice.domain.entity.VisaDocumentStatus;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class VisaDocumentsDaoTest {
    @Autowired
    private VisaDocumentsDao visaDocumentsDao;

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    public void testGetDocumentsTotalNum() {
        // Insert some data
        Session session = sessionFactory.getCurrentSession();
        VisaDocumentStatus doc1 = new VisaDocumentStatus(1,1,1,"Passport", "pdf","123");
        VisaDocumentStatus doc2 = new VisaDocumentStatus(2, 2, 2, "CV", "docx", "456");
        session.save(doc1);
        session.save(doc2);
        session.getTransaction().commit();

        // Test the method
        Integer totalNum = visaDocumentsDao.getDocumentsTotalNum();
        assertThat(totalNum).isEqualTo(6);

    }

    @Test
    public void testGetDocumentTypeById() {
        // Insert some data
        Session session = sessionFactory.getCurrentSession();
        VisaDocumentStatus doc1 = new VisaDocumentStatus(1,1,1,"Passport", "pdf","123");
        VisaDocumentStatus doc2 = new VisaDocumentStatus(2, 2, 2, "CV", "docx", "456");
        session.save(doc1);
        session.save(doc2);
        session.getTransaction().commit();

        // Test the method
        String fileType = visaDocumentsDao.getDocumentTypeById(doc2.getFileId());
        assertThat(fileType).isEqualTo("OPT EAD");
    }

}
