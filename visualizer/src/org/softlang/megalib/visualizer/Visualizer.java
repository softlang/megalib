/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.softlang.megalib.visualizer.models.Graph;
import org.softlang.megalib.visualizer.models.transformation.Transformer;
import org.softlang.megalib.visualizer.models.transformation.TransformerRegistry;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class Visualizer {

    private Transformer transformer;

    public Visualizer(VisualizerOptions options) {
        transformer = TransformerRegistry.getInstance(options);
    }

    public void plotGraph(Graph graph) {
        try {
            Files.write(Paths.get("output/"+graph.getName() + "."+ "dot"),
                               transformer.transform(graph).getBytes(StandardCharsets.UTF_8));
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
    }

}
