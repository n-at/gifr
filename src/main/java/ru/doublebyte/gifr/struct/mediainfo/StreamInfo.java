package ru.doublebyte.gifr.struct.mediainfo;

public class StreamInfo implements VideoStreamInfo, AudioStreamInfo, SubtitlesStreamInfo {

    private Integer index;
    private String type; //codec_type
    private String codec;
    private String codecDisplay; //codec_long_name
    private String profile;
    private Integer sampleRate; //sample_rate
    private Integer channels;
    private String channelLayout; //channel_layout
    private Integer width;
    private Integer height;
    private String aspectRatio; //display_aspect_ratio
    private String pixelFormat; //pix_fmt
    private Double framerate; //r_frame_rate
    private Double averageFramerate; //avg_frame_rate
    private Double duration;
    private Integer bitrate; //bit_rate
    private Boolean defaultStream = false; //DISPOSITION:default
    private String language = "und"; //TAG:language
    private String title = "stream"; //TAG:title

    ///////////////////////////////////////////////////////////////////////////

    public StreamInfo() {

    }

    ///////////////////////////////////////////////////////////////////////////

    public boolean isVideo() {
        return "video".equals(type);
    }

    public boolean isAudio() {
        return "audio".equals(type);
    }

    public boolean isSubtitles() {
        return "subtitle".equals(type);
    }

    ///////////////////////////////////////////////////////////////////////////

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public String getCodecDisplay() {
        return codecDisplay;
    }

    public void setCodecDisplay(String codecDisplay) {
        this.codecDisplay = codecDisplay;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
    }

    public Integer getChannels() {
        return channels;
    }

    public void setChannels(Integer channels) {
        this.channels = channels;
    }

    public String getChannelLayout() {
        return channelLayout;
    }

    public void setChannelLayout(String channelLayout) {
        this.channelLayout = channelLayout;
    }

    public Double getFramerate() {
        return framerate;
    }

    public void setFramerate(Double framerate) {
        this.framerate = framerate;
    }

    public Double getAverageFramerate() {
        return averageFramerate;
    }

    public void setAverageFramerate(Double averageFramerate) {
        this.averageFramerate = averageFramerate;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Integer getBitrate() {
        return bitrate;
    }

    public void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }

    public Boolean isDefaultStream() {
        return defaultStream;
    }

    public void setDefaultStream(Boolean defaultStream) {
        this.defaultStream = defaultStream;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(String aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String getPixelFormat() {
        return pixelFormat;
    }

    public void setPixelFormat(String pixelFormat) {
        this.pixelFormat = pixelFormat;
    }
}
