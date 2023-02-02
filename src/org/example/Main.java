package org.example;

import org.example.ui.reversi.UserInterface;

public class Main {
    public static void main(String[] args) {
        try (var ui = new UserInterface()) {
            ui.start();
        }
    }
}