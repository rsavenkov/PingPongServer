package ru.glumobile.server;

import java.util.Map;

/**
 * Created by Administrator on 14.05.15.
 */
public class Protocol {

    public enum Command {
        ping,
        pong
    }

    private Command command;
    private Map<String, String> args;

    public Protocol() {

    }

    public Protocol(Command command, Map<String, String> args) {
        this.command = command;
        this.args = args;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        this.args = args;
    }
}
