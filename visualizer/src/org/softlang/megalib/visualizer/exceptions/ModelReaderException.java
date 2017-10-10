/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer.exceptions;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class ModelReaderException extends MegaModelVisualizerException {

	private static final long serialVersionUID = 1L;

	public ModelReaderException(String message) {
        super(message);
    }

    public ModelReaderException(Throwable cause) {
        super(cause);
    }

}
