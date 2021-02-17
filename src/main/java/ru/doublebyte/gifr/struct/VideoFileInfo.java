package ru.doublebyte.gifr.struct;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.doublebyte.gifr.configuration.VideoQualityPreset;
import ru.doublebyte.gifr.struct.mediainfo.AudioStreamInfo;
import ru.doublebyte.gifr.struct.mediainfo.StreamInfo;
import ru.doublebyte.gifr.struct.mediainfo.SubtitlesStreamInfo;
import ru.doublebyte.gifr.struct.mediainfo.VideoStreamInfo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VideoFileInfo {

    @JsonProperty("path")
    private final String path;

    @JsonProperty("id")
    private final String checksum;

    @JsonProperty("duration")
    private final double duration;

    @JsonProperty("video")
    private final VideoStreamInfo videoStream;

    @JsonProperty("audio")
    private final List<AudioStreamInfo> audioStreams;

    @JsonProperty("subtitles")
    private final List<SubtitlesStreamInfo> subtitlesStreams;

    ///////////////////////////////////////////////////////////////////////////

    public VideoFileInfo(String path, String checksum, List<StreamInfo> streams) {
        this.path = path;
        this.checksum = checksum;

        videoStream = streams.stream()
                .filter(StreamInfo::isVideo)
                .sorted()
                .findFirst()
                .orElse(null);
        if (videoStream == null) {
            throw new IllegalArgumentException("no video stream found");
        }
        duration = videoStream.getDuration();

        audioStreams = streams.stream()
                .filter(StreamInfo::isAudio)
                .sorted()
                .collect(Collectors.toUnmodifiableList());

        subtitlesStreams = streams.stream()
                .filter(StreamInfo::isSubtitles)
                .sorted()
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Duration formatted for DASH MPD file
     *
     * @return Formatted duration
     */
    @JsonIgnore
    public String getMpdDuration() {
        var totalSeconds = (int) duration;
        var milliseconds = (int) ((duration - totalSeconds) * 1000.0);
        var hours = (totalSeconds / 60) / 60;
        var minutes = (totalSeconds / 60) % 60;
        var seconds = totalSeconds % 60;
        return String.format("PT%dH%02dM%02d.%03dS", hours, minutes, seconds, milliseconds);
    }

    /**
     * Audio stream in source video file by DASH stream id
     * (first N streams in DASH are audio streams)
     *
     * @param streamId ...
     * @return ...
     */
    public AudioStreamInfo getAudioStreamByDashStreamId(String streamId) {
        if (audioStreams.isEmpty()) {
            throw new IllegalArgumentException("audio streams list is empty");
        }
        try {
            var streamIndex = Integer.parseInt(streamId);
            return audioStreams.get(streamIndex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get video quality preset by DASH stream id
     * (video streams in DASH are after audio streams)
     *
     * @param streamId ...
     * @return ...
     */
    public VideoQualityPreset getVideoQualityPresetByDashStreamId(String streamId) {
        try {
            var presetIndex = Integer.parseInt(streamId) - audioStreams.size();
            return getAvailableVideoQualityPresets().get(presetIndex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Video quality presets available for this video file (bounded by video height)
     *
     * @return ...
     */
    @JsonIgnore
    public List<VideoQualityPreset> getAvailableVideoQualityPresets() {
        final var videoStream = getVideoStream();

        return Arrays.stream(VideoQualityPreset.values())
                .filter(preset -> preset.getSize() <= videoStream.getHeight())
                .collect(Collectors.toUnmodifiableList());
    }

    ///////////////////////////////////////////////////////////////////////////

    public String getPath() {
        return path;
    }

    public String getChecksum() {
        return checksum;
    }

    public double getDuration() {
        return duration;
    }

    public VideoStreamInfo getVideoStream() {
        return videoStream;
    }

    public List<AudioStreamInfo> getAudioStreams() {
        return audioStreams;
    }

    public List<SubtitlesStreamInfo> getSubtitlesStreams() {
        return subtitlesStreams;
    }
}
