package ru.doublebyte.gifr.struct.mediainfo;

/**
 * Common media stream info
 */
public interface MediaStreamInfo {

    Integer getIndex();
    String getType();
    String getCodec();
    String getCodecDisplay();
    String getLanguage();
    String getTitle();
    Boolean isDefaultStream();

}
