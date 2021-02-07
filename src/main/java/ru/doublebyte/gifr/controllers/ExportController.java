package ru.doublebyte.gifr.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.doublebyte.gifr.components.GifExporter;
import ru.doublebyte.gifr.components.MediaInfo;

import javax.servlet.http.HttpServletResponse;

@RestController
public class ExportController {

    private static final Logger logger = LoggerFactory.getLogger(ExportController.class);

    private final MediaInfo mediaInfo;
    private final GifExporter gifExporter;

    public ExportController(MediaInfo mediaInfo, GifExporter gifExporter) {
        this.mediaInfo = mediaInfo;
        this.gifExporter = gifExporter;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Export video file as gif
     *
     * @param videoFileId ...
     * @param positionStart Start position in seconds
     * @param positionEnd End position in seconds
     * @param framerate FPS
     * @param size Frame size
     * @param response ...
     * @throws Exception ...
     */
    @RequestMapping(path = "/export", produces = "image/gif")
    public void exportGif(
            @RequestParam("id") String videoFileId,
            @RequestParam("start") Double positionStart,
            @RequestParam("end") Double positionEnd,
            @RequestParam("framerate") Integer framerate,
            @RequestParam("size") Integer size,
            HttpServletResponse response
    ) throws Exception {
        try {
            var videoFileInfo = mediaInfo.getByVideoFileId(videoFileId);
            if (videoFileInfo == null) {
                throw new IllegalStateException("Video file info not found");
            }

            var stream = gifExporter.export(videoFileInfo, positionStart, positionEnd, framerate, size);
            StreamUtils.copy(stream, response.getOutputStream());
            stream.close();
        } catch (Exception e) {
            logger.info("export error", e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("Error: " + e.getMessage());
            response.getWriter().flush();
        }
    }

}
