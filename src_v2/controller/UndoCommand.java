package controller;

import model.TravelResource;

public class UndoCommand {
    public enum CommandType {
        ADD_RESOURCE,
        REMOVE_RESOURCE
    }

    private CommandType type;
    private TravelResource resource;

    public UndoCommand(CommandType type, TravelResource resource) {
        this.type = type;
        this.resource = resource;
    }

    public CommandType getType() {
        return type;
    }

    public TravelResource getResource() {
        return resource;
    }
}
