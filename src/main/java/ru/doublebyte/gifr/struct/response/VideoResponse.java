package ru.doublebyte.gifr.struct.response;

public class VideoResponse extends Response {

    private String url;
    private String id;

    ///////////////////////////////////////////////////////////////////////////

    public VideoResponse() {

    }

    public static VideoResponse success(String url, String videoFileId) {
        var response = new VideoResponse();
        response.setSuccess(true);
        response.setMessage("ok");
        response.url = url;
        response.id = videoFileId;
        return response;
    }

    ///////////////////////////////////////////////////////////////////////////

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
