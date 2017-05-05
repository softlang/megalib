/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.softlang.megalib.visualizer.exceptions.MegaModelVisualizerException;
import org.softlang.megalib.visualizer.models.Graph;
import org.softlang.megalib.visualizer.models.transformation.Transformer;
import org.softlang.megalib.visualizer.models.transformation.TransformerRegistry;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class Visualizer {

    private Transformer transformer;

    private VisualizerOptions options;

    public Visualizer(VisualizerOptions options) {
        this.options = options;
        transformer = TransformerRegistry.getInstance(options);
    }

    public File plotFile(Graph graph) {
        try {
            return Files.write(Paths.get(options.getModelName() + "."
                                         + FileExtensionFactory.get(options.getTransformationType())),
                               transformer.transform(graph).getBytes(StandardCharsets.UTF_8))
                        .toFile();
        } catch (IOException ex) {
            throw new MegaModelVisualizerException(ex);
        }
    }

}
