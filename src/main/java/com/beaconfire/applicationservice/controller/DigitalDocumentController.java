package com.beaconfire.applicationservice.controller;

import com.beaconfire.applicationservice.domain.entity.DigitalDocument;
import com.beaconfire.applicationservice.domain.request.ApplicationFormRequest;
import com.beaconfire.applicationservice.service.DigitalDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;


@RestController
public class DigitalDocumentController {

    private final DigitalDocumentService digitalDocumentService;
    private static final String MESSAGE_1 = "Uploaded the file successfully";
    private static final String MESSAGE_2 = "Cancel file upload successfully";
    @Autowired
    public DigitalDocumentController(DigitalDocumentService digitalDocumentService){this.digitalDocumentService = digitalDocumentService;}


    /**
     * get all digital documents
     * @return
     */
    @GetMapping("/all/digitalDocuments")
    public List<DigitalDocument> getDocuments(){
        List<DigitalDocument> digitalDocumentList = digitalDocumentService.getDocuments();
        return digitalDocumentList;
    }


    /**
     * download digital file
     * @param fileTitle
     * @return
     */
    @GetMapping("/all/download/{fileTitle}")
    public ResponseEntity<Object> downloadFile(@PathVariable String fileTitle){
        byte[] fileContent = digitalDocumentService.downloadFile(fileTitle);
        return ResponseEntity.ok()
                .contentType(contentType(fileTitle))
                .header("Content-disposition", "attachment; filename=\"" + fileTitle + "\"")
                .body(fileContent);
    }

    /**
     * get file type(tool function)
     * @param fileName
     * @return
     */
    private MediaType contentType(String fileName) {
        String[] fileArrSplit = fileName.split("\\.");
        String fileExtension = fileArrSplit[fileArrSplit.length - 1];
        switch (fileExtension) {
            case "pdf":
                return MediaType.APPLICATION_PDF;
            case "txt":
                return MediaType.TEXT_PLAIN;
            case "png":
                return MediaType.IMAGE_PNG;
            case "jpg":
                return MediaType.IMAGE_JPEG;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }



    /**
     * upload a digital document
     * @param multipartFile
     * @return
     */
    @PostMapping("/employee/digitalDocuments")
    public String uploadFile(@RequestPart("file") MultipartFile multipartFile) {
        String fileName = digitalDocumentService.uploadFile(multipartFile);
        return fileName;
    }



    /**
     * cancel file upload
     * @param fileName
     * @return
     */
    @DeleteMapping("/employee/digitalDocuments")
    public ResponseEntity<Object> cancelUpload(@PathVariable String fileName){
        digitalDocumentService.deleteFileFromS3Bucket(fileName);
        return new ResponseEntity<>(MESSAGE_2, HttpStatus.OK);
    }



    /**
     * get presigned object url
     * @param
     */
    @GetMapping("/all/presignedURL/{fileName}")
    public String getFileUrl(@PathVariable String fileName){
        URL url = digitalDocumentService.getFileUrl(fileName);
        return url.toString();
    }

}
