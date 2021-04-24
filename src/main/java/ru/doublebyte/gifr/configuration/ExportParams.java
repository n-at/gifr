package ru.doublebyte.gifr.configuration;

public class ExportParams {

    private String path = "export";
    private int framesCompressionLevel = 100;
    private int framesPreviewQuality = 12;
    private int framesPreviewSize = 240;

    ///////////////////////////////////////////////////////////////////////////

    public ExportParams() {

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getFramesCompressionLevel() {
        return framesCompressionLevel;
    }

    public void setFramesCompressionLevel(int framesCompressionLevel) {
        this.framesCompressionLevel = framesCompressionLevel;
    }

    public int getFramesPreviewQuality() {
        return framesPreviewQuality;
    }

    public void setFramesPreviewQuality(int framesPreviewQuality) {
        this.framesPreviewQuality = framesPreviewQuality;
    }

    public int getFramesPreviewSize() {
        return framesPreviewSize;
    }

    public void setFramesPreviewSize(int framesPreviewSize) {
        this.framesPreviewSize = framesPreviewSize;
    }
}
