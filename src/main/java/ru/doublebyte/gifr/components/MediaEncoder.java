package ru.doublebyte.gifr.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.doublebyte.gifr.configuration.FFMPEGParams;
import ru.doublebyte.gifr.configuration.GlobalAudioEncodingParams;
import ru.doublebyte.gifr.configuration.GlobalVideoEncodingParams;
import ru.doublebyte.gifr.configuration.SegmentParams;
import ru.doublebyte.gifr.struct.CommandlineArguments;
import ru.doublebyte.gifr.struct.mediainfo.VideoFileInfo;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MediaEncoder {

    private static final Logger logger = LoggerFactory.getLogger(MediaEncoder.class);

    private final CommandlineExecutor commandlineExecutor;
    private final GlobalVideoEncodingParams globalVideoEncodingParams;
    private final GlobalAudioEncodingParams globalAudioEncodingParams;
    private final FFMPEGParams ffmpegParams;
    private final SegmentParams segmentParams;
    private final FileManipulation fileManipulation;

    private final Semaphore videoTranscodingLimiter;
    private final Semaphore audioTranscodingLimiter;

    public MediaEncoder(
            CommandlineExecutor commandlineExecutor,
            GlobalVideoEncodingParams globalVideoEncodingParams,
            GlobalAudioEncodingParams globalAudioEncodingParams,
            FFMPEGParams ffmpegParams,
            SegmentParams segmentParams,
            FileManipulation fileManipulation
    ) {
        this.commandlineExecutor = commandlineExecutor;
        this.globalVideoEncodingParams = globalVideoEncodingParams;
        this.globalAudioEncodingParams = globalAudioEncodingParams;
        this.ffmpegParams = ffmpegParams;
        this.segmentParams = segmentParams;
        this.fileManipulation = fileManipulation;
        this.videoTranscodingLimiter = new Semaphore(globalVideoEncodingParams.getConcurrentJobs());
        this.audioTranscodingLimiter = new Semaphore(globalAudioEncodingParams.getConcurrentJobs());
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Generate MPD descriptor and init files
     *
     * @param videoFileInfo Video file info
     */
    public void generateDashInit(VideoFileInfo videoFileInfo) {
        final var dashFilePath = fileManipulation.getDashFilePath(videoFileInfo.getChecksum());

        final var commandline =
                new CommandlineArguments(ffmpegParams.getFFMPEGBinary())
                .add("-hide_banner")
                .add("-y")
                .add("-ss", 0)
                .add("-t", 1)
                .add("-i", videoFileInfo.getPath())
                .add(globalVideoEncodingParams.toCommandlineArguments())
                .add(globalAudioEncodingParams.toCommandlineArguments())
                .add(getDashStreams(videoFileInfo))
                .add("-init_seg_name", String.format("init-%s-$RepresentationID$.$ext$", videoFileInfo.getChecksum()))
                .add("-media_seg_name", String.format("chunk-%s-$RepresentationID$-$Number%%05d$.$ext$", videoFileInfo.getChecksum()))
                .add("-dash_segment_type", "mp4")
                .add("-use_template", 1)
                .add("-use_timeline", 0)
                .add("-seg_duration", String.format(Locale.US, "%.15f", getSegmentDuration()))
                .add("-adaptation_sets", getDashAdaptationSets(videoFileInfo))
                .add("-f", "dash")
                .add(dashFilePath.toString());

        commandlineExecutor.execute(commandline, segmentParams.getEncodingTimeout(), true);

        try {
            var dash = Files.readString(dashFilePath);

            //fix duration and buffer time
            dash = dash.replaceAll("mediaPresentationDuration=\".+\"", String.format("mediaPresentationDuration=\"%s\"", videoFileInfo.getMpdDuration()));
            dash = dash.replaceAll("minBufferTime=\".+\"", String.format(Locale.US, "minBufferTime=\"PT%.2fS\"", getSegmentDuration() * 2));

            //fix path to init and chunk files
            dash = dash.replaceAll("init-", "/video/init/");
            dash = dash.replaceAll("chunk-", "/video/chunk/");

            //fix audio track names
            for (var audioStreamIdx = 0; audioStreamIdx < videoFileInfo.getAudioStreams().size(); audioStreamIdx++) {
                var audioStream = videoFileInfo.getAudioStreams().get(audioStreamIdx);
                var language = String.format("%d:%s:%s", audioStreamIdx+1, audioStream.getLanguage(), audioStream.getTitle()).replaceAll("\"", "");
                dash = dash.replaceAll(
                        String.format("(<AdaptationSet id=\"%d\".*lang=\")(.*)(\")", audioStreamIdx),
                        String.format("$1%s$3", Matcher.quoteReplacement(language))
                );
            }

            Files.writeString(dashFilePath, dash);
        } catch (Exception e) {
            logger.error("mpd fix error", e);
            throw new RuntimeException(e);
        }

        try {
            fileManipulation.removeEncodedChunksFromDashOutput();
        } catch (Exception e) {
            logger.error("chunks remove error", e);
        }
    }

    /**
     * List streams in DASH file.
     * First, all audio streams
     * Second, video streams by quality presets
     *
     * @param videoFileInfo ...
     * @return ...
     */
    private CommandlineArguments getDashStreams(VideoFileInfo videoFileInfo) {
        final var arguments = new CommandlineArguments();

        videoFileInfo.getAudioStreams()
                .forEach(stream -> arguments.add("-map", String.format("0:%d", stream.getIndex())));

        final var videoStreamIdx = videoFileInfo.getVideoStream().getIndex();
        final var videoPresets = videoFileInfo.getAvailableVideoQualityPresets();
        for (var presetIdx = 0; presetIdx < videoPresets.size(); presetIdx++) {
            arguments
                    .add("-map", String.format("0:%d", videoStreamIdx))
                    .add(videoPresets.get(presetIdx).toCommandlineArguments(presetIdx));
        }

        return arguments;
    }

    /**
     * Adaptation sets definition: every audio stream in it's own adaptation set and one set for all video streams
     *
     * @param videoFileInfo ...
     * @return ...
     */
    private String getDashAdaptationSets(VideoFileInfo videoFileInfo) {
        final var audioStreamsCount = videoFileInfo.getAudioStreams().size();
        final var audioAdaptationSets = IntStream.range(0, audioStreamsCount)
                .mapToObj(streamIdx -> String.format("id=%d,streams=%d", streamIdx, streamIdx))
                .collect(Collectors.joining(" "));
        return String.format("%s id=%d,streams=v", audioAdaptationSets, audioStreamsCount);
    }

    /**
     * Optimal segment duration for AAC frame margin
     *
     * @return ...
     */
    private double getSegmentDuration() {
        final var frameDuration = getFrameDuration();
        return Math.ceil(segmentParams.getDuration() / frameDuration) * frameDuration;
    }

    private double getFrameDuration() {
        return 1024.0 / globalAudioEncodingParams.getSampleRate();
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Generate one particular audio segment
     *
     * @param videoFileInfo Video file info
     * @param streamId Media stream index
     * @param chunkId Chunk number (1-based)
     */
    public void generateAudioSegment(VideoFileInfo videoFileInfo, String streamId, String chunkId) {
        var chunkFilePath = fileManipulation.getChunkFilePath(videoFileInfo.getChecksum(), streamId, chunkId);
        var chunkIdNumber = Integer.parseInt(chunkId);

        var overhead = Math.ceil(1.0 / getFrameDuration()) * getFrameDuration();

        var segmentDuration = getSegmentDuration();
        var timeStart = (chunkIdNumber - 1) * segmentDuration;
        var timeEnd = chunkIdNumber * segmentDuration;

        if (chunkIdNumber > 1) {
            //to remove aac encoder delay (1024 samples in beginning of stream) add some overhead
            //and later trim it with delay
            timeStart -= overhead;
        }

        logger.info("generating audio {} #{}, chunk {}", videoFileInfo.getChecksum(), streamId, chunkId);

        try {
            audioTranscodingLimiter.acquire();

            final var temporaryChunkPath = Paths.get(chunkFilePath.toString() + ".tmp");

            final var commandline =
                    new CommandlineArguments(ffmpegParams.getFFMPEGBinary())
                    .add("-hide_banner")
                    .add("-y")
                    .add("-ss", String.format(Locale.US, "%.15f", timeStart))
                    .add("-to", String.format(Locale.US, "%.15f", timeEnd))
                    .add("-i", videoFileInfo.getPath())
                    .add("-copyts")
                    .add("-start_at_zero")
                    .add("-vn")
                    .add("-map", String.format("0:%d", videoFileInfo.getAudioStreamByDashStreamId(streamId).getIndex()))
                    .add(globalAudioEncodingParams.toCommandlineArguments())
                    .add("-f", "mpegts")
                    .add("-muxdelay", 0)
                    .add("-muxpreload", 0)
                    .add(temporaryChunkPath.toString());

            commandlineExecutor.execute(commandline, globalAudioEncodingParams.getEncodingTimeout());

            if (chunkIdNumber > 1) {
                final var trimCommandline =
                        new CommandlineArguments(ffmpegParams.getFFMPEGBinary())
                                .add("-hide_banner")
                                .add("-y")
                                .add("-ss", String.format(Locale.US, "%.15f", overhead))
                                .add("-i", temporaryChunkPath.toString())
                                .add("-c:a", "copy")
                                .add("-vn")
                                .add("-copyts")
                                .add("-f", "mpegts")
                                .add("-muxdelay", 0)
                                .add("-muxpreload", 0)
                                .add(chunkFilePath.toString());

                commandlineExecutor.execute(trimCommandline, globalAudioEncodingParams.getEncodingTimeout());

                Files.delete(temporaryChunkPath);
            } else {
                Files.move(temporaryChunkPath, chunkFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            logger.warn(String.format("audio segment encoding error %s %s %s", videoFileInfo.getPath(), streamId, chunkId));
            throw new RuntimeException(e);
        } finally {
            audioTranscodingLimiter.release();
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Generate one particular video segment
     *
     * @param videoFileInfo Video file info
     * @param streamId Media stream index
     * @param chunkId Chunk number (1-based)
     */
    public void generateVideoSegment(VideoFileInfo videoFileInfo, String streamId, String chunkId) {
        var chunkFilePath = fileManipulation.getChunkFilePath(videoFileInfo.getChecksum(), streamId, chunkId);
        var chunkIdNumber = Integer.parseInt(chunkId);

        var segmentDuration = getSegmentDuration();
        var timeStart = (chunkIdNumber - 1) * segmentDuration;
        var timeEnd = chunkIdNumber * segmentDuration;

        logger.info("generating video {} #{} chunk {}", videoFileInfo.getChecksum(), streamId, chunkId);

        try {
            videoTranscodingLimiter.acquire();

            final var commandline =
                    new CommandlineArguments(ffmpegParams.getFFMPEGBinary())
                    .add("-hide_banner")
                    .add("-y")
                    .add("-ss", String.format(Locale.US, "%.15f", timeStart))
                    .add("-to", String.format(Locale.US, "%.15f", timeEnd))
                    .add("-i", videoFileInfo.getPath())
                    .add("-copyts")
                    .add("-start_at_zero")
                    .add("-an")
                    .add("-map", String.format("0:%d", videoFileInfo.getVideoStream().getIndex()))
                    .add(globalVideoEncodingParams.toCommandlineArguments())
                    .add(videoFileInfo.getVideoQualityPresetByDashStreamId(streamId).toCommandlineArguments())
                    .add("-f", "mpegts")
                    .add("-bitexact")
                    .add("-muxdelay", 0)
                    .add("-muxpreload", 0)
                    .add(chunkFilePath.toString());

            commandlineExecutor.execute(commandline, globalVideoEncodingParams.getEncodingTimeout());
        } catch (Exception e) {
            logger.warn(String.format("video segment encoding error %s %s %s", videoFileInfo.getPath(), streamId, chunkId));
            throw new RuntimeException(e);
        } finally {
            videoTranscodingLimiter.release();
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Generate subtitles file
     *
     * @param videoFileInfo Video file info
     * @param streamId Subtitles track index
     */
    public void generateSubtitles(VideoFileInfo videoFileInfo, String streamId) {
        var filePath = fileManipulation.getSubtitlesFilePath(videoFileInfo.getChecksum(), streamId);

        logger.info("generating subtitles {} #{}", videoFileInfo.getChecksum(), streamId);

        try {
            final var commandline =
                    new CommandlineArguments(ffmpegParams.getFFMPEGBinary())
                    .add("-hide_banner")
                    .add("-y")
                    .add("-i", videoFileInfo.getPath())
                    .add("-map", String.format("0:%d", videoFileInfo.getSubtitlesStreamByDashStreamId(streamId).getIndex()))
                    .add("-c:s", "webvtt")
                    .add(filePath.toString());

            commandlineExecutor.execute(commandline, segmentParams.getEncodingTimeout());
        } catch (Exception e) {
            logger.warn(String.format("subtitles encoding error %s %s", videoFileInfo.getPath(), streamId));
            throw new RuntimeException(e);
        }
    }

}
