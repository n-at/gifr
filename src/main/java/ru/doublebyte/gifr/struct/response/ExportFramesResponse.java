package ru.doublebyte.gifr.struct.response;

public class ExportFramesResponse extends Response {

    private String id;
    private int framerate;
    private int frames;

    ///////////////////////////////////////////////////////////////////////////

    public static ExportFramesResponse success(String id, int framerate, int frames) {
        var response = new ExportFramesResponse();
        response.setSuccess(true);
        response.setMessage("ok");
        response.id = id;
        response.framerate = framerate;
        response.frames = frames;
        return response;
    }

    ///////////////////////////////////////////////////////////////////////////

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFramerate() {
        return framerate;
    }

    public void setFramerate(int framerate) {
        this.framerate = framerate;
    }

    public int getFrames() {
        return frames;
    }

    public void setFrames(int frames) {
        this.frames = frames;
    }
}
