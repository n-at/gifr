package ru.doublebyte.gifr.struct;

public class VideoFileInfo {

    private String path;
    private String checksum;
    private double duration;
    private VideoStreamInfoLegacy video;
    private AudioStreamInfo audio;

    ///////////////////////////////////////////////////////////////////////////

    public VideoFileInfo() {

    }

    /**
     * Duration formatted for DASH MPD file
     *
     * @return Formatted duration
     */
    public String getMpdDuration() {
        var totalSeconds = (int) duration;
        var milliseconds = (int) ((duration - totalSeconds) * 1000.0);
        var hours = (totalSeconds / 60) / 60;
        var minutes = (totalSeconds / 60) % 60;
        var seconds = totalSeconds % 60;
        return String.format("PT%dH%02dM%02d.%03dS", hours, minutes, seconds, milliseconds);
    }

    ///////////////////////////////////////////////////////////////////////////

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public VideoStreamInfoLegacy getVideo() {
        return video;
    }

    public void setVideo(VideoStreamInfoLegacy video) {
        this.video = video;
    }

    public AudioStreamInfo getAudio() {
        return audio;
    }

    public void setAudio(AudioStreamInfo audio) {
        this.audio = audio;
    }
}
