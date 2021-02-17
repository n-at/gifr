package ru.doublebyte.gifr.struct;

public class VideoStreamInfoLegacy {

    private String codec;
    private int width;
    private int height;
    private String pixelFormat;
    private double framerate;
    private int bitrate;
    private double duration;

    ///////////////////////////////////////////////////////////////////////////

    public VideoStreamInfoLegacy() {

    }

    ///////////////////////////////////////////////////////////////////////////

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPixelFormat() {
        return pixelFormat;
    }

    public void setPixelFormat(String pixelFormat) {
        this.pixelFormat = pixelFormat;
    }

    public double getFramerate() {
        return framerate;
    }

    public void setFramerate(double framerate) {
        this.framerate = framerate;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}
