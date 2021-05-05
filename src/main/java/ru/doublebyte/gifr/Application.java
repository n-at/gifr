package ru.doublebyte.gifr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.doublebyte.gifr.components.CommandlineExecutor;
import ru.doublebyte.gifr.components.FileManipulation;
import ru.doublebyte.gifr.configuration.FFMPEGParams;
import ru.doublebyte.gifr.struct.CommandlineArguments;

import javax.annotation.PreDestroy;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Value("${server.address}")
    private String serverHost;

    @Value("${server.port}")
    private String serverPort;

    private final FileManipulation fileManipulation;
    private final CommandlineExecutor commandlineExecutor;
    private final FFMPEGParams ffmpegParams;

    public Application(FileManipulation fileManipulation, CommandlineExecutor commandlineExecutor, FFMPEGParams ffmpegParams) {
        this.fileManipulation = fileManipulation;
        this.commandlineExecutor = commandlineExecutor;
        this.ffmpegParams = ffmpegParams;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Override
    public void run(String... args) {
        fileManipulation.ensureDirectoriesExist();
        cleanupTmpFiles();

        if (!check(ffmpegParams.getFFMPEGBinary())) {
            var ffmpeg = Paths.get("ffmpeg");
            if (!Files.exists(ffmpeg)) {
                ffmpeg = Paths.get("ffmpeg.exe");
                if (!Files.exists(ffmpeg)) {
                    throw new RuntimeException("ffmpeg binary not found");
                }
            }

            var ffmpegAbsolutePath = ffmpeg.toAbsolutePath().toString();
            if (!check(ffmpegAbsolutePath)) {
                throw new RuntimeException("ffmpeg check failed");
            }

            ffmpegParams.setFFMPEGBinary(ffmpegAbsolutePath);
        }

        if (!check(ffmpegParams.getFFProbeBinary())) {
            var ffprobe = Paths.get("ffprobe");
            if (!Files.exists(ffprobe)) {
                ffprobe = Paths.get("ffprobe.exe");
                if (!Files.exists(ffprobe)) {
                    throw new RuntimeException("ffprobe binary not found");
                }
            }

            var ffprobeAbsolutePath = ffprobe.toAbsolutePath().toString();
            if (!check(ffprobeAbsolutePath)) {
                throw new RuntimeException("ffprobe check failed");
            }

            ffmpegParams.setFFProbeBinary(ffprobeAbsolutePath);
        }

        logger.info("");
        logger.info("");
        logger.info("http://{}:{}/", serverHost, serverPort);
        logger.info("");
        logger.info("");
    }

    @PreDestroy
    private void cleanupTmpFiles() {
        fileManipulation.removeEncodedChunks();
        fileManipulation.removeExportFiles();
    }

    private boolean check(String binary) {
        try {
            commandlineExecutor.execute(new CommandlineArguments(binary).add("-version"));
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

}
