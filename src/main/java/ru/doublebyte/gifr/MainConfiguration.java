package ru.doublebyte.gifr;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.doublebyte.gifr.components.*;
import ru.doublebyte.gifr.configuration.*;

@Configuration
@EnableScheduling
public class MainConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "file-system-navigator")
    public FileSystemNavigatorConfiguration fileSystemNavigatorConfiguration() {
        return new FileSystemNavigatorConfiguration();
    }

    @Bean
    @ConfigurationProperties(prefix = "global-video-encoding-params")
    public GlobalVideoEncodingParams globalVideoEncodingParams() {
        return new GlobalVideoEncodingParams();
    }

    @Bean
    @ConfigurationProperties(prefix = "global-audio-encoding-params")
    public GlobalAudioEncodingParams globalAudioEncodingParams() {
        return new GlobalAudioEncodingParams();
    }

    @Bean
    @ConfigurationProperties(prefix = "segment-params")
    public SegmentParams segmentParams() {
        return new SegmentParams();
    }

    @Bean
    @ConfigurationProperties(prefix = "export-params")
    public ExportParams exportParams() {
        return new ExportParams();
    }

    @Bean
    public FileSystemNavigator fileSystemNavigator() {
        return new FileSystemNavigator(fileSystemNavigatorConfiguration());
    }

    @Bean
    public TimeoutCommandlineExecutor timeoutCommandlineExecutor() {
        return new TimeoutCommandlineExecutor();
    }

    @Bean
    public CommandlineExecutor commandlineExecutor() {
        return new CommandlineExecutor();
    }

    @Bean
    public MediaInfo mediaInfo() {
        return new MediaInfo(commandlineExecutor());
    }

    @Bean
    public FileManipulation fileManipulation() {
        return new FileManipulation(segmentParams());
    }

    @Bean
    public MediaEncoder mediaEncoder() {
        return new MediaEncoder(
                commandlineExecutor(),
                globalVideoEncodingParams(),
                globalAudioEncodingParams(),
                segmentParams(),
                fileManipulation()
        );
    }

    @Bean
    public DashEncoding dashEncoding() {
        return new DashEncoding(mediaEncoder(), mediaInfo(), fileManipulation(), segmentParams());
    }

    @Bean
    public GifExporter gifExporter() {
        return new GifExporter(timeoutCommandlineExecutor(), exportParams());
    }

}
