package ru.doublebyte.gifr.components;

import ru.doublebyte.gifr.configuration.ExportParams;
import ru.doublebyte.gifr.struct.mediainfo.VideoFileInfo;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

public class GifExporter {

    private final TimeoutCommandlineExecutor timeoutCommandlineExecutor;
    private final ExportParams exportParams;

    public GifExporter(
            TimeoutCommandlineExecutor timeoutCommandlineExecutor,
            ExportParams exportParams
    ) {
        this.timeoutCommandlineExecutor = timeoutCommandlineExecutor;
        this.exportParams = exportParams;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Export given part of video file as gif
     *
     * @param videoFileInfo ...
     * @param start ...
     * @param end ...
     * @param framerate ...
     * @param size ...
     * @return ...
     */
    public InputStream export(VideoFileInfo videoFileInfo, double start, double end, int framerate, int size) {
        ensureOutputDirectoryExists();

        var videoFilePath = videoFileInfo.getPath();
        videoFilePath = videoFilePath.replaceAll("\"", "\\\"");
        videoFilePath = videoFilePath.replaceAll("\n", "\\\n");

        var outputPath = Paths.get(exportParams.getPath(), UUID.randomUUID().toString() + ".gif");

        final var commandline = "ffmpeg -hide_banner -y" + " " +
                String.format(Locale.US, "-ss %f", start) + " " +
                String.format(Locale.US, "-to %f", end) + " " +
                String.format("-i \"%s\"", videoFilePath) + " " +
                String.format("-vf \"fps=%d,scale=-1:%d:flags=lanczos\"", framerate, size) + " " +
                outputPath.toAbsolutePath().toString();

        try {
            timeoutCommandlineExecutor.execute(commandline);
            return Files.newInputStream(outputPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    private void ensureOutputDirectoryExists() {
        try {
            var path = Paths.get(exportParams.getPath());
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
