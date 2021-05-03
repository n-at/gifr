package ru.doublebyte.gifr.components;

import ru.doublebyte.gifr.configuration.FileSystemNavigatorConfiguration;
import ru.doublebyte.gifr.struct.FileSystemEntry;

import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileSystemNavigator {

    private final FileSystemNavigatorConfiguration configuration;

    public FileSystemNavigator(FileSystemNavigatorConfiguration configuration) {
        this.configuration = configuration;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * List given directory contents
     *
     * @param browsePath Path to list
     * @return ...
     */
    public List<FileSystemEntry> listDirectory(String browsePath) {
        if (browsePath == null || browsePath.isEmpty()) {
            browsePath = configuration.getDefaultStartDirectory();
        }

        var startPath = Paths.get(browsePath);

        try {
            return Files.walk(Paths.get(browsePath), 1, FileVisitOption.FOLLOW_LINKS)
                    .filter(path -> {
                        var file = path.toFile();
                        if (!file.exists()) {
                            return false;
                        }
                        if (file.isHidden()) {
                            return false;
                        }
                        if (!Files.isReadable(path)) {
                            return false;
                        }
                        return !path.equals(startPath);
                    })
                    .map(path -> {
                        var entry = new FileSystemEntry();
                        entry.setFullPath(path.toAbsolutePath().toString());
                        entry.setName(path.getFileName().toString());
                        entry.setType(path.toFile().isDirectory() ? FileSystemEntry.Type.Directory : FileSystemEntry.Type.File);
                        return entry;
                    })
                    .sorted((entryA, entryB) -> {
                        if (entryA.getType() != entryB.getType()) {
                            return entryA.getType() == FileSystemEntry.Type.Directory ? -1 : 1;
                        }
                        return entryA.getName().compareToIgnoreCase(entryB.getName());
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Default path from configuration
     *
     * @return ...
     */
    public String getDefaultPath() {
        return Paths.get(configuration.getDefaultStartDirectory()).toAbsolutePath().toString();
    }

}
