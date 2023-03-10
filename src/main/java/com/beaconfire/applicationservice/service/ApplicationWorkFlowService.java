package com.beaconfire.applicationservice.service;

import com.beaconfire.applicationservice.dao.ApplicationWorkFlowDAO;
import com.beaconfire.applicationservice.domain.entity.ApplicationWorkFlow;
import com.beaconfire.applicationservice.domain.entity.Employee;
import com.beaconfire.applicationservice.domain.entity.PersonalDocument;
import com.beaconfire.applicationservice.domain.request.ApplicationFormRequest;
import com.beaconfire.applicationservice.repo.EmployeeRepo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationWorkFlowService {

    private ApplicationWorkFlowDAO applicationWorkFlowDAO;
    private final EmployeeRepo repository;

    @Autowired
    public ApplicationWorkFlowService(EmployeeRepo repository, ApplicationWorkFlowDAO applicationWorkFlowDAO) {
        this.repository = repository;
        this.applicationWorkFlowDAO = applicationWorkFlowDAO;
    }


    @Transactional
    public void createNewApplication(Integer employeeId){
        // create a new application workflow in Mysql
        applicationWorkFlowDAO.createNewApplication(employeeId);

        /* insert a new employee in mongoDB
            email? insert in AuthenticationService or here?
         */
        if(repository.findEmployeeByUserId(employeeId).size() == 0) {
            Employee employee = new Employee();
            employee.setUserId(employeeId);
            employee.setId(employeeId);
            repository.save(employee);
        }
    }


    /* use when employee submit their entire application
       change application status from "never submitted" to "pending"
     */
    @Transactional
    public void updateApplicationStatus(Integer employeeId){
        applicationWorkFlowDAO.updateApplication(employeeId);
    }


    @Transactional
    public List<ApplicationWorkFlow> getApplicationsByStatus(String status){
        return applicationWorkFlowDAO.getApplicationsByStatus(status);
    }


    @Transactional
    public ApplicationWorkFlow getApplicationByEmployeeId(Integer employeeId){
        ApplicationWorkFlow applicationWorkFlow = applicationWorkFlowDAO.getApplicationByEmployeeId(employeeId);
        if (applicationWorkFlow == null) {
            throw new NullPointerException("The employee has never submitted an application.");
        }
        return applicationWorkFlowDAO.getApplicationByEmployeeId(employeeId);
    }

    @Transactional
    public void approveApplication(Integer applicationId){
        applicationWorkFlowDAO.approveApplication(applicationId);
    }

    @Transactional
    public void rejectApplication(Integer applicationId, String feedback){
        applicationWorkFlowDAO.rejectApplication(applicationId, feedback);
    }


}
