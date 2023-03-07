package com.beaconfire.applicationservice.service;

import com.beaconfire.applicationservice.dao.ApplicationWorkFlowDAO;
import com.beaconfire.applicationservice.domain.entity.ApplicationWorkFlow;
import com.beaconfire.applicationservice.domain.entity.Employee;
import com.beaconfire.applicationservice.domain.entity.PersonalDocument;
import com.beaconfire.applicationservice.domain.request.ApplicationFormRequest;
import com.beaconfire.applicationservice.repo.EmployeeRepo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationWorkFlowService {

    private ApplicationWorkFlowDAO applicationWorkFlowDAO;
    private final EmployeeRepo repository;

    @Autowired
    public ApplicationWorkFlowService(EmployeeRepo repository, DigitalDocumentService digitalDocumentService) {
        this.repository = repository;
    }
    @Autowired
    public void setApplicationWorkFlowDAO(ApplicationWorkFlowDAO applicationWorkFlowDAO) {
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
            repository.save(employee);
        }
    }

    public void updateApplicationForm(Integer employeeId, ApplicationFormRequest applicationFormRequest){

        Employee employee = repository.findEmployeeByUserId(employeeId).get(0);
        employee.setFirstName(applicationFormRequest.getFirstName());
        employee.setLastName(applicationFormRequest.getLastName());
        employee.setMiddleName(applicationFormRequest.getMiddleName());
        employee.setPreferredName(applicationFormRequest.getPreferredName());
        employee.setAddress(applicationFormRequest.getAddress());
        employee.setCellPhoneNum(applicationFormRequest.getCellPhoneNum());
        employee.setWorkPhoneNum(applicationFormRequest.getWorkPhoneNum());
        employee.setGender(applicationFormRequest.getGender());
        employee.setDateOFBirth(applicationFormRequest.getDateOfBirth());
        employee.setSSN(applicationFormRequest.getSSN());
        employee.setIdentity(applicationFormRequest.getIdentity());
        employee.setVisaStatus(applicationFormRequest.getVisaStatus());
        employee.setDriverLicenseNumber(applicationFormRequest.getDriverLicenseNumber());
        employee.setExpirationDate(applicationFormRequest.getExpirationDate());
        employee.setReference(applicationFormRequest.getReference());
        employee.setContacts(applicationFormRequest.getContacts());

        repository.save(employee);
    }


    /* use when employee submit their entire application
       change application status from "never submitted" to "pending"
     */
    @Transactional
    public void updateApplicationStatus(Integer employeeId){
        applicationWorkFlowDAO.updateApplication(employeeId);
    }

    public Employee getEmployeeById (Integer id){
        return repository.findEmployeeByUserId(id).get(0);
    }

    @Transactional
    public List<ApplicationWorkFlow> getApplicationsByStatus(String status){
        return applicationWorkFlowDAO.getApplicationsByStatus(status);
    }

    @Transactional
    public ApplicationWorkFlow getApplicationById(Integer applicationId){
        return applicationWorkFlowDAO.getApplicationById(applicationId);
    }

    @Transactional
    public ApplicationWorkFlow getApplicationByEmployeeId(Integer employeeId){
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
