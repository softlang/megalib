/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.commons.io.FileUtils;
import org.softlang.megalib.visualizer.models.Graph;
import org.softlang.megalib.visualizer.models.Node;
import org.softlang.megalib.visualizer.models.transformation.Transformer;
import org.softlang.megalib.visualizer.models.transformation.TransformerConfiguration;
import org.softlang.megalib.visualizer.models.transformation.TransformerRegistry;

import org.apache.fop.svg.PDFTranscoder;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

public class Visualizer {

    private Transformer transformer;
	private String fileEnding;
	private String type;

    public Visualizer(VisualizerOptions options) {
    	fileEnding = options.getFileEnding();
    	type = options.getTransformationType();
        transformer = TransformerRegistry.getInstance(options);
    }

    public void plotGraph(Graph graph) {
        try {
        	String path = "../output/"+graph.getName().replaceAll("\\.", "/")+"."+fileEnding;
        	File f = new File(path);
        	f.getParentFile().mkdirs();      
        	Files.write(f.toPath(),transformer.transform(graph).getBytes(StandardCharsets.UTF_8));
        
        
        
        TransformerConfiguration transformerConfig = transformer.getConfig();
        Graph gLegend = new Graph(graph.getName()+"_legend","Legend for Model: "+graph.getName());
        for(Node n: graph.getNodes().values()) {
        	for(String s : n.getInstanceHierarchy()) {
        		if(transformerConfig.contains(s)) {
        			n = new Node(s,s,"");
        			gLegend.add(n);
        		}
        	}
        }
        
        path = "../output/"+gLegend.getName().replaceAll("\\.", "/")+"."+fileEnding;
        File fLegend = new File(path);
        Files.write(fLegend.toPath(),transformer.transform(gLegend).getBytes(StandardCharsets.UTF_8));
        if(type.equals("dot")) {
            	MutableGraph g = Parser.read(FileUtils.openInputStream(f));
            	MutableGraph gMutableLegend = Parser.read(FileUtils.openInputStream(fLegend));
            Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("../output/"+graph.getName().replaceAll("\\.", "/")+".png"));
            Graphviz.fromGraph(gMutableLegend).render(Format.PNG).toFile(new File("../output/"+gLegend.getName().replaceAll("\\.", "/")+".png"));
        }
        else if(type.equals("dot_pdf")) {
        	MutableGraph g = Parser.read(FileUtils.openInputStream(f));
        	MutableGraph gMutableLegend = Parser.read(FileUtils.openInputStream(fLegend));
        	File svgIn = new File("../output/"+graph.getName().replaceAll("\\.", "/")+".svg");
        	File svgLegendIn = new File("../output/"+gLegend.getName().replaceAll("\\.", "/")+".svg");
        	Graphviz.fromGraph(g).render(Format.SVG).toFile(svgIn);
        	Graphviz.fromGraph(gMutableLegend).render(Format.SVG).toFile(svgLegendIn);
        	File pdfOut = new File("../output/"+graph.getName().replaceAll("\\.", "/")+".pdf");
        	File pdfLegendOut = new File("../output/"+gLegend.getName().replaceAll("\\.", "/")+".pdf");
        	svg2pdf(svgIn,pdfOut);
        	svg2pdf(svgLegendIn,pdfLegendOut);
    }
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
    }

    private static void svg2pdf(File in, File out) {
    	try {
			String uriIn = in.toURI().toURL().toString();
			TranscoderInput svgIn = new TranscoderInput(uriIn);
			OutputStream pdfOutStream = new FileOutputStream(out.toString());
			TranscoderOutput pdfOut = new TranscoderOutput(pdfOutStream);
			Transcoder transcoder = new PDFTranscoder();
			transcoder.transcode(svgIn, pdfOut);
			pdfOutStream.flush();
			pdfOutStream.close();
		} catch (TranscoderException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
}
