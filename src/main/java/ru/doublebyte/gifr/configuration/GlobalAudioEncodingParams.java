package ru.doublebyte.gifr.configuration;

public class GlobalAudioEncodingParams {

    private String codec = "aac";
    private String bitrate = "128k";
    private int channels = 2;
    private int sampleRate = 44100;

    ///////////////////////////////////////////////////////////////////////////

    public GlobalAudioEncodingParams() {

    }

    public String toEncoderOptions() {
        return String.format("-c:a %s -b:a %s -ac %d -ar %d",
                codec, bitrate, channels, sampleRate);
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
}
