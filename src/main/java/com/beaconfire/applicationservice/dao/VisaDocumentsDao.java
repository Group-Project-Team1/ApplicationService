package com.beaconfire.applicationservice.dao;

import com.beaconfire.applicationservice.domain.entity.ApplicationWorkFlow;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VisaDocumentsDao {
    @Autowired
    SessionFactory sessionFactory;

    public Integer getDocumentsTotalNum(){
        Session session;
        List<VisaDocument> result = new ArrayList<>();
        try{
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder cb= sessionFactory.getCriteriaBuilder();
            CriteriaQuery<VisaDocument> cq = cb.createQuery(VisaDocument.class);
            Root<VisaDocument> root = cq.from(VisaDocument.class);
            cq.select(root);
            result = session.createQuery(cq).getResultList();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result.size();
    }

    public String getDocumentTypeById(Integer id){
        Session session;
        Optional<VisaDocument> result = null;
        try{
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder cb= sessionFactory.getCriteriaBuilder();
            CriteriaQuery<VisaDocument> cq = cb.createQuery(VisaDocument.class);
            Root<VisaDocument> root = cq.from(VisaDocument.class);
            Predicate predicate = cb.equal(root.get("id"), id);
            cq.select(root).where(predicate);
            result = session.createQuery(cq).uniqueResultOptional();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result.isPresent() ? result.get().getFileType() : null;
    }

}
