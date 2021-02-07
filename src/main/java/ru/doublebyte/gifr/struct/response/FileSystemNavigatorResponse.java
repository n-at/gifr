package ru.doublebyte.gifr.struct.response;

import ru.doublebyte.gifr.struct.FileSystemEntry;

import java.util.ArrayList;
import java.util.List;

public class FileSystemNavigatorResponse extends Response {

    private String currentPath;
    private String previousPath;
    private List<FileSystemEntry> entries = new ArrayList<>();

    ///////////////////////////////////////////////////////////////////////////

    public static FileSystemNavigatorResponse success(String currentPath, String previousPath, List<FileSystemEntry> entries) {
        var response = new FileSystemNavigatorResponse();
        response.setSuccess(true);
        response.setMessage("ok");
        response.currentPath = currentPath;
        response.previousPath = previousPath;
        response.entries = entries;
        return response;
    }

    ///////////////////////////////////////////////////////////////////////////

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public List<FileSystemEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<FileSystemEntry> entries) {
        this.entries = entries;
    }

    public String getPreviousPath() {
        return previousPath;
    }

    public void setPreviousPath(String previousPath) {
        this.previousPath = previousPath;
    }
}
