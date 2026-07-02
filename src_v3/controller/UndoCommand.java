package controller;

import model.LibraryItem;

public class UndoCommand {
    public enum CommandType {
        ADD_RESOURCE,
        REMOVE_RESOURCE
    }

    private CommandType type;
    private LibraryItem resource;

    public UndoCommand(CommandType type, LibraryItem resource) {
        this.type = type;
        this.resource = resource;
    }

    public CommandType getType() {
        return type;
    }

    public LibraryItem getResource() {
        return resource;
    }
}
