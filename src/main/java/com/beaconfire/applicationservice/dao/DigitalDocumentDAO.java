package com.beaconfire.applicationservice.dao;

import com.beaconfire.applicationservice.domain.entity.ApplicationWorkFlow;
import com.beaconfire.applicationservice.domain.entity.DigitalDocument;
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

@Repository
public class DigitalDocumentDAO {
    @Autowired
    SessionFactory sessionFactory;

    public List<DigitalDocument> getDocuments(){
        Session session;
        List<DigitalDocument> results = new ArrayList<>();
        try{
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder cb = sessionFactory.getCriteriaBuilder();
            CriteriaQuery<DigitalDocument> cq = cb.createQuery(DigitalDocument.class);
            Root<DigitalDocument> root = cq.from(DigitalDocument.class);
            cq.select(root);
            results = session.createQuery(cq).getResultList();
        }catch (Exception e){
            e.printStackTrace();
        }
        return results.isEmpty() ? null : results;
    }

}
