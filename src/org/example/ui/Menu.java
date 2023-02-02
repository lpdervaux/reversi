package org.example.ui;

/**
 * Defines choice and description of a menu item.
 * Meant for use with {@code UserInterface.promptUntilMenuChoice}, which uses enumerations of this interface to implement simple menus.
 */
public interface Menu {
    String choice();
    String description();
}
