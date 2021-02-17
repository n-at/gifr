package ru.doublebyte.gifr.struct.mediainfo;

/**
 * Video specific info
 */
public interface VideoStreamInfo extends MediaStreamInfo {

    String getProfile();
    Integer getWidth();
    Integer getHeight();
    String getAspectRatio();
    String getPixelFormat();
    Double getFramerate();
    Double getAverageFramerate();
    Double getDuration();
    Integer getBitrate();

}
