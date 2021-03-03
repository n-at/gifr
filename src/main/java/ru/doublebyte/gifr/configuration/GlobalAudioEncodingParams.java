package ru.doublebyte.gifr.configuration;

import ru.doublebyte.gifr.struct.CommandlineArguments;

public class GlobalAudioEncodingParams {

    private String codec = "aac";
    private String bitrate = "128k";
    private int channels = 2;
    private int sampleRate = 44100;
    private int encodingTimeout = 5;
    private int concurrentJobs = 2;
    private double overheadDuration = 1;

    ///////////////////////////////////////////////////////////////////////////

    public GlobalAudioEncodingParams() {

    }

    public CommandlineArguments toCommandlineArguments() {
        return new CommandlineArguments()
                .add("-c:a", codec)
                .add("-b:a", bitrate)
                .add("-ac", channels)
                .add("-ar", sampleRate);
    }

    ///////////////////////////////////////////////////////////////////////////

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
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

    public double getOverheadDuration() {
        return overheadDuration;
    }

    public void setOverheadDuration(double overheadDuration) {
        this.overheadDuration = overheadDuration;
    }
}
