package com.beaconfire.applicationservice.controller;

import com.beaconfire.applicationservice.domain.entity.ApplicationWorkFlow;
import com.beaconfire.applicationservice.domain.entity.DigitalDocument;
import com.beaconfire.applicationservice.domain.entity.Employee;
import com.beaconfire.applicationservice.domain.entity.VisaDocumentStatus;
import com.beaconfire.applicationservice.domain.response.VisaStatusManagementResponse;
import com.beaconfire.applicationservice.exception.CannotAccessOtherUsersDataException;
import com.beaconfire.applicationservice.exception.approveApplicationFailedException;
import com.beaconfire.applicationservice.exception.rejectApplicationFailedException;
import com.beaconfire.applicationservice.service.ApplicationWorkFlowService;
import com.beaconfire.applicationservice.service.DigitalDocumentService;
import com.beaconfire.applicationservice.service.VisaDocumentStatusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@Api(value = "MongoDemoController RESTful endpoints")
@Controller
public class ApplicationController {
    private final ApplicationWorkFlowService applicationWorkFlowService;
    private final DigitalDocumentService digitalDocumentService;
    private final VisaDocumentStatusService visaDocumentStatusService;
    @Autowired
    public ApplicationController(ApplicationWorkFlowService applicationWorkFlowService, DigitalDocumentService digitalDocumentService, VisaDocumentStatusService visaDocumentStatusService){
        this.applicationWorkFlowService = applicationWorkFlowService;
        this.digitalDocumentService = digitalDocumentService;
        this.visaDocumentStatusService = visaDocumentStatusService;
    }

    /**
     * submit application form
     * @param employeeId
     * @param OPTReceiptURL
     */
    @PostMapping("/employee/{employeeId}/applicationForm")
    @ApiOperation(value = "update employee", response = Employee.class)
    @PreAuthorize("hasAuthority('employee')")
    public void submitApplicationForm(@PathVariable Integer employeeId,
                                      @RequestParam("OPTReceiptURL") String OPTReceiptURL){
/*
        // update employee info in mongoDB
        applicationWorkFlowService.updateApplicationForm(employeeId, applicationFormRequest);

        // upload driverLicense and update personalDocuments in mongoDB
        String driverLicenseFileName = digitalDocumentService.uploadFile(driverLicense);
        digitalDocumentService.updatePersonalDocuments(employeeId, driverLicenseFileName, "driver License");

        // upload OPT Receipt and update personalDocuments in mongoDB
        String OPTFileName = digitalDocumentService.uploadFile(OPTReceipt);
        digitalDocumentService.updatePersonalDocuments(employeeId, OPTFileName, "OPT Receipt");
*/

        //create a record in visaDocumentStatus table
//        String OPTReceiptURL = digitalDocumentService.getFileUrl(OPTFileName).toString();
        visaDocumentStatusService.createVisaDocumentStatusRecord(employeeId, OPTReceiptURL);

    }

    /**
     * upload documents and submit the entire application
     * @param employeeId
     */
    @PostMapping("/employee/{employeeId}/application")
    @PreAuthorize("hasAuthority('employee')")
    public void submitApplication(@PathVariable Integer employeeId){

        /* upload documents and update personalDocuments in mongoDB
           upload together or upload separately ?
         */
//        for(int i = 0; i < digitalDocumentList.size(); i++){
//            String fileName = digitalDocumentService.uploadFile(files.get(i));
//            String fileTitle = digitalDocumentList.get(i).getTitle();
//            digitalDocumentService.updatePersonalDocuments(employeeId, fileName, fileTitle);
//        }

        // update application status
        applicationWorkFlowService.updateApplicationStatus(employeeId);

    }


    @GetMapping("/all/{employeeId}/application")
    public ApplicationWorkFlow getApplicationByEmployeeId(@PathVariable Integer employeeId){
//        int userId = (int) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if(userId != 1 && userId != employeeId){
//            throw new CannotAccessOtherUsersDataException("You cannot view the application of other employee.");
//        }

        ApplicationWorkFlow applicationWorkFlow = applicationWorkFlowService.getApplicationByEmployeeId(employeeId);
        if(applicationWorkFlow == null) System.out.println("null");
        else System.out.println(applicationWorkFlow.getId());
        return applicationWorkFlow;
    }


    /**
     * create a new application after registration
     * @param employeeId
     * @return
     */
    @PostMapping("/createApplication/{employeeId}")
    public ResponseEntity<Object> createNewApplication(@PathVariable Integer employeeId){
        System.out.println(2.75);
        applicationWorkFlowService.createNewApplication(employeeId);
        return new ResponseEntity<>("Successfully create a new application.", HttpStatus.OK);
    }


