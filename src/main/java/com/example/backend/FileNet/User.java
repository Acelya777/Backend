package com.example.backend.FileNet;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User  {
    @JsonProperty("Id")
    private String Id;
    @JsonProperty("DocumentTitle")
    private String documentTitle;
    @JsonProperty("prp_Ad")
    private String firstName;
    @JsonProperty("prp_soyisim")
    private String lastName;
    @JsonProperty("prp_tc")
    private String TC;
    @JsonProperty("prp_IrtibatKisisiEmail")
    private String mail;


    public String getId() {
        return Id;
    }

    public void setId(com.filenet.api.util.Id id) {
        Id = String.valueOf(id);
    }

    public User(String id, String documentTitle, String firstName, String lastName, String TC, String mail) {
        Id = id;
        this.documentTitle = documentTitle;
        this.firstName = firstName;
        this.lastName = lastName;
        this.TC = TC;
        this.mail = mail;
    }




    public User() {

    }


    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTC() {
        return TC;
    }

    public void setTC(String TC) {
        this.TC = TC;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
