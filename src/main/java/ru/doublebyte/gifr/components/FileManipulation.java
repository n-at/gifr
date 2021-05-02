package ru.doublebyte.gifr.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.doublebyte.gifr.configuration.ExportParams;
import ru.doublebyte.gifr.configuration.SegmentParams;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManipulation {

    private static final Logger logger = LoggerFactory.getLogger(FileManipulation.class);

    private final SegmentParams segmentParams;
    private final ExportParams exportParams;

    public FileManipulation(SegmentParams segmentParams, ExportParams exportParams) {
        this.segmentParams = segmentParams;
        this.exportParams = exportParams;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Create output and chunk directory if it does not exist
     */
    public void ensureDirectoriesExist() {
        ensureDirectoryExists(segmentParams.getDashOutputPath());
        ensureDirectoryExists(segmentParams.getChunkOutputPath());
        ensureDirectoryExists(exportParams.getPath());
    }

    /**
     * Remove existing encoded chunks from output directory
     */
    public void removeEncodedChunksFromDashOutput() {
        removeAllChunksFromDirectory(segmentParams.getDashOutputPath());
    }

    /**
     * Remove all chunks from chunks output directory
     */
    public void removeEncodedChunks() {
        removeAllChunksFromDirectory(segmentParams.getChunkOutputPath());
    }

    ///////////////////////////////////////////////////////////////////////////

    public String getDashFileName(String videoFileId) {
        ensureVideoFileIdIsValid(videoFileId);
        return String.format("%s-%s", videoFileId, segmentParams.getDashFileName());
    }

    public Path getDashFilePath(String videoFileId) {
        ensureVideoFileIdIsValid(videoFileId);
        return Paths.get(segmentParams.getDashOutputPath(), getDashFileName(videoFileId));
    }

    public boolean dashFileExists(String videoFileId) {
        ensureVideoFileIdIsValid(videoFileId);
        return Files.exists(getDashFilePath(videoFileId));
    }

    public InputStream getDashFileInputStream(String videoFileId) {
        ensureVideoFileIdIsValid(videoFileId);
        if (!dashFileExists(videoFileId)) {
            throw new IllegalArgumentException("dash file does not exist");
        }
        try {
            return Files.newInputStream(getDashFilePath(videoFileId));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    public String getInitFileName(String videoFileId, String streamId) {
        ensureVideoFileIdIsValid(videoFileId);
        ensureStreamIdIsValid(streamId);
        return String.format("init-%s-%s.m4s", videoFileId, streamId);
    }

    public Path getInitFilePath(String videoFileId, String streamId) {
        ensureVideoFileIdIsValid(videoFileId);
        ensureStreamIdIsValid(streamId);
        return Paths.get(segmentParams.getDashOutputPath(), getInitFileName(videoFileId, streamId));
    }

    public boolean initFileExists(String videoFileId, String streamId) {
        ensureVideoFileIdIsValid(videoFileId);
        ensureStreamIdIsValid(streamId);
        return Files.exists(getInitFilePath(videoFileId, streamId));
    }

    public InputStream getInitFileInputStream(String videoFileId, String streamId) {
        ensureVideoFileIdIsValid(videoFileId);
        ensureStreamIdIsValid(streamId);
        if (!initFileExists(videoFileId, streamId)) {
            throw new IllegalArgumentException("init file does not exist");
        }
        try {
            return Files.newInputStream(getInitFilePath(videoFileId, streamId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    public String getSubtitlesFileName(String videoFileId, String streamId) {
        ensureVideoFileIdIsValid(videoFileId);
        ensureStreamIdIsValid(streamId);
        return String.format("subtitles-%s-%s.vtt", videoFileId, streamId);
    }

    public Path getSubtitlesFilePath(String videoFileId, String streamId) {
        ensureVideoFileIdIsValid(videoFileId);
        ensureStreamIdIsValid(streamId);
        return Paths.get(segmentParams.getDashOutputPath(), getSubtitlesFileName(videoFileId, streamId));
    }

    public boolean subtitlesFileExists(String videoFileId, String streamId) {
        ensureVideoFileIdIsValid(videoFileId);
        ensureStreamIdIsValid(streamId);
        return Files.exists(getSubtitlesFilePath(videoFileId, streamId));
    }

    public InputStream getSubtitlesFileInputStream(String videoFileId, String streamId) {
        ensureVideoFileIdIsValid(videoFileId);
        ensureStreamIdIsValid(streamId);
        if (!subtitlesFileExists(videoFileId, streamId)) {
            throw new IllegalArgumentException("subtitles file does not exist");
        }
        try {
            return Files.newInputStream(getSubtitlesFilePath(videoFileId, streamId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    public String getChunkFileName(String videoFileId, String streamId, String chunkId) {
        ensureVideoFileIdIsValid(videoFileId);
        ensureStreamIdIsValid(streamId);
        ensureChunkIdIsValid(chunkId);
        return String.format("chunk-%s-%s-%s.m4s", videoFileId, streamId, chunkId);
    }

    public Path getChunkFilePath(String videoFileId, String streamId, String chunkId) {
        ensureVideoFileIdIsValid(videoFileId);
        ensureStreamIdIsValid(streamId);
        ensureChunkIdIsValid(chunkId);
        return Paths.get(segmentParams.getChunkOutputPath(), getChunkFileName(videoFileId, streamId, chunkId));
    }

    public boolean chunkFileExists(String videoFileId, String streamId, String chunkId) {
        ensureVideoFileIdIsValid(videoFileId);
        ensureStreamIdIsValid(streamId);
        ensureChunkIdIsValid(chunkId);
        return Files.exists(getChunkFilePath(videoFileId, streamId, chunkId));
    }

    public InputStream getChunkFileInputStream(String videoFileId, String streamId, String chunkId) {
        ensureVideoFileIdIsValid(videoFileId);
        ensureStreamIdIsValid(streamId);
        ensureChunkIdIsValid(chunkId);
        if (!chunkFileExists(videoFileId, streamId, chunkId)) {
            throw new IllegalArgumentException("chunk file does not exist");
        }
        try {
            return Files.newInputStream(getChunkFilePath(videoFileId, streamId, chunkId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Remove one encoded chunk
     *
     * @param chunkFileName ...
     */
    public void removeChunkFile(String chunkFileName) {
        try {
            var path = Paths.get(segmentParams.getChunkOutputPath(), chunkFileName);
            if (Files.exists(path)) {
                logger.info("remove old chunk: {}", chunkFileName);
                Files.delete(path);
            } else {
                logger.warn("chunk does not exist: {}", chunkFileName);
            }
        } catch (Exception e) {
            logger.error("chunk delete error " + chunkFileName, e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    public String getOutputGifFileName(String exportId) {
        ensureExportIdIsValid(exportId);
        return String.format("%s.gif", exportId);
    }

    public Path getOutputGifFilePath(String exportId) {
        ensureExportIdIsValid(exportId);
        return Paths.get(exportParams.getPath(), getOutputGifFileName(exportId));
    }

    public Path getOutputGifPalettePath(String exportId) {
        ensureExportIdIsValid(exportId);
        return Paths.get(exportParams.getPath(), String.format("%s-palette.png", exportId));
    }

    ///////////////////////////////////////////////////////////////////////////

    public String getOutputFramesPath(String exportId) {
        ensureExportIdIsValid(exportId);
        var path = Paths.get(exportParams.getPath(), exportId).toString();
        ensureDirectoryExists(path);
        return path;
    }

    public String getOutputFramesFileNameTemplate(String exportId) {
        ensureExportIdIsValid(exportId);
        return Paths.get(getOutputFramesPath(exportId), "%010d.png").toString();
    }

    public int getOutputFrameCount(String exportId) {
        try {
            ensureExportIdIsValid(exportId);
            var framesPath = Paths.get(getOutputFramesPath(exportId));

            return (int) Files.walk(framesPath)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.matches("^\\d+\\.png$"))
                    .count();
        } catch (Exception e) {
            logger.warn("output frames count error {}: {}", exportId, e.getMessage());
            return 0;
        }
    }

    public Path getOutputFramePath(String exportId, int frameId) {
        ensureExportIdIsValid(exportId);
        var fileName = String.format("%010d.png", frameId);
        return Paths.get(getOutputFramesPath(exportId), fileName);
    }

    public boolean outputFrameExists(String exportId, int frameId) {
        ensureExportIdIsValid(exportId);
        return Files.exists(getOutputFramePath(exportId, frameId));
    }

    public Path getOutputFramePreviewPath(String exportId, int frameId) {
        ensureExportIdIsValid(exportId);
        var fileName = String.format("%010d.preview.jpg", frameId);
        return Paths.get(getOutputFramesPath(exportId), fileName);
    }

    public boolean outputFramePreviewExists(String exportId, int frameId) {
        ensureExportIdIsValid(exportId);
        return Files.exists(getOutputFramePreviewPath(exportId, frameId));
    }

    ///////////////////////////////////////////////////////////////////////////

    private void ensureDirectoryExists(String directoryPath) {
        var path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (Exception e) {
                logger.error("unable to create " + directoryPath, e);
                throw new RuntimeException(e);
            }
        }
    }

    private void removeAllChunksFromDirectory(String directory) {
        try {
            Files.walk(Paths.get(directory))
                    .filter(path -> path.getFileName().toString().startsWith("chunk-"))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (Exception e) {
                            logger.error("file delete error: " + path, e);
                        }
                    });
        } catch (Exception e) {
            logger.error("chunks delete error", e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    private void ensureVideoFileIdIsValid(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("empty video file id");
        }
        if (!id.matches("^[a-zA-Z0-9-]+$")) {
            throw new IllegalArgumentException("incorrect video file id format");
        }
    }

    private void ensureStreamIdIsValid(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("empty stream id");
        }
        if (!id.matches("^[0-9]+$")) {
            throw new IllegalArgumentException("incorrect stream id");
        }
    }

    private void ensureChunkIdIsValid(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("empty chunk id");
        }
        if (!id.matches("^[0-9]+$")) {
            throw new IllegalArgumentException("incorrect chunk id");
        }
    }

    private void ensureExportIdIsValid(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("empty export id");
        }
        if (!id.matches("^[a-zA-Z0-9-]+$")) {
            throw new IllegalArgumentException("incorrect export id format");
        }
    }

}
