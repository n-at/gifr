package ru.doublebyte.gifr.components;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;
import ru.doublebyte.gifr.configuration.FFMPEGParams;
import ru.doublebyte.gifr.struct.CommandlineArguments;
import ru.doublebyte.gifr.struct.mediainfo.VideoFileInfo;
import ru.doublebyte.gifr.struct.mediainfo.StreamInfo;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MediaInfo {

    private static final Logger logger = LoggerFactory.getLogger(MediaInfo.class);

    private final FFMPEGParams ffmpegParams;
    private final CommandlineExecutor commandlineExecutor;
    private final Cache<String, VideoFileInfo> videoFileInfoCache;
    private final Cache<String, VideoFileInfo> videoFileInfoByIdCache;

    public MediaInfo(FFMPEGParams ffmpegParams, CommandlineExecutor commandlineExecutor) {
        this.ffmpegParams = ffmpegParams;
        this.commandlineExecutor = commandlineExecutor;
        this.videoFileInfoCache = Caffeine.newBuilder().build();
        this.videoFileInfoByIdCache = Caffeine.newBuilder().build();
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Video file info
     *
     * @param videoFilePath Path to video file
     * @return Info
     */
    public VideoFileInfo videoFileInfo(String videoFilePath) {
        var videoFileInfo = videoFileInfoCache.getIfPresent(videoFilePath);

        if (videoFileInfo == null) {
            try {
                if (!Files.exists(Paths.get(videoFilePath))) {
                    throw new IllegalArgumentException("file not found: " + videoFilePath);
                }
                videoFileInfo = new VideoFileInfo(videoFilePath, fileChecksum(videoFilePath), getFileDuration(videoFilePath), getFileMediaStreams(videoFilePath));
                videoFileInfoCache.put(videoFilePath, videoFileInfo);
                videoFileInfoByIdCache.put(videoFileInfo.getChecksum(), videoFileInfo);
            } catch (Exception e) {
                logger.error("video file info error " + videoFilePath, e);
            }
        }

        return videoFileInfo;
    }

    public VideoFileInfo getByVideoFileId(String videoFileId) {
        return videoFileInfoByIdCache.getIfPresent(videoFileId);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * MD5 file checksum
     *
     * @param filePath Path to file
     * @return Checksum
     */
    protected String fileChecksum(String filePath) {
        try (
                var inputStream = Files.newInputStream(Paths.get(filePath))
        ) {
            return DigestUtils.md5DigestAsHex(inputStream);
        } catch (Exception e) {
            throw new IllegalStateException("checksum error", e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Get file media streams
     *
     * @param videoFilePath ...
     * @return ...
     */
    protected List<StreamInfo> getFileMediaStreams(String videoFilePath) {
        try {
            final var commandline =
                    new CommandlineArguments(ffmpegParams.getFFProbeBinary())
                    .add("-loglevel", "error")
                    .add("-show_entries", "stream")
                    .add("-of", "default=noprint_wrappers=1:nokey=0")
                    .add(videoFilePath);

            var output = commandlineExecutor.output(commandline);
            if (output != null && !output.isEmpty()) {
                return fromFFProbeOutput(output);
            } else {
                throw new Exception("stream info not found");
            }
        } catch (Exception e) {
            logger.error("file media streams error " + videoFilePath, e);
            return null;
        }
    }

    /**
     * Get video file duration
     *
     * @param videoFilePath ...
     * @return ...
     */
    protected double getFileDuration(String videoFilePath) {
        try {
            final var commandline =
                    new CommandlineArguments(ffmpegParams.getFFProbeBinary())
                    .add("-loglevel", "error")
                    .add("-show_entries", "format=duration")
                    .add("-of", "default=noprint_wrappers=1:nokey=1")
                    .add(videoFilePath);

            var output = commandlineExecutor.output(commandline);
            if (output != null && !output.isEmpty()) {
                return ffprobeDouble(output);
            } else {
                throw new Exception("duration not found");
            }
        } catch (Exception e) {
            logger.error("get duration error " + videoFilePath, e);
            return 0;
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Read stream info from output of ffprobe output
     *
     * @param output ffprobe raw output
     * @return stream definitions found in output
     */
    protected List<StreamInfo> fromFFProbeOutput(String output) {
        if (output == null || output.isEmpty()) {
            throw new IllegalArgumentException("empty ffprobe output");
        }

        final var pattern = Pattern.compile("^([a-zA-Z:_-]+)=(.*)$");

        final var streams = new ArrayList<StreamInfo>();
        StreamInfo currentStream = null;

        for (var entry : output.split("\n")) {
            if (entry == null || entry.isEmpty()) {
                continue;
            }
            final var matcher = pattern.matcher(entry);
            if (!matcher.find()) {
                continue;
            }
            final var property = matcher.group(1);
            final var value = matcher.group(2);

            if (property == null || property.isEmpty()) {
                continue;
            }
            if ("index".equals(property)) {
                currentStream = new StreamInfo();
                streams.add(currentStream);
            }
            if (currentStream == null) {
                continue;
            }

            switch (property) {
                case "index":
                    currentStream.setIndex(ffprobeInteger(value));
                    break;
                case "codec_type":
                    currentStream.setType(value);
                    break;
                case "codec_name":
                    currentStream.setCodec(value);
                    break;
                case "codec_long_name":
                    currentStream.setCodecDisplay(value);
                    break;
                case "profile":
                    currentStream.setProfile(value);
                    break;
                case "sample_rate":
                    currentStream.setSampleRate(ffprobeInteger(value));
                    break;
                case "channels":
                    currentStream.setChannels(ffprobeInteger(value));
                    break;
                case "channel_layout":
                    currentStream.setChannelLayout(value);
                    break;
                case "width":
                    currentStream.setWidth(ffprobeInteger(value));
                    break;
                case "height":
                    currentStream.setHeight(ffprobeInteger(value));
                    break;
                case "display_aspect_ratio":
                    currentStream.setAspectRatio(value);
                    break;
                case "pix_fmt":
                    currentStream.setPixelFormat(value);
                    break;
                case "r_frame_rate":
                    currentStream.setFramerate(ffprobeFraction(value));
                    break;
                case "avg_frame_rate":
                    currentStream.setAverageFramerate(ffprobeFraction(value));
                    break;
                case "duration":
                    currentStream.setDuration(ffprobeDouble(value));
                    break;
                case "bit_rate":
                    currentStream.setBitrate(ffprobeInteger(value));
                    break;
                case "DISPOSITION:default":
                    currentStream.setDefaultStream(ffprobeBoolean(value));
                    break;
                case "TAG:language":
                    currentStream.setLanguage(value);
                    break;
                case "TAG:title":
                    currentStream.setTitle(value);
                    break;
            }
        }

        return streams;
    }

    protected Integer ffprobeInteger(String value) {
        if (value == null || value.isEmpty() || "N/A".equals(value)) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    protected Boolean ffprobeBoolean(String value) {
        if (value == null || value.isEmpty() || "N/A".equals(value)) {
            return false;
        }
        try {
            return Integer.parseInt(value) != 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    protected Double ffprobeDouble(String value) {
        if (value == null || value.isEmpty() || "N/A".equals(value)) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    protected Double ffprobeFraction(String value) {
        if (value == null || value.isEmpty() || "N/A".equals(value)) {
            return null;
        }
        try {
            if (value.contains("/")) {
                var fractionParts = value.split("/");
                if (fractionParts.length != 2) {
                    return null;
                }
                return Double.parseDouble(fractionParts[0]) / Double.parseDouble(fractionParts[1]);
            } else {
                return Double.parseDouble(value);
            }
        } catch (Exception ignored) {
            return null;
        }
    }

}
