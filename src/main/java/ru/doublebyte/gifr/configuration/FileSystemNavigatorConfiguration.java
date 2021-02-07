package ru.doublebyte.gifr.configuration;

public class FileSystemNavigatorConfiguration {

    private String defaultStartDirectory = "~";

    public FileSystemNavigatorConfiguration() {

    }

    public String getDefaultStartDirectory() {
        return defaultStartDirectory;
    }

    public void setDefaultStartDirectory(String defaultStartDirectory) {
        this.defaultStartDirectory = defaultStartDirectory;
    }
}
