package ru.doublebyte.gifr.configuration;

import ru.doublebyte.gifr.struct.CommandlineArguments;

public class GlobalVideoEncodingParams {

    private int framerate = 25;
    private String profile = "main";
    private String preset = "veryfast";
    private int keyframes = 200;
    private String codec = "libx264";
    private String pixelFormat = "yuv420p";
    private int encodingTimeout = 5;
    private int concurrentJobs = 2;

    ///////////////////////////////////////////////////////////////////////////

    public GlobalVideoEncodingParams() {

    }

    public CommandlineArguments toCommandlineArguments() {
        return new CommandlineArguments()
                .add("-r", framerate)
                .add("-preset", preset)
                .add("-profile:v", profile)
                .add("-keyint_min", keyframes)
                .add("-g", keyframes)
                .add("-sc_threshold", 0)
                .add("-c:v", codec)
                .add("-pix_fmt", pixelFormat);
    }

    ///////////////////////////////////////////////////////////////////////////

    public int getFramerate() {
        return framerate;
    }

    public void setFramerate(int framerate) {
        this.framerate = framerate;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getPreset() {
        return preset;
    }

    public void setPreset(String preset) {
        this.preset = preset;
    }

    public int getKeyframes() {
        return keyframes;
    }

    public void setKeyframes(int keyframes) {
        this.keyframes = keyframes;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public String getPixelFormat() {
        return pixelFormat;
    }

    public void setPixelFormat(String pixelFormat) {
        this.pixelFormat = pixelFormat;
    }

    public int getEncodingTimeout() {
        return encodingTimeout;
    }

    public void setEncodingTimeout(int encodingTimeout) {
        this.encodingTimeout = encodingTimeout;
    }

    public int getConcurrentJobs() {
        return concurrentJobs;
    }

    public void setConcurrentJobs(int concurrentJobs) {
        this.concurrentJobs = concurrentJobs;
    }
}
