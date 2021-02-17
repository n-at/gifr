package ru.doublebyte.gifr.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.doublebyte.gifr.configuration.GlobalAudioEncodingParams;
import ru.doublebyte.gifr.configuration.GlobalVideoEncodingParams;
import ru.doublebyte.gifr.configuration.SegmentParams;
import ru.doublebyte.gifr.struct.mediainfo.VideoFileInfo;
import ru.doublebyte.gifr.utils.FileNameUtils;

import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MediaEncoder {

    private static final Logger logger = LoggerFactory.getLogger(MediaEncoder.class);

    private final TimeoutCommandlineExecutor timeoutCommandlineExecutor;
    private final GlobalVideoEncodingParams globalVideoEncodingParams;
    private final GlobalAudioEncodingParams globalAudioEncodingParams;
    private final SegmentParams segmentParams;
    private final FileManipulation fileManipulation;

    public MediaEncoder(
            TimeoutCommandlineExecutor timeoutCommandlineExecutor,
            GlobalVideoEncodingParams globalVideoEncodingParams,
            GlobalAudioEncodingParams globalAudioEncodingParams,
            SegmentParams segmentParams,
            FileManipulation fileManipulation
    ) {
        this.timeoutCommandlineExecutor = timeoutCommandlineExecutor;
        this.globalVideoEncodingParams = globalVideoEncodingParams;
        this.globalAudioEncodingParams = globalAudioEncodingParams;
        this.segmentParams = segmentParams;
        this.fileManipulation = fileManipulation;
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
                "ffmpeg -hide_banner -y" + " " +
                "-ss 0 -t 1" + " " +
                String.format("-i \"%s\"", FileNameUtils.escape(videoFileInfo.getPath())) + " " +
                globalVideoEncodingParams.toEncoderOptions() + " " +
                globalAudioEncodingParams.toEncoderOptions() + " " +
                String.join(" ", getDashStreams(videoFileInfo)) + " " +
                String.format("-init_seg_name \"init-%s-\\$RepresentationID\\$.\\$ext\\$\"", videoFileInfo.getChecksum()) + " " +
                String.format("-media_seg_name \"chunk-%s-\\$RepresentationID\\$-\\$Number%%05d\\$.\\$ext\\$\"", videoFileInfo.getChecksum()) + " " +
                "-dash_segment_type mp4 -use_template 1 -use_timeline 0" + " " +
                String.format("-seg_duration %d", segmentParams.getDuration()) + " " +
                String.format("-adaptation_sets \"%s\"", getDashAdaptationSets(videoFileInfo)) + " " +
                "-f dash" + " " +
                String.format("\"%s\"", FileNameUtils.escape(dashFilePath.toString()));

        timeoutCommandlineExecutor.execute(commandline, segmentParams.getEncodingTimeout());

        try {
            var dash = Files.readString(dashFilePath);

            //fix duration and buffer time
            dash = dash.replaceAll("mediaPresentationDuration=\".+\"", String.format("mediaPresentationDuration=\"%s\"", videoFileInfo.getMpdDuration()));
            dash = dash.replaceAll("minBufferTime=\".+\"", String.format("minBufferTime=\"PT%d.0S\"", segmentParams.getDuration() * 2));

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
    private List<String> getDashStreams(VideoFileInfo videoFileInfo) {
        final var streams = videoFileInfo.getAudioStreams()
                .stream()
                .map(stream -> String.format("-map 0:%d", stream.getIndex()))
                .collect(Collectors.toList());

        final var videoStreamIdx = videoFileInfo.getVideoStream().getIndex();
        final var videoPresets = videoFileInfo.getAvailableVideoQualityPresets();
        for (var presetIdx = 0; presetIdx < videoPresets.size(); presetIdx++) {
            var preset = videoPresets.get(presetIdx);
            streams.add(preset.toVideoEncodingOptions(videoStreamIdx, presetIdx));
        }

        return streams;
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

        var segmentDuration = segmentParams.getDuration();
        var timeStart = (chunkIdNumber - 1) * segmentDuration + 0.023222; //magical constant
        var timeEnd = chunkIdNumber * segmentDuration;

        logger.info("generating audio {} #{}, chunk {}", videoFileInfo.getChecksum(), streamId, chunkId);

        try {
            var stream = videoFileInfo.getAudioStreamByDashStreamId(streamId);

            var commandline = "ffmpeg -hide_banner -y" + " " +
                    String.format(Locale.US, "-ss %f", timeStart) + " " +
                    String.format("-to %d", timeEnd) + " " +
                    String.format("-i \"%s\"", FileNameUtils.escape(videoFileInfo.getPath())) + " " +
                    "-copyts -start_at_zero -vn" + " " +
                    String.format("-map 0:%d", stream.getIndex()) + " " +
                    globalAudioEncodingParams.toEncoderOptions() + " " +
                    "-f mpegts -muxdelay 0 -muxpreload 0" + " " +
                    String.format("\"%s\"", FileNameUtils.escape(chunkFilePath.toString()));

            timeoutCommandlineExecutor.execute(commandline, globalAudioEncodingParams.getEncodingTimeout());
        } catch (Exception e) {
            logger.warn(String.format("audio segment encoding error %s %s %s", videoFileInfo.getPath(), streamId, chunkId));
            throw new RuntimeException(e);
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

        var segmentDuration = segmentParams.getDuration();
        var timeStart = (chunkIdNumber - 1) * segmentDuration;
        var timeEnd = chunkIdNumber * segmentDuration;

        logger.info("generating video {} #{} chunk {}", videoFileInfo.getChecksum(), streamId, chunkId);

        try {
            var stream = videoFileInfo.getVideoStream();
            var qualityPreset = videoFileInfo.getVideoQualityPresetByDashStreamId(streamId);

            var commandline = "ffmpeg -hide_banner -y" + " " +
                    String.format("-ss %d", timeStart) + " " +
                    String.format("-to %d", timeEnd) + " " +
                    String.format("-i \"%s\"", FileNameUtils.escape(videoFileInfo.getPath())) + " " +
                    "-copyts -start_at_zero -an" + " " +
                    String.format("-map 0:%d", stream.getIndex()) + " " +
                    globalVideoEncodingParams.toEncoderOptions() + " " +
                    String.format("-vf \"scale=-2:%d\"", qualityPreset.getSize()) + " " +
                    String.format("-b:v %dk", qualityPreset.getBitrate()) + " " +
                    String.format("-maxrate %dk", qualityPreset.getMaxrate()) + " " +
                    String.format("-bufsize %dk", qualityPreset.getBufsize()) + " " +
                    "-f mpegts -muxdelay 0 -muxpreload 0" + " " +
                    String.format("\"%s\"", FileNameUtils.escape(chunkFilePath.toString()));

            timeoutCommandlineExecutor.execute(commandline, globalVideoEncodingParams.getEncodingTimeout());
        } catch (Exception e) {
            logger.warn(String.format("video segment encoding error %s %s %s", videoFileInfo.getPath(), streamId, chunkId));
            throw new RuntimeException(e);
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
            var stream = videoFileInfo.getSubtitlesStreamByDashStreamId(streamId);

            var commandline =
                    "ffmpeg -hide_banner -y" + " " +
                    String.format("-i \"%s\"", FileNameUtils.escape(videoFileInfo.getPath())) + " " +
                    String.format("-map 0:%d", stream.getIndex()) + " " +
                    "-c:s webvtt" + " " +
                    String.format("\"%s\"", FileNameUtils.escape(filePath.toString()));

            timeoutCommandlineExecutor.execute(commandline, segmentParams.getEncodingTimeout());
        } catch (Exception e) {
            logger.warn(String.format("subtitles encoding error %s %s", videoFileInfo.getPath(), streamId));
            throw new RuntimeException(e);
        }
    }

}
