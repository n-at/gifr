package ru.doublebyte.gifr.struct.response;

public class VideoResponse extends Response {

    private String url;

    ///////////////////////////////////////////////////////////////////////////

    public VideoResponse() {

    }

    public static VideoResponse success(String url) {
        var response = new VideoResponse();
        response.setSuccess(true);
        response.setMessage("ok");
        response.url = url;
        return response;
    }

    ///////////////////////////////////////////////////////////////////////////

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
