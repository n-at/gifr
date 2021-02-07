package ru.doublebyte.gifr.components;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;
import ru.doublebyte.gifr.struct.VideoFileInfo;

import java.nio.file.Files;
import java.nio.file.Paths;

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

        var videoFileInfo = new VideoFileInfo();
        videoFileInfo.setPath(videoFilePath);
        videoFileInfo.setChecksum(fileChecksum(videoFilePath));
        videoFileInfo.setDuration(videoDuration(videoFilePath));

        var size = videoSize(videoFilePath);
        if (size == null || size.isEmpty() || !size.contains("x")) {
            throw new IllegalArgumentException("unable to calculate video size");
        }
        var sizeParts = size.split("x");
        videoFileInfo.setWidth(Integer.parseInt(sizeParts[0].trim()));
        videoFileInfo.setHeight(Integer.parseInt(sizeParts[1].trim()));

        return videoFileInfo;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Video duration in seconds
     * by ffprobe
     *
     * @param videoFilePath Path to video file
     * @return Video duration
     */
    protected double videoDuration(String videoFilePath) {
        var command = String.format("ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 \"%s\"", videoFilePath);
        var output = timeoutCommandlineExecutor.execute(command);
        return Double.parseDouble(output);
    }

    /**
     * Video size in format WxH
     * by ffprobe
     *
     * @param videoFilePath path to video file
     * @return Video size
     */
    protected String videoSize(String videoFilePath) {
        var command = String.format("ffprobe -v error -select_streams v:0 -show_entries stream=width,height -of csv=s=x:p=0 \"%s\"", videoFilePath);
        return timeoutCommandlineExecutor.execute(command);
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
