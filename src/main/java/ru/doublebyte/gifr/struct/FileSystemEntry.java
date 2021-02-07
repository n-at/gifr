package ru.doublebyte.gifr.struct;

public class FileSystemEntry {

    private Type type;
    private String name;
    private String fullPath;

    ///////////////////////////////////////////////////////////////////////////

    public FileSystemEntry() {

    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    ///////////////////////////////////////////////////////////////////////////

    public enum Type {
        File,
        Directory,
    }

}
