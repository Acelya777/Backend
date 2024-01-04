package com.example.backend.Controller;



import com.example.backend.FileNet.FileNetConnection;
import com.example.backend.FileNet.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Base64;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

public class MainControl {
    @RequestMapping(value = "/getDocuments", method = RequestMethod.GET)
    public String getDocuments() {
        return "test";
    }
    /*@GetMapping ("/login")
    ResponseEntity<Object> handleAuthentication(@RequestHeader(name = "Authorization") String authorization) {

        if(authorization == null){
            ApiError error = new ApiError(401,"Unauthorized request","/login/1.0/auth");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);}
        log.info(authorization);
        return ResponseEntity.ok().build();
    }*/
   /* @RequestMapping("admin")
    @ResponseBody
    public String loginAdmin(){
        return "{\"message\": \"Login success\"}";
    }*/




   /* @RequestMapping("acelya")
    @ResponseBody
    public String acelya(){
       try{
           Connection os = new Connection();
           if(os != null){

               return os.toString();
           }else{
               return "FileNet bağlantısı sırasında bir hata oluştu";
           }
       } catch(Exception e){
           e.printStackTrace();
           return "FileNet bağlantısı sırasında bir hata oluştu: " + e.getMessage();
       }
    }*/

}