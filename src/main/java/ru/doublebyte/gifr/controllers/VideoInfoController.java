package ru.doublebyte.gifr.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.doublebyte.gifr.components.MediaInfo;
import ru.doublebyte.gifr.struct.response.Response;
import ru.doublebyte.gifr.struct.response.VideoInfoResponse;

@RestController
public class VideoInfoController {

    private static final Logger logger = LoggerFactory.getLogger(VideoInfoController.class);

    ///////////////////////////////////////////////////////////////////////////

    private final MediaInfo mediaInfo;

    public VideoInfoController(MediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Get information about video file
     *
     * @param path Path to file
     * @return ...
     */
    @RequestMapping("/video-info")
    public Response info(@RequestParam("path") String path) {
        try {
            logger.info("video info {}", path);
            return VideoInfoResponse.success(mediaInfo.videoFileInfo(path));
        } catch (Exception e) {
            logger.error("video info error " + path, e);
            return Response.error(e.getMessage());
        }
    }
}
