package com.beaconfire.applicationservice.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DigitalDocumentDAO {
    @Autowired
    SessionFactory sessionFactory;
}
