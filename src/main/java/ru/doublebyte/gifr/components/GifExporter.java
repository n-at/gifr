package ru.doublebyte.gifr.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.doublebyte.gifr.configuration.ExportParams;
import ru.doublebyte.gifr.configuration.FFMPEGParams;
import ru.doublebyte.gifr.struct.CommandlineArguments;
import ru.doublebyte.gifr.struct.ExportFrames;
import ru.doublebyte.gifr.struct.mediainfo.VideoFileInfo;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.Locale;
import java.util.UUID;

public class GifExporter {

    private static final Logger logger = LoggerFactory.getLogger(GifExporter.class);

    private final CommandlineExecutor commandlineExecutor;
    private final FFMPEGParams ffmpegParams;
    private final ExportParams exportParams;
    private final FileManipulation fileManipulation;

    public GifExporter(
            CommandlineExecutor commandlineExecutor,
            FFMPEGParams ffmpegParams,
            ExportParams exportParams,
            FileManipulation fileManipulation
    ) {
        this.commandlineExecutor = commandlineExecutor;
        this.ffmpegParams = ffmpegParams;
        this.exportParams = exportParams;
        this.fileManipulation = fileManipulation;
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
        var exportId = UUID.randomUUID().toString();
        var outputPath = fileManipulation.getOutputGifFilePath(exportId);

        final var commandline = new CommandlineArguments(ffmpegParams.getFFMPEGBinary())
                .add("-hide_banner")
                .add("-y")
                .add("-ss", String.format(Locale.US, "%f", start))
                .add("-to", String.format(Locale.US, "%f", end))
                .add("-i", videoFileInfo.getPath())
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

    /**
     * Export given part of video file as single PNG frames and JPEG previews
     *
     * @param videoFileInfo ...
     * @param start ...
     * @param end ...
     * @param framerate ...
     * @param size ...
     * @return ...
     */
    public ExportFrames exportFrames(VideoFileInfo videoFileInfo, double start, double end, int framerate, int size) {
        var exportId = UUID.randomUUID().toString();

        //explode video into frames
        final var commandline = new CommandlineArguments(ffmpegParams.getFFMPEGBinary())
                .add("-hide_banner")
                .add("-y")
                .add("-ss", String.format(Locale.US, "%f", start))
                .add("-to", String.format(Locale.US, "%f", end))
                .add("-i", videoFileInfo.getPath())
                .add("-vf", String.format("fps=%d,scale=-1:%d:flags=lanczos", framerate, size))
                .add("-compression_level", exportParams.getFramesCompressionLevel())
                .add("-start_number", 0)
                .add(fileManipulation.getOutputFramesFileNameTemplate(exportId));
        try {
            commandlineExecutor.execute(commandline);
        } catch (Exception e) {
            logger.warn("export frames error {}: {}", exportId, e.getMessage());
            throw new RuntimeException(e);
        }

        //make preview frames
        var framesCount = fileManipulation.getOutputFrameCount(exportId);
        for (var frameId = 0; frameId < framesCount; frameId++) {
            var framePath = fileManipulation.getOutputFramePath(exportId, frameId);
            var framePreviewPath = fileManipulation.getOutputFramePreviewPath(exportId, frameId);
            var convertCommandline = new CommandlineArguments(ffmpegParams.getFFMPEGBinary())
                    .add("-hide_banner")
                    .add("-y")
                    .add("-i", framePath.toString())
                    .add("-qscale:v", exportParams.getFramesPreviewQuality())
                    .add("-vf", String.format("scale=-1:%d", exportParams.getFramesPreviewSize()))
                    .add(framePreviewPath.toString());
            try {
                commandlineExecutor.execute(convertCommandline);
            } catch (Exception e) {
                logger.warn("export frames preview error {},{}: {}", exportId, frameId, e.getMessage());
                throw new RuntimeException(e);
            }
        }

        return new ExportFrames(exportId, framesCount);
    }

    public InputStream exportedFrameInputStream(String exportId, int frameId) {
        if (!fileManipulation.outputFrameExists(exportId, frameId)) {
            throw new IllegalArgumentException("frame does not exist");
        }
        try {
            return Files.newInputStream(fileManipulation.getOutputFramePath(exportId, frameId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream exportedFramePreviewInputStream(String exportId, int frameId) {
        if (!fileManipulation.outputFramePreviewExists(exportId, frameId)) {
            throw new IllegalArgumentException("frame preview does not exist");
        }
        try {
            return Files.newInputStream(fileManipulation.getOutputFramePreviewPath(exportId, frameId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Join exported frames into a gif
     *
     * @param exportId ...
     * @param start Start frame index (inclusive)
     * @param end End frame index (inclusive)
     * @param framerate ...
     * @return ...
     */
    public InputStream exportFramesGif(String exportId, int start, int end, int framerate) {
        if (end < start) {
            throw new IllegalArgumentException("end should be greater than start");
        }

        for (var frameIdx = start; frameIdx <= end; frameIdx++) {
            if (!fileManipulation.outputFrameExists(exportId, frameIdx)) {
                throw new IllegalArgumentException(String.format("exported frame does not exist exportId=%s frameId=%s", exportId, frameIdx));
            }
        }

        var gifExportId = UUID.randomUUID().toString();
        var gifExportPath = fileManipulation.getOutputGifFilePath(gifExportId);

        var commandline = new CommandlineArguments(ffmpegParams.getFFMPEGBinary())
                .add("-hide_banner")
                .add("-y")
                .add("-framerate", framerate)
                .add("-start_number", start)
                .add("-f", "image2")
                .add("-i", fileManipulation.getOutputFramesFileNameTemplate(exportId))
                .add("-frames:v", end-start+1)
                .add("-vf", String.format("fps=%s", framerate))
                .add(gifExportPath.toString());
        try {
            commandlineExecutor.execute(commandline);
            return Files.newInputStream(gifExportPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
