package ru.doublebyte.gifr.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.doublebyte.gifr.components.GifExporter;
import ru.doublebyte.gifr.components.MediaInfo;
import ru.doublebyte.gifr.struct.response.ExportFramesResponse;
import ru.doublebyte.gifr.struct.response.Response;

import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

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

            var filename = String.format(Locale.US, "%s-%f-%f.gif", videoFileInfo.getChecksum(), positionStart, positionEnd);
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);

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

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Export video fragment into single frames
     *
     * @param videoFileId ...
     * @param positionStart Start position in seconds
     * @param positionEnd End position in seconds
     * @param framerate FPS
     * @param size Frame size
     * @return ...
     */
    @RequestMapping(path = "/export-frames")
    public Response exportFrames(
            @RequestParam("id") String videoFileId,
            @RequestParam("start") Double positionStart,
            @RequestParam("end") Double positionEnd,
            @RequestParam("framerate") Integer framerate,
            @RequestParam("size") Integer size
    ) {
        try {
            var videoFileInfo = mediaInfo.getByVideoFileId(videoFileId);
            if (videoFileInfo == null) {
                throw new IllegalStateException("Video file info not found");
            }

            var exportFrames = gifExporter.exportFrames(videoFileInfo, positionStart, positionEnd, framerate, size);

            return ExportFramesResponse.success(exportFrames.getId(), framerate, exportFrames.getFrames());
        } catch (Exception e) {
            logger.info("export frames error", e);
            return Response.error(e.getMessage());
        }
    }

    /**
     * Get exported frame
     *
     * @param exportId ...
     * @param frameId ...
     * @param response ...
     */
    @RequestMapping(path = "/export-frames/frame/{exportId}/{frameId}", produces = "image/png")
    public void exportFrameFile(
            @PathVariable("exportId") String exportId,
            @PathVariable("frameId") Integer frameId,
            HttpServletResponse response
    ) {
        try (
                var inputStream = gifExporter.exportedFrameInputStream(exportId, frameId)
        ) {
            StreamUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            logger.error(String.format("export frame error exportId=%s frameId=%d", exportId, frameId), e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Get exported frame preview
     *
     * @param exportId ...
     * @param frameId ...
     * @param response ...
     */
    @RequestMapping(path = "/export-frames/preview/{exportId}/{frameId}", produces = "image/jpeg")
    public void exportFramePreview(
            @PathVariable("exportId") String exportId,
            @PathVariable("frameId") Integer frameId,
            HttpServletResponse response
    ) {
        try (
                var inputStream = gifExporter.exportedFramePreviewInputStream(exportId, frameId)
        ) {
            StreamUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            logger.error(String.format("export frame preview error exportId=%s frameId=%d", exportId, frameId), e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
