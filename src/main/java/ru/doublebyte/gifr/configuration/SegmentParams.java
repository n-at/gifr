package ru.doublebyte.gifr.configuration;

public class SegmentParams {

    private String dashFileName = "dash.mpd";
    private String dashOutputPath = "dash";
    private String chunkOutputPath = "chunks";
    private double duration = 10;
    private double avFrameDuration = 0.32;
    private int lifetime = 120;
    private int encodingTimeout = 5;

    ///////////////////////////////////////////////////////////////////////////

    public SegmentParams() {

    }

    ///////////////////////////////////////////////////////////////////////////

    public String getDashFileName() {
        return dashFileName;
    }

    public void setDashFileName(String dashFileName) {
        this.dashFileName = dashFileName;
    }

    public String getDashOutputPath() {
        return dashOutputPath;
    }

    public void setDashOutputPath(String dashOutputPath) {
        this.dashOutputPath = dashOutputPath;
    }

    public String getChunkOutputPath() {
        return chunkOutputPath;
    }

    public void setChunkOutputPath(String chunkOutputPath) {
        this.chunkOutputPath = chunkOutputPath;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getAvFrameDuration() {
        return avFrameDuration;
    }

    public void setAvFrameDuration(double avFrameDuration) {
        this.avFrameDuration = avFrameDuration;
    }

    public int getLifetime() {
        return lifetime;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    public int getEncodingTimeout() {
        return encodingTimeout;
    }

    public void setEncodingTimeout(int encodingTimeout) {
        this.encodingTimeout = encodingTimeout;
    }
}
