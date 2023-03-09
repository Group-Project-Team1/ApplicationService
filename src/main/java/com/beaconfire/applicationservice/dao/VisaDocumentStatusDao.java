package com.beaconfire.applicationservice.dao;

import com.beaconfire.applicationservice.domain.entity.VisaDocument;
import com.beaconfire.applicationservice.domain.entity.VisaDocumentStatus;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VisaDocumentStatusDao {

    @Autowired
    SessionFactory sessionFactory;


    /**
     * create a new record when employee submit OPT Receipt
     * @param employeeId
     */
    public void createVisaDocumentStatusRecord(Integer employeeId, String url){
        Session session;
        try{
            session = sessionFactory.getCurrentSession();
            VisaDocumentStatus visaDocumentStatus = new VisaDocumentStatus();
            visaDocumentStatus.setFileId(1);
            visaDocumentStatus.setEmployeeId(employeeId);
            visaDocumentStatus.setStatus("pending");
            visaDocumentStatus.setPath(url);
            session.save(visaDocumentStatus);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateVisaDocumentStatus(Integer employeeId, String status, Integer fileId){
        Session session;
        try{
            session = sessionFactory.getCurrentSession();
            VisaDocumentStatus visaDocumentStatus = getVisaDocumentStatusByEmployeeId(employeeId);
            visaDocumentStatus.setStatus(status);
            if(fileId != -1){
                visaDocumentStatus.setFileId(fileId);
            }
            session.saveOrUpdate(visaDocumentStatus);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getDocumentIdByEmployeeId(Integer employeeId){
        Session session;
        int res = 0;
        try {
            session = sessionFactory.getCurrentSession();
            VisaDocumentStatus visaDocumentStatus = getVisaDocumentStatusByEmployeeId(employeeId);
            res = visaDocumentStatus.getFileId();
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public VisaDocumentStatus getVisaDocumentStatusByEmployeeId(Integer employeeId){
        Session session;
        Optional<VisaDocumentStatus> result = null;
        try{
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder cb= sessionFactory.getCriteriaBuilder();
            CriteriaQuery<VisaDocumentStatus> cq = cb.createQuery(VisaDocumentStatus.class);
            Root<VisaDocumentStatus> root = cq.from(VisaDocumentStatus.class);
            Predicate predicate = cb.equal(root.get("employeeId"), employeeId);
            cq.select(root).where(predicate);
            result = session.createQuery(cq).uniqueResultOptional();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result.isPresent() ? result.get() : null;

    }

    public void addRejectFeedback(Integer employeeId, String feedback){
        Session session;
        try{
            session = sessionFactory.getCurrentSession();
            VisaDocumentStatus visaDocumentStatus = getVisaDocumentStatusByEmployeeId(employeeId);
            visaDocumentStatus.setComment(feedback);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
