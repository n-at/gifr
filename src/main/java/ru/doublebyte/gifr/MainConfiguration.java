package ru.doublebyte.gifr;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.doublebyte.gifr.components.FileSystemNavigator;
import ru.doublebyte.gifr.components.MediaInfo;
import ru.doublebyte.gifr.components.TimeoutCommandlineExecutor;
import ru.doublebyte.gifr.configuration.FileSystemNavigatorConfiguration;

@Configuration
public class MainConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "file-system-navigator")
    public FileSystemNavigatorConfiguration fileSystemNavigatorConfiguration() {
        return new FileSystemNavigatorConfiguration();
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
    public MediaInfo mediaInfo() {
        return new MediaInfo(timeoutCommandlineExecutor());
    }

}
