package ru.doublebyte.gifr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.doublebyte.gifr.components.CommandlineExecutor;
import ru.doublebyte.gifr.components.FileManipulation;
import ru.doublebyte.gifr.struct.CommandlineArguments;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private final FileManipulation fileManipulation;
    private final CommandlineExecutor commandlineExecutor;

    public Application(FileManipulation fileManipulation, CommandlineExecutor commandlineExecutor) {
        this.fileManipulation = fileManipulation;
        this.commandlineExecutor = commandlineExecutor;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Override
    public void run(String... args) {
        fileManipulation.ensureDirectoriesExist();
        fileManipulation.removeEncodedChunks();
        which("ffprobe");
        which("ffmpeg");
    }

    protected void which(String program) {
        try {
            var output = commandlineExecutor.execute(new CommandlineArguments("which").add(program));
            logger.info("Using {} from {}", program, output.trim());
        } catch (Exception e) {
            logger.error("program test error " + program, e);
            System.exit(1);
        }
    }

}
