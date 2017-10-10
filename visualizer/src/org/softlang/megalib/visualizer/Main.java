/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer;

import java.util.Set;

import org.java.megalib.parser.ParserException;
import org.softlang.megalib.visualizer.cli.CommandLine;
import org.softlang.megalib.visualizer.exceptions.MegaModelVisualizerException;
import org.softlang.megalib.visualizer.models.Graph;
import org.softlang.megalib.visualizer.models.GraphFactory;
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

            Graph graph = new GraphFactory(options).create();
            Visualizer visualizer = new Visualizer(options);
            visualizer.plotModel(graph);
            
            Set<Graph> graphs = new GraphFactory(options).createSepView();
            visualizer.plotBlocks(graphs);

            System.out.println("Visualization complete.");
        } catch (MegaModelVisualizerException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

}
