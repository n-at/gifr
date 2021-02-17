package ru.doublebyte.gifr.struct;

public class AudioStreamInfoLegacy {

    private String codec;
    private int sampleRate;
    private int channels;
    private int bitrate;
    private double duration;

    ///////////////////////////////////////////////////////////////////////////

    public AudioStreamInfoLegacy() {

    }

    ///////////////////////////////////////////////////////////////////////////

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
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
