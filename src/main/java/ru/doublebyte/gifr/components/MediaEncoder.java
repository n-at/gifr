package ru.doublebyte.gifr.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.doublebyte.gifr.configuration.GlobalAudioEncodingParams;
import ru.doublebyte.gifr.configuration.GlobalVideoEncodingParams;
import ru.doublebyte.gifr.configuration.SegmentParams;
import ru.doublebyte.gifr.configuration.VideoQualityPreset;
import ru.doublebyte.gifr.struct.VideoFileInfo;

import java.nio.file.Files;
import java.util.Locale;

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

        var videoFilePath = videoFileInfo.getPath();
        videoFilePath = videoFilePath.replaceAll("\"", "\\\"");
        videoFilePath = videoFilePath.replaceAll("\n", "\\\n");

        final var commandline = String.format("ffmpeg -hide_banner -y -ss 0 -t 1 -i \"%s\"", videoFilePath) + " " +
                globalVideoEncodingParams.toEncoderOptions() + " " +
                globalAudioEncodingParams.toEncoderOptions() + " " +
                "-map 0:a" + " " +
                VideoQualityPreset.Video360.toVideoEncodingOptions(0) + " " +
                VideoQualityPreset.Video540.toVideoEncodingOptions(1) + " " +
                VideoQualityPreset.Video720.toVideoEncodingOptions(2) + " " +
                VideoQualityPreset.Video1080.toVideoEncodingOptions(3) + " " +
                String.format("-init_seg_name \"init-%s-\\$RepresentationID\\$.\\$ext\\$\"", videoFileInfo.getChecksum()) + " " +
                String.format("-media_seg_name \"chunk-%s-\\$RepresentationID\\$-\\$Number%%05d\\$.\\$ext\\$\"", videoFileInfo.getChecksum()) + " " +
                "-dash_segment_type mp4 -use_template 1 -use_timeline 0" + " " +
                String.format("-seg_duration %d", segmentParams.getDuration()) + " " +
                "-adaptation_sets \"id=0,streams=a id=1,streams=v\"" + " " +
                "-f dash" + " " +
                dashFilePath.toString();

        timeoutCommandlineExecutor.execute(commandline);

        try {
            var dash = Files.readString(dashFilePath);
            dash = dash.replaceAll("mediaPresentationDuration=\".+\"", String.format("mediaPresentationDuration=\"%s\"", videoFileInfo.getMpdDuration()));
            dash = dash.replaceAll("minBufferTime=\".+\"", String.format("minBufferTime=\"PT%d.0S\"", segmentParams.getDuration() * 2));
            dash = dash.replaceAll("init-", "/video/init/");
            dash = dash.replaceAll("chunk-", "/video/chunk/");
            Files.writeString(dashFilePath, dash);
        } catch (Exception e) {
            logger.error("mpd fix error", e);
        }

        try {
            fileManipulation.removeEncodedChunksFromDashOutput();
        } catch (Exception e) {
            logger.error("chunks remove error", e);
        }
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

        var videoFilePath = videoFileInfo.getPath();
        videoFilePath = videoFilePath.replaceAll("\"", "\\\"");
        videoFilePath = videoFilePath.replaceAll("\n", "\\\n");

        var segmentDuration = segmentParams.getDuration();
        var timeStart = (chunkIdNumber - 1) * segmentDuration + 0.023222; //magical constant
        var timeEnd = chunkIdNumber * segmentDuration;

        logger.info("generating audio {} #{}, chunk {}", videoFileInfo.getPath(), streamId, chunkId);

        var commandline = "ffmpeg -hide_banner -y" + " " +
                String.format(Locale.US, "-ss %f", timeStart) + " " +
                String.format("-to %d", timeEnd) + " " +
                String.format("-i \"%s\"", videoFilePath) + " " +
                "-copyts -start_at_zero" + " " +
                "-vn" + " " +
                globalAudioEncodingParams.toEncoderOptions() + " " +
                "-f mpegts -muxdelay 0 -muxpreload 0" + " " +
                chunkFilePath.toString();

        try {
            timeoutCommandlineExecutor.execute(commandline);
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
     * @param preset Video quality preset
     * @param chunkId Chunk number (1-based)
     */
    public void generateVideoSegment(VideoFileInfo videoFileInfo, String streamId, VideoQualityPreset preset, String chunkId) {
        var chunkFilePath = fileManipulation.getChunkFilePath(videoFileInfo.getChecksum(), streamId, chunkId);
        var chunkIdNumber = Integer.parseInt(chunkId);

        var videoFilePath = videoFileInfo.getPath();
        videoFilePath = videoFilePath.replaceAll("\"", "\\\"");
        videoFilePath = videoFilePath.replaceAll("\n", "\\\n");

        var segmentDuration = segmentParams.getDuration();
        var timeStart = (chunkIdNumber - 1) * segmentDuration;
        var timeEnd = chunkIdNumber * segmentDuration;

        logger.info("generating video {} #{} chunk {}", videoFileInfo.getPath(), streamId, chunkId);

        var commandline = "ffmpeg -hide_banner -y" + " " +
                String.format("-ss %d", timeStart) + " " +
                String.format("-to %d", timeEnd) + " " +
                String.format("-i \"%s\"", videoFilePath) + " " +
                "-copyts -start_at_zero" + " " +
                "-an" + " " +
                globalVideoEncodingParams.toEncoderOptions() + " " +
                String.format("-vf \"scale=-2:%d\"", preset.getSize()) + " " +
                String.format("-b:v %dk", preset.getBitrate()) + " " +
                String.format("-maxrate %dk", preset.getMaxrate()) + " " +
                String.format("-bufsize %dk", preset.getBufsize()) + " " +
                "-f mpegts -muxdelay 0 -muxpreload 0" + " " +
                chunkFilePath.toString();

        try {
            timeoutCommandlineExecutor.execute(commandline);
        } catch (Exception e) {
            logger.warn(String.format("video segment encoding error %s %s %s", videoFileInfo.getPath(), streamId, chunkId));
            throw new RuntimeException(e);
        }
    }

}
