package ru.doublebyte.gifr.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import java.nio.charset.Charset;

public class TimeoutCommandlineExecutor {

    private static final Logger logger = LoggerFactory.getLogger(TimeoutCommandlineExecutor.class);

    private static final int DefaultKillTimeout = 5;
    private static final int DefaultExecutionTimeout = 5;

    ///////////////////////////////////////////////////////////////////////////

    public String execute(String commandline) {
        return execute(commandline, DefaultExecutionTimeout);
    }

    public String execute(String commandline, int timeout) {
        return execute(commandline, timeout, false);
    }

    /**
     * Execute command and capture output to string
     *
     * @param commandline Command
     * @param timeout Execution timeout (in seconds)
     * @param captureErrors Capture error stream
     * @return Command output
     */
    public String execute(String commandline, int timeout, boolean captureErrors) {
        try {
            logger.debug("commandline timeout={}s: {}", timeout, commandline);

            var process = new ProcessBuilder("/bin/sh", "-c", buildTimeoutCommandline(commandline, timeout))
                    .redirectErrorStream(captureErrors)
                    .start();

            var output = StreamUtils.copyToString(process.getInputStream(), Charset.defaultCharset());

            var exitCode = process.waitFor();
            if (exitCode != 0) {
                logger.info(output);
                throw new RuntimeException("process exit code != 0");
            }

            return output;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    protected String buildTimeoutCommandline(String commandline) {
        return buildTimeoutCommandline(commandline, DefaultExecutionTimeout);
    }

    protected String buildTimeoutCommandline(String commandline, int timeout) {
        return buildTimeoutCommandline(commandline, timeout, DefaultKillTimeout);
    }

    /**
     * Build command with limited time for execution
     *
     * @param commandline Command to execute
     * @param timeout Timeout for execution (in seconds)
     * @param killTimeout Timeout to send KILL signal (in seconds)
     * @return Command
     */
    protected String buildTimeoutCommandline(String commandline, int timeout, int killTimeout) {
        return String.format("timeout --kill-after=%d %d", killTimeout, timeout) + " " + commandline;
    }

}
