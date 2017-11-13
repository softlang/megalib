/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.softlang.megalib.visualizer.models.Graph;
import org.softlang.megalib.visualizer.models.transformation.Transformer;
import org.softlang.megalib.visualizer.models.transformation.TransformerRegistry;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

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
        	File o = new File("../output/png/");
        	o.mkdir();
        	o = new File("../output/dot/");
        	o.mkdir();
        	String path = "../output/dot/"+graph.getName()+".dot";
        	File f = new File(path);
            Files.write(f.toPath(),transformer.transform(graph).getBytes(StandardCharsets.UTF_8));
            
            MutableGraph g = Parser.read(FileUtils.openInputStream(f));
            Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("../output/png/"+graph.getName()+".png"));
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
    }

}
