package ru.doublebyte.gifr.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.doublebyte.gifr.configuration.SegmentParams;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManipulation {

    private static final Logger logger = LoggerFactory.getLogger(FileManipulation.class);

    private final SegmentParams segmentParams;

    public FileManipulation(SegmentParams segmentParams) {
        this.segmentParams = segmentParams;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Create output and chunk directory if it does not exist
     */
    public void ensureDirectoriesExist() {
        ensureDirectoryExists(segmentParams.getDashOutputPath());
        ensureDirectoryExists(segmentParams.getChunkOutputPath());
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
        return String.format("%s-%s", videoFileId, segmentParams.getDashFileName());
    }

    public Path getDashFilePath(String videoFileId) {
        return Paths.get(segmentParams.getDashOutputPath(), getDashFileName(videoFileId));
    }

    public boolean dashFileExists(String videoFileId) {
        return Files.exists(getDashFilePath(videoFileId));
    }

    public InputStream getDashFileInputStream(String videoFileId) {
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
        return String.format("init-%s-%s.m4s", videoFileId, streamId);
    }

    public Path getInitFilePath(String videoFileId, String streamId) {
        return Paths.get(segmentParams.getDashOutputPath(), getInitFileName(videoFileId, streamId));
    }

    public boolean initFileExists(String videoFileId, String streamId) {
        return Files.exists(getInitFilePath(videoFileId, streamId));
    }

    public InputStream getInitFileInputStream(String videoFileId, String streamId) {
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
        return String.format("subtitles-%s-%s.vtt", videoFileId, streamId);
    }

    public Path getSubtitlesFilePath(String videoFileId, String streamId) {
        return Paths.get(segmentParams.getDashOutputPath(), getSubtitlesFileName(videoFileId, streamId));
    }

    public boolean subtitlesFileExists(String videoFileId, String streamId) {
        return Files.exists(getSubtitlesFilePath(videoFileId, streamId));
    }

    public InputStream getSubtitlesFileInputStream(String videoFileId, String streamId) {
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
        return String.format("chunk-%s-%s-%s.m4s", videoFileId, streamId, chunkId);
    }

    public Path getChunkFilePath(String videoFileId, String streamId, String chunkId) {
        return Paths.get(segmentParams.getChunkOutputPath(), getChunkFileName(videoFileId, streamId, chunkId));
    }

    public boolean chunkFileExists(String videoFileId, String streamId, String chunkId) {
        return Files.exists(getChunkFilePath(videoFileId, streamId, chunkId));
    }

    public InputStream getChunkFileInputStream(String videoFileId, String streamId, String chunkId) {
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

}
