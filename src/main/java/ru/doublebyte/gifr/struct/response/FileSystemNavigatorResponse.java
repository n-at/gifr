package ru.doublebyte.gifr.struct.response;

import ru.doublebyte.gifr.struct.FileSystemEntry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSystemNavigatorResponse extends Response {

    private String separator = File.separator;
    private String currentPath;
    private List<FileSystemEntry> entries = new ArrayList<>();

    ///////////////////////////////////////////////////////////////////////////

    public static FileSystemNavigatorResponse success(String currentPath, List<FileSystemEntry> entries) {
        var response = new FileSystemNavigatorResponse();
        response.setSuccess(true);
        response.setMessage("ok");
        response.currentPath = currentPath;
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

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }
}
