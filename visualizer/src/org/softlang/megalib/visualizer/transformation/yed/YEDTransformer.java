/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer.transformation.yed;

import org.softlang.megalib.visualizer.VisualizerOptions;
import org.softlang.megalib.visualizer.exceptions.MegaModelVisualizerException;
import org.softlang.megalib.visualizer.models.Graph;
import org.softlang.megalib.visualizer.models.transformation.Transformer;
import org.softlang.megalib.visualizer.models.transformation.TransformerConfiguration;
import org.softlang.megalib.visualizer.models.transformation.TransformerRegistry;

/**
 *
 * open options:
 *  Gephi, jgrapht
 */
public class YEDTransformer extends Transformer {

    static {
        TransformerRegistry.registerTransformer("yed","yed", (VisualizerOptions options) -> new YEDTransformer(options));
    }

    public YEDTransformer(VisualizerOptions options) {
        super(options);
    }

    @Override
    public String transform(Graph g) {
        throw new MegaModelVisualizerException("Not supported yet.");
    }

	@Override
	public TransformerConfiguration getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

}
