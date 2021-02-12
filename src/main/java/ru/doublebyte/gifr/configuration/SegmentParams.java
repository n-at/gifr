package ru.doublebyte.gifr.configuration;

public class SegmentParams {

    private String dashFileName = "dash.mpd";
    private String dashOutputPath = "dash";
    private String chunkOutputPath = "chunks";
    private int duration = 10;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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
