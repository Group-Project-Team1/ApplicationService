package com.beaconfire.applicationservice.dao;

import com.beaconfire.applicationservice.domain.entity.VisaDocumentStatus;
import com.beaconfire.applicationservice.domain.entity.ApplicationWorkFlow;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ApplicationWorkFlowDAO {
    @Autowired
    SessionFactory sessionFactory;

    public void createNewApplication(Integer employeeId){
        Session session;
        try{
            session = sessionFactory.getCurrentSession();
            ApplicationWorkFlow applicationWorkFlow = new ApplicationWorkFlow();
            applicationWorkFlow.setEmployeeId(employeeId);
            applicationWorkFlow.setCreateDate(Timestamp.valueOf(LocalDateTime.now()));
            applicationWorkFlow.setStatus("never submitted");
            session.save(applicationWorkFlow);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateApplication(Integer employeeId){
        Session session;
        Optional<ApplicationWorkFlow> result = null;
        try{
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder cb= sessionFactory.getCriteriaBuilder();
            CriteriaQuery<ApplicationWorkFlow> cq = cb.createQuery(ApplicationWorkFlow.class);
            Root<ApplicationWorkFlow> root = cq.from(ApplicationWorkFlow.class);
            Predicate predicate = cb.equal(root.get("employeeId"), employeeId);
            cq.select(root).where(predicate);
            result = session.createQuery(cq).uniqueResultOptional();
            if(result.isPresent()){
                ApplicationWorkFlow applicationWorkFlow = result.get();
                applicationWorkFlow.setStatus("pending");
                applicationWorkFlow.setLastModificationDate(Timestamp.valueOf(LocalDateTime.now()));
                session.saveOrUpdate(applicationWorkFlow);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ApplicationWorkFlow getApplicationByEmployeeId(Integer employeeId){
        Session session;
        Optional<ApplicationWorkFlow> result = null;
        try{
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder cb= sessionFactory.getCriteriaBuilder();
            CriteriaQuery<ApplicationWorkFlow> cq = cb.createQuery(ApplicationWorkFlow.class);
            Root<ApplicationWorkFlow> root = cq.from(ApplicationWorkFlow.class);
            Predicate predicate = cb.equal(root.get("employeeId"), employeeId);
            cq.select(root).where(predicate);
            result = session.createQuery(cq).uniqueResultOptional();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result.isPresent() ? null : result.get();
    }

    public ApplicationWorkFlow getApplicationById(Integer applicationId){
        Session session;
        ApplicationWorkFlow applicationWorkFlow = new ApplicationWorkFlow();
        try{
            session = sessionFactory.getCurrentSession();
            applicationWorkFlow = session.get(ApplicationWorkFlow.class, applicationId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return applicationWorkFlow;
    }

    public List<ApplicationWorkFlow> getApplicationsByStatus(String status){
        Session session;
        List<ApplicationWorkFlow> result = new ArrayList<>();
        try{
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder cb= sessionFactory.getCriteriaBuilder();
            CriteriaQuery<ApplicationWorkFlow> cq = cb.createQuery(ApplicationWorkFlow.class);
            Root<ApplicationWorkFlow> root = cq.from(ApplicationWorkFlow.class);
            Predicate predicate = cb.equal(root.get("status"), status);
            cq.select(root).where(predicate);
            result = session.createQuery(cq).getResultList();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public void approveApplication(Integer applicationId){
        Session session;
        try{
            session = sessionFactory.getCurrentSession();
            ApplicationWorkFlow applicationWorkFlow = session.get(ApplicationWorkFlow.class, applicationId);
            applicationWorkFlow.setStatus("approved");
            session.update(applicationId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void rejectApplication(Integer applicationId, String feedback){
        Session session;
        try{
            session = sessionFactory.getCurrentSession();
            ApplicationWorkFlow applicationWorkFlow = session.get(ApplicationWorkFlow.class, applicationId);
            applicationWorkFlow.setStatus("rejected");
            applicationWorkFlow.setComment(feedback);
            session.update(applicationWorkFlow);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
