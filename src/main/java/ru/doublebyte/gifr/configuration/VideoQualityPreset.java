package ru.doublebyte.gifr.configuration;

import ru.doublebyte.gifr.struct.CommandlineArguments;

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

    public CommandlineArguments toCommandlineArguments() {
        return new CommandlineArguments()
                .add("-vf", String.format("scale=-2:%d", size))
                .add("-b:v", String.format("%dk", bitrate))
                .add("-maxrate", String.format("%dk", maxrate))
                .add("-bufsize", String.format("%dk", bufsize));
    }

    public CommandlineArguments toCommandlineArguments(int presetIdx) {
        return new CommandlineArguments()
                .add(String.format("-filter:v:%d", presetIdx), String.format("scale=-2:%d", size))
                .add(String.format("-b:v:%d", presetIdx), String.format("%dk", bitrate))
                .add(String.format("-maxrate:%d", presetIdx), String.format("%dk", maxrate))
                .add(String.format("-bufsize:%d", presetIdx), String.format("%dk", bufsize));
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
