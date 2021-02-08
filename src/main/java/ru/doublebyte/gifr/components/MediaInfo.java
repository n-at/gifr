package ru.doublebyte.gifr.components;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;
import ru.doublebyte.gifr.struct.AudioStreamInfo;
import ru.doublebyte.gifr.struct.VideoFileInfo;
import ru.doublebyte.gifr.struct.VideoStreamInfo;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class MediaInfo {

    private static final Logger logger = LoggerFactory.getLogger(MediaInfo.class);

    private final TimeoutCommandlineExecutor timeoutCommandlineExecutor;
    private final Cache<String, VideoFileInfo> videoFileInfoCache;
    private final Cache<String, VideoFileInfo> videoFileInfoByIdCache;

    public MediaInfo(TimeoutCommandlineExecutor timeoutCommandlineExecutor) {
        this.timeoutCommandlineExecutor = timeoutCommandlineExecutor;
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
                videoFileInfo = videoFileInfoImpl(videoFilePath);
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

    private VideoFileInfo videoFileInfoImpl(String videoFilePath) {
        if (!Files.exists(Paths.get(videoFilePath))) {
            throw new IllegalArgumentException("file not found");
        }

        var videoStreamInfo = getVideoStreamInfo(videoFilePath);
        if (videoStreamInfo == null) {
            throw new IllegalStateException("no video stream found");
        }

        var audioStreamInfo = getAudioStreamInfo(videoFilePath);

        var videoFileInfo = new VideoFileInfo();
        videoFileInfo.setPath(videoFilePath);
        videoFileInfo.setChecksum(fileChecksum(videoFilePath));
        videoFileInfo.setDuration(videoStreamInfo.getDuration());
        videoFileInfo.setVideo(videoStreamInfo);
        videoFileInfo.setAudio(audioStreamInfo);
        return videoFileInfo;
    }

    ///////////////////////////////////////////////////////////////////////////

    protected VideoStreamInfo getVideoStreamInfo(String videoFilePath) {
        try {
            var command = "ffprobe -v error -select_streams v:0" + " " +
                    "-show_entries stream=codec_long_name,width,height,pix_fmt,r_frame_rate,bit_rate,duration" + " " +
                    "-of default=noprint_wrappers=1:nokey=0" + " " +
                    String.format("\"%s\"", videoFilePath);

            var output = timeoutCommandlineExecutor.execute(command);
            if (output == null || output.isEmpty()) {
                return null;
            }

            var videoStreamInfo = new VideoStreamInfo();

            Arrays.stream(output.split("\n"))
                    .forEach(entry -> {
                        if (entry == null || entry.isEmpty() || !entry.contains("=")) {
                            return;
                        }
                        var parameter = entry.split("=");
                        var name = parameter[0];
                        var value = parameter[1];

                        switch (name) {
                            case "codec_long_name":
                                videoStreamInfo.setCodec(value);
                                break;
                            case "width":
                                videoStreamInfo.setWidth(Integer.parseInt(value));
                                break;
                            case "height":
                                videoStreamInfo.setHeight(Integer.parseInt(value));
                                break;
                            case "pix_fmt":
                                videoStreamInfo.setPixelFormat(value);
                                break;
                            case "r_frame_rate":
                                if (value.contains("/")) {
                                    var rateParts = value.split("/");
                                    var rate = Double.parseDouble(rateParts[0]) / Double.parseDouble(rateParts[1]);
                                    videoStreamInfo.setFramerate(rate);
                                } else {
                                    videoStreamInfo.setFramerate(Double.parseDouble(value));
                                }
                                break;
                            case "bit_rate":
                                videoStreamInfo.setBitrate(Integer.parseInt(value));
                                break;
                            case "duration":
                                videoStreamInfo.setDuration(Double.parseDouble(value));
                                break;
                        }
                    });

            return videoStreamInfo;
        } catch (Exception e) {
            logger.error("video stream info error " + videoFilePath, e);
            return null;
        }
    }

    protected AudioStreamInfo getAudioStreamInfo(String videoFilePath) {
        try {
            var command = "ffprobe -v error -select_streams a:0" + " " +
                    "-show_entries stream=codec_long_name,sample_rate,channels,bit_rate,duration" + " " +
                    "-of default=noprint_wrappers=1:nokey=0" + " " +
                    String.format("\"%s\"", videoFilePath);

            var output = timeoutCommandlineExecutor.execute(command);
            if (output == null || output.isEmpty()) {
                return null;
            }

            var audioStreamInfo = new AudioStreamInfo();

            Arrays.stream(output.split("\n"))
                    .forEach(entry -> {
                        if (entry == null || entry.isEmpty() || !entry.contains("=")) {
                            return;
                        }
                        var parameter = entry.split("=");
                        var name = parameter[0];
                        var value = parameter[1];

                        switch (name) {
                            case "codec_long_name":
                                audioStreamInfo.setCodec(value);
                                break;
                            case "sample_rate":
                                audioStreamInfo.setSampleRate(Integer.parseInt(value));
                                break;
                            case "channels":
                                audioStreamInfo.setChannels(Integer.parseInt(value));
                                break;
                            case "bit_rate":
                                audioStreamInfo.setBitrate(Integer.parseInt(value));
                                break;
                            case "duration":
                                audioStreamInfo.setDuration(Double.parseDouble(value));
                                break;
                        }
                    });

            return audioStreamInfo;
        } catch (Exception e) {
            logger.error("audio stream info error " + videoFilePath, e);
            return null;
        }
    }

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

}
