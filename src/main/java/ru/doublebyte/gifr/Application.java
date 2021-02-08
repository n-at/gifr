package ru.doublebyte.gifr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StreamUtils;
import ru.doublebyte.gifr.components.FileManipulation;

import java.nio.charset.Charset;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private final FileManipulation fileManipulation;

    public Application(FileManipulation fileManipulation) {
        this.fileManipulation = fileManipulation;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Override
    public void run(String... args) {
        fileManipulation.ensureDirectoriesExist();
        fileManipulation.removeEncodedChunks();
        which("timeout");
        which("ffprobe");
        which("ffmpeg");
    }

    protected void which(String program) {
        try {
            var process = new ProcessBuilder("/bin/sh", "-c", "which " + program)
                    .redirectErrorStream(true)
                    .start();
            var output = StreamUtils.copyToString(process.getInputStream(), Charset.defaultCharset());
            var exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new Exception("exit code=" + exitCode);
            }
            logger.info("Using {} from {}", program, output.trim());
        } catch (Exception e) {
            logger.error("program test error " + program, e);
            System.exit(1);
        }
    }

}
