/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer.models.transformation;

import org.softlang.megalib.visualizer.VisualizerOptions;
import org.softlang.megalib.visualizer.models.Graph;

/**
 * Assuming that any Transformer works with String template and thus produces a
 * String.
 */
public abstract class Transformer {



    protected VisualizerOptions options;

    public Transformer(VisualizerOptions options) {
        this.options = options;
    }

    public abstract String transform(Graph g);

}