    /**
     * hr checks applications by status
     * @param status
     * @return
     */
    @GetMapping("/hr/{status}/applications")
    @PreAuthorize("hasAuthority('hr')")
    public List<ApplicationWorkFlow> getApplicationsByStatus(@PathVariable String status){
        List<ApplicationWorkFlow> applications = applicationWorkFlowService.getApplicationsByStatus(status);
        return applications;
    }



    /**
     * hr reviews an application
     * @param employeeId
     * @return
     */
    @PostMapping("/hr/viewApplication/{employeeId}")
    @PreAuthorize("hasAuthority('hr')")
    public ResponseEntity<Object> reviewApplication(@PathVariable Integer employeeId, @RequestParam String action, @RequestParam String feedback){
        ApplicationWorkFlow applicationWorkFlow = applicationWorkFlowService.getApplicationByEmployeeId(employeeId);
        if(action.equals("approve")){
            if(!applicationWorkFlow.getStatus().equals("pending")){
                throw new approveApplicationFailedException();
            }
            applicationWorkFlowService.approveApplication(applicationWorkFlow.getId());
            return new ResponseEntity<>("This application has been successfully approved.", HttpStatus.OK);
        }else{
            if(!applicationWorkFlow.getStatus().equals("pending")){
                throw new rejectApplicationFailedException();
            }
            applicationWorkFlowService.rejectApplication(employeeId, feedback);
            //send email to employee
            return new ResponseEntity<>("This application has been successfully rejected.", HttpStatus.OK);

        }

    }


    // visa status management part

    /**
     * employee gets his/her visaStatusManagement page
     * @param employeeId
     * @return
     */
    @GetMapping("/employee/{employeeId}/visaStatusManagement")
    @PreAuthorize("hasAuthority('employee')")
    public VisaStatusManagementResponse getVisaStatus(@PathVariable Integer employeeId){
//        int userId = (int) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if(userId != employeeId){
//            throw new CannotAccessOtherUsersDataException("You cannot view the visa status management page of other employee.");
//        }
        VisaDocumentStatus visaDocumentStatus = visaDocumentStatusService.getVisaDocumentStatusByEmployeeId(employeeId);
        String status = visaDocumentStatus.getStatus();
        if(status.equals("approved")){
            return VisaStatusManagementResponse.builder().message("All documents have been approved.").build();
        }
        else if(status.equals("rejected")){
            String feedback = visaDocumentStatus.getComment();
            String fileType = visaDocumentStatusService.getDocumentTypeById(visaDocumentStatus.getFileId());
            return VisaStatusManagementResponse.builder().message(feedback + " Please reupload your " + fileType).build();
        }
        else if(status.equals("never submitted")){
            String fileType = visaDocumentStatusService.getDocumentTypeById(visaDocumentStatus.getFileId());
            return VisaStatusManagementResponse.builder().message("Please upload your " + fileType).build();
        }
        else{
            String fileType = visaDocumentStatusService.getDocumentTypeById(visaDocumentStatus.getFileId());
            return VisaStatusManagementResponse.builder().message("Waiting for HR to approve your " + fileType).build();
        }
    }

    /**
     * employee uploads visa document
     * @param employeeId
     * @param fileId
     * @return
     */
    @PostMapping("/employee/{employeeId}/visaStatusManagement")
    @PreAuthorize("hasAuthority('employee')")
    public void submitVisaDocuments(@PathVariable Integer employeeId,
                                    @RequestParam("fileId") Integer fileId
                                    /* @RequestPart("file") MultipartFile file,
                                    @RequestPart("fileType") String fileType */){
//        String fileName = digitalDocumentService.uploadFile(file);
//        digitalDocumentService.updatePersonalDocuments(employeeId, fileName, fileType);
        visaDocumentStatusService.updateVisaDocumentStatus(employeeId, "pending", fileId);
    }


    /**
     * Hr reviews visa documents, approve or reject
     * @param employeeId
     * @param action
     * @param feedback
     * @return
     */
    @PostMapping("/hr/visaDocuments/{employeeId}")
    @PreAuthorize("hasAuthority('hr')")
    public ResponseEntity<Object> reviewVisaDocuments(@PathVariable Integer employeeId,
                                                      @RequestParam String action,
                                                      @RequestParam String feedback){
        if(action.equals("approve")){

            // For approving an application, no need to add feedback, so feedback will be null
            visaDocumentStatusService.approveSubmittedDocument(employeeId);
            return new ResponseEntity<>("This file has been successfully approved.", HttpStatus.OK);

        }else if(action.equals("reject")){

            visaDocumentStatusService.rejectSubmittedDocument(employeeId, feedback);
            return new ResponseEntity<>("This document has been successfully rejected.", HttpStatus.OK);
        }
        return null;
    }


}
