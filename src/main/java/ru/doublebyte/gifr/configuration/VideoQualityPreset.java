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

    public String toVideoEncodingOptions(int index) {
        return String.format("-map v:0 -filter:v:%d \"scale=-2:%d\" -b:v:%d %dk -maxrate:%d %dk -bufsize:%d %dk",
                index, size, index, bitrate, index, maxrate, index, bufsize);
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
