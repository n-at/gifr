package ru.doublebyte.gifr.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.doublebyte.gifr.components.DashEncoding;
import ru.doublebyte.gifr.components.MediaInfo;
import ru.doublebyte.gifr.struct.response.Response;
import ru.doublebyte.gifr.struct.response.VideoResponse;

import javax.servlet.http.HttpServletResponse;

@RestController
public class VideoController {

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    private final MediaInfo mediaInfo;
    private final DashEncoding dashEncoding;

    public VideoController(MediaInfo mediaInfo, DashEncoding dashEncoding) {
        this.mediaInfo = mediaInfo;
        this.dashEncoding = dashEncoding;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Open video file, prepare DASH stream descriptors
     *
     * @param path Path to video file
     * @return ...
     */
    @RequestMapping("/video/open")
    public Response open(@RequestParam("path") String path) {
        try {
            var videoFileInfo = mediaInfo.videoFileInfo(path);
            if (videoFileInfo == null) {
                throw new IllegalStateException("Video file info error");
            }

            dashEncoding.open(videoFileInfo);

            var url = String.format("/video/dash/%s.mpd", videoFileInfo.getChecksum());
            return VideoResponse.success(url, videoFileInfo.getChecksum());
        } catch (Exception e) {
            logger.error("open video error " + path, e);
            return Response.error(e.getMessage());
        }
    }

    /**
     * Get DASH descriptor file
     *
     * @param videoFileId ...
     * @param response ...
     */
    @RequestMapping(path = "/video/dash/{id}.mpd", produces = "application/dash+xml")
    public void streamDash(
            @PathVariable("id") String videoFileId,
            HttpServletResponse response
    ) {
        try (
            var inputStream = dashEncoding.getDashFileInputStream(videoFileId)
        ) {
            StreamUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            logger.error("dash error", e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Get DASH stream init file
     *
     * @param videoFileId ...
     * @param streamId ...
     * @param response ...
     */
    @RequestMapping(path = "/video/init/{id}-{stream}.m4s", produces = "application/octet-stream")
    public void streamInit(
            @PathVariable("id") String videoFileId,
            @PathVariable("stream") String streamId,
            HttpServletResponse response
    ) {
        try (
                var inputStream = dashEncoding.getInitFileInputStream(videoFileId, streamId)
        ) {
            StreamUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            logger.error("dash input error", e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Get DASH stream chunk
     *
     * @param videoFileId ...
     * @param streamId ...
     * @param chunkId ...
     * @param response ...
     */
    @RequestMapping(path = "/video/chunk/{id}-{stream}-{chunk}.m4s", produces = "application/octet-stream")
    public void streamChunk(
            @PathVariable("id") String videoFileId,
            @PathVariable("stream") String streamId,
            @PathVariable("chunk") String chunkId,
            HttpServletResponse response
    ) {
        try (
                var inputStream = dashEncoding.getChunkFileInputStream(videoFileId, streamId, chunkId)
        ) {
            StreamUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            logger.error("dash error", e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
