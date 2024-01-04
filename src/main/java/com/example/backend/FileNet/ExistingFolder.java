package com.example.backend.FileNet;

public class ExistingFolder {
    private String Id;
    private String FolderName;

    public ExistingFolder(String id, String folderName) {
        Id = id;
        FolderName = folderName;
    }
    public ExistingFolder() {
    }

    public String getFolderName() {
        return FolderName;
    }

    public void setFolderName(String folderName) {
        FolderName = folderName;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
