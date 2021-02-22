package ru.doublebyte.gifr;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.doublebyte.gifr.components.CommandlineExecutor;
import ru.doublebyte.gifr.components.FileManipulation;
import ru.doublebyte.gifr.configuration.FFMPEGParams;
import ru.doublebyte.gifr.struct.CommandlineArguments;

@SpringBootApplication
public class Application implements CommandLineRunner {

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
        fileManipulation.removeEncodedChunks();
        commandlineExecutor.execute(new CommandlineArguments(ffmpegParams.getFFMPEGBinary()).add("-version"));
        commandlineExecutor.execute(new CommandlineArguments(ffmpegParams.getFFProbeBinary()).add("-version"));
    }

}
