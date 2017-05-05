/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer.exceptions;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class CommandLineException extends MegaModelVisualizerException {

    public CommandLineException(String message) {
        super(message);
    }

    public CommandLineException(Throwable cause) {
        super(cause);
    }
}
