/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer;

import java.util.List;

import org.softlang.megalib.visualizer.cli.CommandLine;
import org.softlang.megalib.visualizer.exceptions.MegaModelVisualizerException;
import org.softlang.megalib.visualizer.models.Graph;
import org.softlang.megalib.visualizer.models.ModelToGraph;
import org.softlang.megalib.visualizer.models.transformation.TransformerRegistry;
import org.softlang.megalib.visualizer.transformation.dot.DOTTransformer;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class Main {

    public static void main(String[] args) {
        try {
        	TransformerRegistry.registerTransformer("dot", (VisualizerOptions options)
        		     -> new DOTTransformer(options));

            CommandLine cli = new CommandLine(TransformerRegistry.getRegisteredTransformerNames())
                .parse(args);
            VisualizerOptions options = VisualizerOptions.of(cli.getRequiredArguments());


            Visualizer visualizer = new Visualizer(options);
            //Graph graph = new ModelToGraph(options).createGraph();
            //visualizer.plotGraph(graph);

            List<Graph> graphs = new ModelToGraph(options).createGraphs();
            graphs.forEach(visualizer::plotGraph);

            System.out.println("Visualization complete.");
        } catch (MegaModelVisualizerException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

}
