package net.treset.worldmanager.manager;

public class CommandCallback {
    public enum Type {
        SUCCESS,
        FAILURE
    }

    private final Type type;
    private final String message;

    public CommandCallback(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
