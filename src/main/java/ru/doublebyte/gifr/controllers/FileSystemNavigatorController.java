package ru.doublebyte.gifr.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.doublebyte.gifr.components.FileSystemNavigator;
import ru.doublebyte.gifr.struct.response.FileSystemNavigatorResponse;
import ru.doublebyte.gifr.struct.response.Response;

import java.nio.file.Paths;

@RestController
public class FileSystemNavigatorController {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemNavigatorController.class);

    private final FileSystemNavigator fileSystemNavigator;

    public FileSystemNavigatorController(FileSystemNavigator fileSystemNavigator) {
        this.fileSystemNavigator = fileSystemNavigator;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * List contents of given directory
     *
     * @param path Path to list, when empty, use default path from configuration
     * @return ...
     */
    @RequestMapping("/file-system-navigator/list")
    public Response listFiles(@RequestParam(name = "path", required = false) String path) {
        try {
            if (path == null || path.isEmpty()) {
                path = fileSystemNavigator.getDefaultPath();
            }

            logger.info("list files {}", path);

            var previousPath = Paths.get(path).getParent().toAbsolutePath().toString();

            return FileSystemNavigatorResponse.success(path, previousPath, fileSystemNavigator.listDirectory(path));
        } catch (Exception e) {
            logger.error("list files error " + path, e);
            return Response.error(e.getMessage());
        }
    }

}
