package com.beaconfire.applicationservice.service;

import com.beaconfire.applicationservice.dao.VisaDocumentStatusDao;
import com.beaconfire.applicationservice.dao.VisaDocumentsDao;
import com.beaconfire.applicationservice.domain.entity.VisaDocumentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class VisaDocumentStatusService {
    private final VisaDocumentStatusDao visaDocumentStatusDao;
    private final VisaDocumentsDao visaDocumentsDao;
    @Autowired
    public VisaDocumentStatusService(VisaDocumentStatusDao visaDocumentStatusDao, VisaDocumentsDao visaDocumentsDao){
        this.visaDocumentStatusDao = visaDocumentStatusDao;
        this.visaDocumentsDao = visaDocumentsDao;
    }

    @Transactional
    public void createVisaDocumentStatusRecord(Integer employeeId, String url){
        visaDocumentStatusDao.createVisaDocumentStatusRecord(employeeId, url);
    }

    @Transactional
    public void updateVisaDocumentStatus(Integer employeeId, String status, Integer fileId){
        visaDocumentStatusDao.updateVisaDocumentStatus(employeeId, status, fileId);
    }

    @Transactional
    public void approveSubmittedDocument(Integer employeeId){
        int documentTotalNum = visaDocumentsDao.getDocumentsTotalNum();
        int currentDocumentId = visaDocumentStatusDao.getDocumentIdByEmployeeId(employeeId);
        if(currentDocumentId < documentTotalNum){
            visaDocumentStatusDao.updateVisaDocumentStatus(employeeId, "never submitted", currentDocumentId + 1);
        }else{
            visaDocumentStatusDao.updateVisaDocumentStatus(employeeId, "approved", -1);
        }
    }

    @Transactional
    public void rejectSubmittedDocument(Integer employeeId, String feedback){
        int currentDocumentId = visaDocumentStatusDao.getDocumentIdByEmployeeId(employeeId);
        visaDocumentStatusDao.updateVisaDocumentStatus(employeeId, "rejected", currentDocumentId);
        visaDocumentStatusDao.addRejectFeedback(employeeId, feedback);
    }

    @Transactional
    public VisaDocumentStatus getVisaDocumentStatusByEmployeeId(Integer employeeId){
        return visaDocumentStatusDao.getVisaDocumentStatusByEmployeeId(employeeId);
    }

    @Transactional
    public String getDocumentTypeById(Integer id){
        return visaDocumentsDao.getDocumentTypeById(id);
    }

}
