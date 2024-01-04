package com.example.backend.Controller;

import com.example.backend.FileNet.ExistingFolder;
import com.example.backend.FileNet.FileNetConnection;
import com.example.backend.FileNet.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.PropertyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
public class FileNetController {
    @Autowired
    private FileNetConnection fileNetConnection;

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("userJson") String userJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            User user = objectMapper.readValue(userJson, User.class);
            System.out.println(user);
            //Convert MultipartFile to Base64 String
            String base64Data = java.util.Base64.getEncoder().encodeToString(file.getBytes());
            System.out.println(base64Data);
            //Send Base64 data to FileNet
            fileNetConnection.CreateDynamic(base64Data, user);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @GetMapping("/{documentId}/properties")
    public ResponseEntity<User> fetchAndPrintDocumentProperties(@PathVariable String documentId) {
        User user = FileNetConnection.fetchAndPrintDocumentProperties(documentId);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/{folderId}/documents")
    public ResponseEntity<List<User>> getAllDocumentPropertiesInFolder(@PathVariable String folderId) {
        List<User> userList =FileNetConnection.listAllDocumentsInFolder(folderId);
        return ResponseEntity.ok(userList);
    }
    @DeleteMapping("/{documentId}/delete")
    public void deleteFile(@PathVariable String documentId){
        FileNetConnection.DeleteFile(documentId);
    }

    @PutMapping("/{documentId}/update")
    public void updateFile(@PathVariable String documentId, @RequestBody String userJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.readValue(userJson, User.class);

        FileNetConnection.UpdateFile(documentId,user);
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<InputStreamResource>  retrieveDocument(@PathVariable String documentId) {
        try {
            InputStream contentStream = fileNetConnection.SetDocumentFile(documentId);
            if (contentStream != null) {
                PropertyFilter pf = new PropertyFilter();
                pf.addIncludeProperty(new FilterElement(null,null,null,"DocumentTitle",null));
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", String.valueOf(pf));

                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(contentStream));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/changeFile/{folderId}")
        public void updateFile(@PathVariable String folderId,@RequestParam("file") MultipartFile file,@RequestParam String userJson){
        try {
            String base64Data = java.util.Base64.getEncoder().encodeToString(file.getBytes());
            ObjectMapper objectMapper = new ObjectMapper();
            User user = objectMapper.readValue(userJson, User.class);
            FileNetConnection.ChangeFileVersion(base64Data,folderId,user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/Target/{folderId}/{targetFolderPath}/{targetFolderName}")
    public void MoveFile(@PathVariable String folderId,@PathVariable String targetFolderPath,@PathVariable String targetFolderName){
       try{
           String correctedTargetFolderPath = "/" + targetFolderPath + "/";
        FileNetConnection.moveDocumentContent(folderId,correctedTargetFolderPath,targetFolderName);
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }
    @GetMapping("/Alldocuments/{folderId}")
    public ResponseEntity<List<ExistingFolder>> AllDocuments(@PathVariable String folderId){
        List<ExistingFolder> existingFolder= FileNetConnection.listAllDocuments(folderId);
        return ResponseEntity.ok(existingFolder);
    }


   /* @GetMapping("/filefindAll")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = null;
        try {
            users = fileNetConnection.getAllUsersFromFileNet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(users);
    }*/
    }


