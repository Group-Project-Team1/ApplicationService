package com.beaconfire.applicationservice.service;

import com.beaconfire.applicationservice.dao.DigitalDocumentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DigitalDocumentService {

    private DigitalDocumentDAO digitalDocumentDAO;

    @Autowired
    public void setDigitalDocumentDAO(DigitalDocumentDAO digitalDocumentDAO) {
        this.digitalDocumentDAO = digitalDocumentDAO;
    }
}
