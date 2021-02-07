package ru.doublebyte.gifr.struct.response;

import ru.doublebyte.gifr.struct.VideoFileInfo;

public class VideoInfoResponse extends Response {

    private VideoFileInfo info;

    ///////////////////////////////////////////////////////////////////////////

    public VideoInfoResponse() {

    }

    public static VideoInfoResponse success(VideoFileInfo info) {
        var response = new VideoInfoResponse();
        response.setSuccess(true);
        response.setMessage("ok");
        response.info = info;
        return response;
    }

    ///////////////////////////////////////////////////////////////////////////

    public VideoFileInfo getInfo() {
        return info;
    }

    public void setInfo(VideoFileInfo info) {
        this.info = info;
    }

}
