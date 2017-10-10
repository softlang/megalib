/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer;

import java.util.List;

import org.java.megalib.parser.ParserException;
import org.softlang.megalib.visualizer.cli.CommandLine;
import org.softlang.megalib.visualizer.exceptions.MegaModelVisualizerException;
import org.softlang.megalib.visualizer.models.Graph;
import org.softlang.megalib.visualizer.models.ModelToGraph;
import org.softlang.megalib.visualizer.models.transformation.TransformerRegistry;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class Main {

    public static void main(String[] args) throws ParserException {
        try {

            CommandLine cli = new CommandLine(TransformerRegistry.getRegisteredTransformerNames())
                .parse(args);
            VisualizerOptions options = VisualizerOptions.of(cli.getRequiredArguments());

            Graph graph = new ModelToGraph(options).createGraph();
            Visualizer visualizer = new Visualizer(options);
            visualizer.plotModel(graph);

            List<Graph> graphs = new ModelToGraph(options).createGraphs();
            graphs.forEach(visualizer::plotModel);

            System.out.println("Visualization complete.");
        } catch (MegaModelVisualizerException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

}
