package com.beaconfire.applicationservice.controller;

import com.beaconfire.applicationservice.domain.entity.ApplicationWorkFlow;
import com.beaconfire.applicationservice.domain.entity.DigitalDocument;
import com.beaconfire.applicationservice.domain.entity.Employee;
import com.beaconfire.applicationservice.domain.entity.VisaDocumentStatus;
import com.beaconfire.applicationservice.domain.request.ApplicationFormRequest;
import com.beaconfire.applicationservice.domain.response.ApplicationResponse;
import com.beaconfire.applicationservice.domain.response.PendingApplicationResponse;
import com.beaconfire.applicationservice.domain.response.RejectedApplicationResponse;
import com.beaconfire.applicationservice.domain.response.VisaStatusManagementResponse;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
     * @param applicationFormRequest
     * @return
     */
    @PostMapping("/{employeeId}/applicationForm")
    @ApiOperation(value = "update employee", response = Employee.class)
    public ResponseEntity<Object> submitApplicationForm(@PathVariable Integer employeeId,
                                                        @RequestPart("applicationFormRequest") ApplicationFormRequest applicationFormRequest,
                                                        @RequestPart("driverLicense") MultipartFile driverLicense,
                                                        @RequestPart("OPTReceipt") MultipartFile OPTReceipt){

        // update employee info in mongoDB
        applicationWorkFlowService.updateApplicationForm(employeeId, applicationFormRequest);

        // upload driverLicense and update personalDocuments in mongoDB
        String driverLicenseFileName = digitalDocumentService.uploadFile(driverLicense);
        digitalDocumentService.updatePersonalDocuments(employeeId, driverLicenseFileName, "driver License");

        //upload OPT Receipt and update personalDocuments in mongoDB
        String OPTFileName = digitalDocumentService.uploadFile(OPTReceipt);
        digitalDocumentService.updatePersonalDocuments(employeeId, OPTFileName, "OPT Receipt");

        //create a record in visaDocumentStatus table
        String OPTReceiptURL = digitalDocumentService.getFileUrl(OPTFileName).toString();
        visaDocumentStatusService.createVisaDocumentStatusRecord(employeeId, OPTReceiptURL);

        return new ResponseEntity<>("Application form submitted successfully", HttpStatus.OK);

    }

    /**
     * documents and submit the entire application
     * @param employeeId
     * @param files
     * @return
     */
    @PostMapping("/{employeeId}/application")
    public ResponseEntity<Object> submitApplication(@PathVariable Integer employeeId, @RequestParam("files") List<MultipartFile> files){
        List<DigitalDocument> digitalDocumentList = digitalDocumentService.getDocuments();


        /* upload documents and update personalDocuments in mongoDB
           upload together or upload separately ?
         */
        for(int i = 0; i < digitalDocumentList.size(); i++){
            String fileName = digitalDocumentService.uploadFile(files.get(i));
            String fileTitle = digitalDocumentList.get(i).getTitle();
            digitalDocumentService.updatePersonalDocuments(employeeId, fileName, fileTitle);
        }

        // update application status
        applicationWorkFlowService.updateApplicationStatus(employeeId);

        return new ResponseEntity<>("Pleas wait for HR to review your application.", HttpStatus.OK);
    }


    /**
     * employee checks his/her pending application
     * @param employeeId
     * @return
     */
    @GetMapping("/{employeeId}/pendingApplication")
    public PendingApplicationResponse getPendingApplication(@PathVariable Integer employeeId){
        Employee employee = applicationWorkFlowService.getEmployeeById(employeeId);
        return PendingApplicationResponse.builder()
                .message("Please wait for HR to review your application.")
                .applicationDerail(employee)
                .build();

    }

    /**
     * employee checks his/her rejected application
     * @param employeeId
     * @return
     */
    @GetMapping("{employeeId}/rejectedApplication")
    public RejectedApplicationResponse getRejectedApplication(@PathVariable Integer employeeId){
        Employee employee = applicationWorkFlowService.getEmployeeById(employeeId);
        ApplicationWorkFlow applicationWorkFlow = applicationWorkFlowService.getApplicationByEmployeeId(employeeId);
        return RejectedApplicationResponse.builder()
                .feedback(applicationWorkFlow.getComment())
                .message("Successfully get the detail of your rejected application.")
                .applicationDerail(employee)
                .build();
    }

    /**
     * create a new application after registration
     * @param employeeId
     * @return
     */
    @PostMapping("/createApplication/{employeeId}")
    public ResponseEntity<Object> createNewApplication(@PathVariable Integer employeeId){
        applicationWorkFlowService.createNewApplication(employeeId);
        return new ResponseEntity<>("Successfully create a new application.", HttpStatus.OK);
    }


    /**
     * hr checks applications by status
     * @param status
     * @return
     */
    @GetMapping("/{status}/applications")
    public ResponseEntity<Object> getApplicationsByStatus(@PathVariable String status){
        List<ApplicationWorkFlow> applications = applicationWorkFlowService.getApplicationsByStatus(status);
        return ResponseEntity.ok().body(applications);
    }


    /**
     * hr views application
     * @param applicationId
     * @return
     */
    @GetMapping("/viewApplication/{applicationId}")
    public ApplicationResponse viewApplication(@PathVariable Integer applicationId){
        ApplicationWorkFlow applicationWorkFlow = applicationWorkFlowService.getApplicationById(applicationId);
        Employee employee = applicationWorkFlowService.getEmployeeById(applicationWorkFlow.getEmployeeId());
        return ApplicationResponse.builder()
                .applicationId(applicationId)
                .applicationStatus(applicationWorkFlow.getStatus())
                .applicationDerail(employee)
                .build();
    }


    /**
     * hr approve an application
     * @param applicationId
     * @return
     */
    @PostMapping("/viewApplication/{applicationId}")
    public ResponseEntity<Object> approveApplication(@PathVariable Integer applicationId){
        ApplicationWorkFlow applicationWorkFlow = applicationWorkFlowService.getApplicationById(applicationId);
        if(!applicationWorkFlow.getStatus().equals("pending")){
            throw new approveApplicationFailedException();
        }
        applicationWorkFlowService.approveApplication(applicationWorkFlow.getId());
        return new ResponseEntity<>("This application is successfully approved.", HttpStatus.OK);
    }


    /**
     * hr rejects an application and gives feedback
     * @param applicationId
     * @param feedback
     * @return
     */
    @PostMapping("/rejectApplication/{applicationId}")
    public ResponseEntity<Object> rejectApplication(@PathVariable Integer applicationId, @RequestParam String feedback){
        ApplicationWorkFlow applicationWorkFlow = applicationWorkFlowService.getApplicationById(applicationId);
        if(!applicationWorkFlow.getStatus().equals("pending")){
            throw new rejectApplicationFailedException();
        }
        applicationWorkFlowService.rejectApplication(applicationId, feedback);
        return new ResponseEntity<>("This application is successfully rejected.", HttpStatus.OK);

        //send email to employee
    }


    // visa status management part

    /**
     * employee get his/her visaStatusManagement page
     * @param employeeId
     * @return
     */
    @GetMapping("{employeeId}/visaStatusManagement")
    public VisaStatusManagementResponse getVisaStatus(@PathVariable Integer employeeId){
        VisaDocumentStatus visaDocumentStatus = visaDocumentStatusService.getVisaDocumentStatusByEmployeeId(employeeId);
        String status = visaDocumentStatus.getStatus();
        if(status.equals("approved")){
            return VisaStatusManagementResponse.builder().message("All documents have been approved.").build();
        }
        else if(status.equals("rejected")){
            ApplicationWorkFlow applicationWorkFlow = applicationWorkFlowService.getApplicationByEmployeeId(employeeId);
            String feedback = applicationWorkFlow.getComment();
            return VisaStatusManagementResponse.builder().message(feedback).build();
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
     * @param file
     * @param fileId
     * @param fileType
     * @return
     */
    @PostMapping("/{employeeId}/visaStatusManagement")
    public ResponseEntity<Object> submitVisaDocuments(@PathVariable Integer employeeId,
                                                      @RequestPart("file") MultipartFile file,
                                                      @RequestPart("fileId") Integer fileId,
                                                      @RequestPart("fileType") String fileType){
        String fileName = digitalDocumentService.uploadFile(file);
        digitalDocumentService.updatePersonalDocuments(employeeId, fileName, fileType);
        visaDocumentStatusService.updateVisaDocumentStatus(employeeId, "pending", fileId);
        return new ResponseEntity<>("Waiting for HR to approve your " + fileType, HttpStatus.OK);
    }


    /**
     * Hr reviews visa documents, approve or reject
     * @param employeeId
     * @param action
     * @param feedback
     * @return
     */
    @PostMapping("visaDocuments/{employeeId}")
    public ResponseEntity<Object> reviewVisaDocuments(@PathVariable Integer employeeId,
                                                      @RequestParam String action,
                                                      @RequestParam String feedback){
        if(action.equals("approve")){

            // For approving an application, no need to add feedback, so feedback will be null
            visaDocumentStatusService.approveSubmittedDocument(employeeId);
            return new ResponseEntity<>("This file has been successfully approved.", HttpStatus.OK);

        }else if(action.equals("reject")){

            visaDocumentStatusService.rejectSubmittedDocument(employeeId);

            // When you reject a document, you reject the application simultaneously
            applicationWorkFlowService.rejectApplication(employeeId, feedback);
            return new ResponseEntity<>("This document has been successfully rejected as well as the application.", HttpStatus.OK);
        }
        return null;
    }


}
