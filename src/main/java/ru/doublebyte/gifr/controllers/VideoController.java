package ru.doublebyte.gifr.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.doublebyte.gifr.components.DashEncoding;

import javax.servlet.http.HttpServletResponse;

@RestController
public class VideoController {

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    private final DashEncoding dashEncoding;

    public VideoController(DashEncoding dashEncoding) {
        this.dashEncoding = dashEncoding;
    }

    ///////////////////////////////////////////////////////////////////////////

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
            logger.error("dash error id=" + videoFileId, e);
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
            logger.error(String.format("init error id=%s stream=%s", videoFileId, streamId), e);
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
            logger.error(String.format("chunk error: id=%s stream=%s chunk=%s", videoFileId, streamId, chunkId), e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
