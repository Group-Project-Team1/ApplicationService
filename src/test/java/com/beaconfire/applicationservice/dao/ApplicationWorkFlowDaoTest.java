package com.beaconfire.applicationservice.dao;

import com.beaconfire.applicationservice.domain.entity.ApplicationWorkFlow;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class ApplicationWorkFlowDaoTest {
    @Autowired
    private ApplicationWorkFlowDAO applicationWorkFlowDAO;

    @Autowired
    private SessionFactory sessionFactory;
    @Test
    public void testCreateNewApplication() {
        // Given
        Integer employeeId = 123;

        // When
        applicationWorkFlowDAO.createNewApplication(employeeId);

        // Then
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ApplicationWorkFlow> cq = cb.createQuery(ApplicationWorkFlow.class);
        Root<ApplicationWorkFlow> root = cq.from(ApplicationWorkFlow.class);
        Predicate predicate = cb.equal(root.get("employeeId"), employeeId);
        cq.select(root).where(predicate);
        List<ApplicationWorkFlow> result = session.createQuery(cq).getResultList();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("never submitted");
    }

    @Test
    public void testUpdateApplication() {
        // Given
        Integer employeeId = 123;
        applicationWorkFlowDAO.createNewApplication(employeeId);

        // When
        applicationWorkFlowDAO.updateApplication(employeeId);

        // Then
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ApplicationWorkFlow> cq = cb.createQuery(ApplicationWorkFlow.class);
        Root<ApplicationWorkFlow> root = cq.from(ApplicationWorkFlow.class);
        Predicate predicate = cb.equal(root.get("employeeId"), employeeId);
        cq.select(root).where(predicate);
        List<ApplicationWorkFlow> result = session.createQuery(cq).getResultList();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("pending");
    }

    @Test
    public void testGetApplicationByEmployeeId() {
        // Given
        Integer employeeId = 123;
        applicationWorkFlowDAO.createNewApplication(employeeId);

        // When
        ApplicationWorkFlow result = applicationWorkFlowDAO.getApplicationByEmployeeId(employeeId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("never submitted");
    }

    @Test
    public void testGetApplicationsByStatus() {
        // Given
        Integer employeeId1 = 123;
        Integer employeeId2 = 456;
        applicationWorkFlowDAO.createNewApplication(employeeId1);
        applicationWorkFlowDAO.createNewApplication(employeeId2);
        applicationWorkFlowDAO.updateApplication(employeeId2);

        // When
        List<ApplicationWorkFlow> result = applicationWorkFlowDAO.getApplicationsByStatus("pending");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmployeeId()).isEqualTo(employeeId2);
    }

    @Test
    public void testApproveApplication() {
        // Given
        Integer employeeId = 123;
        applicationWorkFlowDAO.createNewApplication(employeeId);
        ApplicationWorkFlow application = applicationWorkFlowDAO.getApplicationByEmployeeId(employeeId);

        // When
        applicationWorkFlowDAO.approveApplication(application.getId());

        // Then
        Session session = sessionFactory.getCurrentSession();
        ApplicationWorkFlow result = session.get(ApplicationWorkFlow.class, application.getId());
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("approved");
    }

    @Test
    public void testRejectApplication() {
        // Given
        Integer employeeId = 123;
        applicationWorkFlowDAO.createNewApplication(employeeId);
        ApplicationWorkFlow application = applicationWorkFlowDAO.getApplicationByEmployeeId(employeeId);

        // When
        applicationWorkFlowDAO.rejectApplication(application.getId(), "feedback");

        // Then
        Session session = sessionFactory.getCurrentSession();
        ApplicationWorkFlow result = session.get(ApplicationWorkFlow.class, application.getId());
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("rejected");
        assertThat(result.getComment()).isEqualTo("feedback");
    }

}
