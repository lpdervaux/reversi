package org.example;

import org.example.ui.reversi.UserInterface;

/**
 * Entry point that starts the user interface.
 */
public class StartUI {
    public static void main(String[] args) {
        try (
            var ui = new UserInterface()
        ) {
            ui.start();
        }
    }
}