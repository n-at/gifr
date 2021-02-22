package ru.doublebyte.gifr.components;

import ru.doublebyte.gifr.configuration.ExportParams;
import ru.doublebyte.gifr.configuration.FFMPEGParams;
import ru.doublebyte.gifr.struct.CommandlineArguments;
import ru.doublebyte.gifr.struct.mediainfo.VideoFileInfo;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

public class GifExporter {

    private final CommandlineExecutor commandlineExecutor;
    private final FFMPEGParams ffmpegParams;
    private final ExportParams exportParams;

    public GifExporter(
            CommandlineExecutor commandlineExecutor,
            FFMPEGParams ffmpegParams,
            ExportParams exportParams
    ) {
        this.commandlineExecutor = commandlineExecutor;
        this.ffmpegParams = ffmpegParams;
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

        final var commandline =
                new CommandlineArguments(ffmpegParams.getFFMPEGBinary())
                .add("-hide_banner")
                .add("-y")
                .add("-ss", String.format(Locale.US, "%f", start))
                .add("-to", String.format(Locale.US, "%f", end))
                .add("-i", videoFilePath)
                .add("-vf", String.format("fps=%d,scale=-1:%d:flags=lanczos", framerate, size))
                .add(outputPath.toString());

        try {
            commandlineExecutor.execute(commandline);
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
