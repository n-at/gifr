package ru.doublebyte.gifr.struct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandlineArguments {

    private final List<String> arguments = new ArrayList<>();

    ///////////////////////////////////////////////////////////////////////////

    public CommandlineArguments(String command) {
        arguments.add(command);
    }

    public List<String> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    ///////////////////////////////////////////////////////////////////////////

    public CommandlineArguments add(String argument, String value) {
        arguments.add(argument);
        arguments.add(value);
        return this;
    }

    public CommandlineArguments add(String argument) {
        arguments.add(argument);
        return this;
    }

}
