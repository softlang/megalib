/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer.exceptions;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class MegaModelVisualizerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MegaModelVisualizerException(String message) {
        super(message);
    }

    public MegaModelVisualizerException(Throwable cause) {
        super(cause);
    }
}
