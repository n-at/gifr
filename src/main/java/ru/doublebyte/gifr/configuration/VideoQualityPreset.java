package ru.doublebyte.gifr.configuration;

public enum VideoQualityPreset {

    Video360(360, 800, 856, 1200),
    Video430(432, 1400, 1498, 2100),
    Video540(540, 2000, 2140, 3500),
    Video720(720, 2800, 2996, 4200),
    Video1080(1080, 5000, 5350, 7500),

    ;

    private final int size;
    private final int bitrate;
    private final int maxrate;
    private final int bufsize;

    VideoQualityPreset(int size, int bitrate, int maxrate, int bufsize) {
        this.size = size;
        this.bitrate = bitrate;
        this.maxrate = maxrate;
        this.bufsize = bufsize;
    }

    public String toVideoEncodingOptions(int streamIdx, int presetIdx) {
        return String.format("-map 0:%d -filter:v:%d \"scale=-2:%d\" -b:v:%d %dk -maxrate:%d %dk -bufsize:%d %dk",
                streamIdx, presetIdx, size, presetIdx, bitrate, presetIdx, maxrate, presetIdx, bufsize);
    }

    public int getSize() {
        return size;
    }

    public int getBitrate() {
        return bitrate;
    }

    public int getMaxrate() {
        return maxrate;
    }

    public int getBufsize() {
        return bufsize;
    }
}
