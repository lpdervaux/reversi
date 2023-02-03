package org.example.ui;

import java.io.*;
import java.util.Scanner;

/**
 * A user interface that scans standard input.
 * <p>
 * Correctly initializes and disposes of a {@code Scanner} bound to {@code System.in}.
 */
public abstract class ScannerUserInterface implements AutoCloseable {
    protected final Scanner stdin;

    /**
     * Instantiates instance variable {@code stdin} with a new {@code Scanner}.
     */
    /*
     * A Scanner is not reclaimable by garbage collection until closed
     * However, closing a Scanner bound to System.in will also close System.in
     * Hence the need for FilterInputStream at instantiation
     */
    protected ScannerUserInterface() {
        this.stdin = new Scanner(
            new FilterInputStream(System.in) {
                @Override
                public void close() {}
            }
        );
    }

    /**
     * Closes associated {@code Scanner}.
     */
    @Override
    public void close() {
        stdin.close();
    }
}
