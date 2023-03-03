package com.beaconfire.applicationservice.service;

import com.beaconfire.applicationservice.dao.ApplicationWorkFlowDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationWorkFlowService {

    private ApplicationWorkFlowDAO applicationWorkFlowDAO;

    @Autowired
    public void setApplicationWorkFlowDAO(ApplicationWorkFlowDAO applicationWorkFlowDAO) {
        this.applicationWorkFlowDAO = applicationWorkFlowDAO;
    }
}
