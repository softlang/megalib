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

public class Visualizer {

    private Transformer transformer;
	private String fileEnding;

    public Visualizer(VisualizerOptions options) {
    	fileEnding = options.getTransformationType();
        transformer = TransformerRegistry.getInstance(options);
    }

    public void plotGraph(Graph graph) {
        try {
        	String path = "../output/"+graph.getName().replaceAll("\\.", "/")+"."+fileEnding;
        	File f = new File(path);
        	f.getParentFile().mkdirs();
            Files.write(f.toPath(),transformer.transform(graph).getBytes(StandardCharsets.UTF_8));
            if(fileEnding.equals("dot")) {
            	MutableGraph g = Parser.read(FileUtils.openInputStream(f));
                Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("../output/"+graph.getName().replaceAll("\\.", "/")+".png"));
            }
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
    }

}
