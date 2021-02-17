package ru.doublebyte.gifr.components;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import ru.doublebyte.gifr.configuration.SegmentParams;
import ru.doublebyte.gifr.configuration.VideoQualityPreset;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class DashEncoding {

    private final MediaEncoder mediaEncoder;
    private final MediaInfo mediaInfo;
    private final FileManipulation fileManipulation;

    private final Cache<String, Boolean> chunkAutoRemoval;

    public DashEncoding(
            MediaEncoder mediaEncoder,
            MediaInfo mediaInfo,
            FileManipulation fileManipulation,
            SegmentParams segmentParams
    ) {
        this.mediaEncoder = mediaEncoder;
        this.mediaInfo = mediaInfo;
        this.fileManipulation = fileManipulation;
        this.chunkAutoRemoval = Caffeine.newBuilder()
                .expireAfterWrite(segmentParams.getLifetime(), TimeUnit.SECONDS)
                .expireAfterAccess(segmentParams.getLifetime(), TimeUnit.SECONDS)
                .removalListener((chunkFileName, graph, cause) -> fileManipulation.removeChunkFile((String)chunkFileName))
                .build();
    }

    ///////////////////////////////////////////////////////////////////////////

    public InputStream getDashFileInputStream(String videoFileId) {
        if (!fileManipulation.dashFileExists(videoFileId)) {
            var videoFileInfo = mediaInfo.getByVideoFileId(videoFileId);
            if (videoFileInfo == null) {
                throw new IllegalStateException("video file info not found");
            }

            mediaEncoder.generateDashInit(videoFileInfo);
        }

        if (fileManipulation.dashFileExists(videoFileId)) {
            return fileManipulation.getDashFileInputStream(videoFileId);
        } else {
            throw new IllegalStateException("dash file not found or not readable");
        }
    }

    public InputStream getInitFileInputStream(String videoFileId, String streamId) {
        if (fileManipulation.initFileExists(videoFileId, streamId)) {
            return fileManipulation.getInitFileInputStream(videoFileId, streamId);
        } else {
            throw new IllegalStateException("init file not found or not readable");
        }
    }

    public InputStream getChunkFileInputStream(String videoFileId, String streamId, String chunkId) {
        chunkAutoRemoval.put(fileManipulation.getChunkFileName(videoFileId, streamId, chunkId), true);

        if (!fileManipulation.chunkFileExists(videoFileId, streamId, chunkId)) {
            var videoFileInfo = mediaInfo.getByVideoFileId(videoFileId);
            if (videoFileInfo == null) {
                throw new IllegalStateException("video file info not found");
            }

            switch (streamId) {
                case "0":
                    mediaEncoder.generateAudioSegment(videoFileInfo, streamId, chunkId);
                    break;
                case "1":
                case "2":
                case "3":
                case "4":
                case "5":
                    mediaEncoder.generateVideoSegment(videoFileInfo, streamId, chunkId);
                    break;
                default:
                    throw new IllegalArgumentException("illegal stream id");
            }
        }

        if (fileManipulation.chunkFileExists(videoFileId, streamId, chunkId)) {
            return fileManipulation.getChunkFileInputStream(videoFileId, streamId, chunkId);
        } else {
            throw new IllegalStateException("chunk file not found or not readable");
        }
    }

}
