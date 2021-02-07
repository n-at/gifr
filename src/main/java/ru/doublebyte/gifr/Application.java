package ru.doublebyte.gifr;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.doublebyte.gifr.components.FileManipulation;

@SpringBootApplication
public class Application implements CommandLineRunner {

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
    }

}
