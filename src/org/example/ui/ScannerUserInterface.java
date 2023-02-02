package org.example.ui;

import java.io.*;
import java.util.Scanner;

/**
 * A user interface that scans standard input.
 *
 * Provides a correctly initialized {@code Scanner} on {@code System.in} and implements {@code AutoCloseable} to close it.
 */
public abstract class ScannerUserInterface implements AutoCloseable {
    /*
     * Scanner is not reclaimable by garbage collection until closed.
     * However, closing Scanner while bound to System.in will also close System.in.
     * Hence, the need for FilterInputStream at instantiation and explicit call to close()
     */
    protected final Scanner stdin;

    /**
     * Instantiates instance variable {@code stdin} with a new {@code Scanner}.
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
