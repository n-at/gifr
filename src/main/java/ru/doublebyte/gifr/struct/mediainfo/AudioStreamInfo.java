package ru.doublebyte.gifr.struct.mediainfo;

public interface AudioStreamInfo extends MediaStreamInfo {

    Integer getSampleRate();
    Integer getChannels();
    String getChannelLayout();
    Double getDuration();
    Integer getBitrate();

}
