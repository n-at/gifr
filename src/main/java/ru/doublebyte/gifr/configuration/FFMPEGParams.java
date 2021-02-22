package ru.doublebyte.gifr.configuration;

public class FFMPEGParams {

    private String ffmpegBinary = "ffmpeg";
    private String ffprobeBinary = "ffprobe";

    public FFMPEGParams() {
    }

    public String getFFMPEGBinary() {
        return ffmpegBinary;
    }

    public void setFFMPEGBinary(String ffmpegBinary) {
        this.ffmpegBinary = ffmpegBinary;
    }

    public String getFFProbeBinary() {
        return ffprobeBinary;
    }

    public void setFFProbeBinary(String ffprobeBinary) {
        this.ffprobeBinary = ffprobeBinary;
    }
}
