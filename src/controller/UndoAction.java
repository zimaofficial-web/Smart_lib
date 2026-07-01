package controller;

import model.LibraryItem;

/**
 * Represents an action that can be undone in the admin panel.
 */
public class UndoAction {
    public enum ActionType { ADD, DELETE }

    private ActionType type;
    private LibraryItem item;

    public UndoAction(ActionType type, LibraryItem item) {
        this.type = type;
        this.item = item;
    }

    public ActionType getType() { return type; }
    public LibraryItem getItem()  { return item; }
}
