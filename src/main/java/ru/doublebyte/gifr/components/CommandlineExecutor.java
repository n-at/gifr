package ru.doublebyte.gifr.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.doublebyte.gifr.struct.CommandlineArguments;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class CommandlineExecutor {

    private static final Logger logger = LoggerFactory.getLogger(CommandlineExecutor.class);

    private static final int DefaultKillTimeout = 5;

    ///////////////////////////////////////////////////////////////////////////

    public CommandlineExecutor() {

    }

    ///////////////////////////////////////////////////////////////////////////

    public void execute(CommandlineArguments arguments) {
        execute(arguments, DefaultKillTimeout);
    }

    public void execute(CommandlineArguments arguments, int timeout) {
        execute(arguments, timeout, null);
    }

    /**
     * Execute command
     *
     * @param arguments Commandline arguments
     * @param timeout Execution timeout (in seconds)
     * @param directory Path to working directory
     */
    public void execute(CommandlineArguments arguments, int timeout, String directory) {
        try {
            logger.debug("command execution timeout={} {}", timeout, arguments.getArguments());

            var process = new ProcessBuilder(arguments.getArguments())
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .directory(directory == null ? null : Paths.get(directory).toFile())
                    .start();

            var finished = process.waitFor(timeout, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new Exception("process terminated due timeout");
            } else if (process.exitValue() != 0) {
                throw new Exception("process exit code=" + process.exitValue());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    public String output(CommandlineArguments arguments) {
        return output(arguments, DefaultKillTimeout);
    }

    public String output(CommandlineArguments arguments, int timeout) {
        return output(arguments, timeout, false);
    }

    /**
     * Execute command and capture output to string
     *
     * @param arguments Command and arguments
     * @param timeout Execution timeout (in seconds)
     * @param captureErrors Capture error stream in output
     * @return Execution output
     */
    public String output(CommandlineArguments arguments, int timeout, boolean captureErrors) {
        try {
            logger.debug("command execution timeout={} captureErrors={} {}", timeout, captureErrors, arguments.getArguments());

            var process = new ProcessBuilder(arguments.getArguments())
                    .redirectErrorStream(captureErrors)
                    .start();

            var finished = process.waitFor(timeout, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new Exception("process terminated due timeout");
            } else if (process.exitValue() != 0) {
                throw new Exception("process exit code=" + process.exitValue());
            }

            return new String(process.getInputStream().readAllBytes(), Charset.defaultCharset());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
