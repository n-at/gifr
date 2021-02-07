package ru.doublebyte.gifr.components;

import org.springframework.scheduling.annotation.Scheduled;
import ru.doublebyte.gifr.configuration.VideoQualityPreset;
import ru.doublebyte.gifr.struct.VideoFileInfo;

import java.io.InputStream;

public class DashEncoding {

    private final MediaEncoder mediaEncoder;
    private final MediaInfo mediaInfo;
    private final FileManipulation fileManipulation;

    public DashEncoding(
            MediaEncoder mediaEncoder,
            MediaInfo mediaInfo,
            FileManipulation fileManipulation
    ) {
        this.mediaEncoder = mediaEncoder;
        this.mediaInfo = mediaInfo;
        this.fileManipulation = fileManipulation;
    }

    ///////////////////////////////////////////////////////////////////////////

    public void open(VideoFileInfo videoFileInfo) {
        mediaEncoder.generateDashInit(videoFileInfo);
    }

    public InputStream getDashFileInputStream(String videoFileId) {
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
                    mediaEncoder.generateVideoSegment(videoFileInfo, streamId, VideoQualityPreset.Video360, chunkId);
                    break;

                case "2":
                    mediaEncoder.generateVideoSegment(videoFileInfo, streamId, VideoQualityPreset.Video430, chunkId);
                    break;

                case "3":
                    mediaEncoder.generateVideoSegment(videoFileInfo, streamId, VideoQualityPreset.Video540, chunkId);
                    break;

                case "4":
                    mediaEncoder.generateVideoSegment(videoFileInfo, streamId, VideoQualityPreset.Video720, chunkId);
                    break;

                case "5":
                    mediaEncoder.generateVideoSegment(videoFileInfo, streamId, VideoQualityPreset.Video1080, chunkId);
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

    ///////////////////////////////////////////////////////////////////////////

    @Scheduled(fixedDelayString = "${segment-params.lifetime}")
    public void removeOldChunks() {
        fileManipulation.removeOldChunks();
    }

}
