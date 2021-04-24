package ru.doublebyte.gifr.struct;

public class ExportFrames {

    private String id;
    private int frames;

    ///////////////////////////////////////////////////////////////////////////

    public ExportFrames() {
    }

    public ExportFrames(String id, int frames) {
        this.id = id;
        this.frames = frames;
    }

    ///////////////////////////////////////////////////////////////////////////

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFrames() {
        return frames;
    }

    public void setFrames(int frames) {
        this.frames = frames;
    }
}
