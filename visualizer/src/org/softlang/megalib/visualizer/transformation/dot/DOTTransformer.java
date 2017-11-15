/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer.transformation.dot;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.softlang.megalib.visualizer.VisualizerOptions;
import org.softlang.megalib.visualizer.models.Graph;
import org.softlang.megalib.visualizer.models.Node;
import org.softlang.megalib.visualizer.models.transformation.ConfigItem;
import org.softlang.megalib.visualizer.models.transformation.Transformer;
import org.softlang.megalib.visualizer.models.transformation.TransformerConfiguration;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class DOTTransformer extends Transformer {

    private static final ConfigItem<String, String> DEFAULT_CONFIG = new ConfigItem<String, String>()
        .put("color", "black")
        .put("shape", "oval");

    private TransformerConfiguration config = new DOTConfigurationBuilder().buildConfiguration();

    public DOTTransformer(VisualizerOptions options) {
        super(options);
    }

    @Override
    public String transform(Graph g) {
        if (g == null) {
			throw new IllegalArgumentException();
		}

        return process(g);
    }
    
    private STGroup loadTemplateFromResource() {
    	String resource = "graphviz.stg";
    	URL u = this.getClass().getResource(resource);
    	return new STGroupFile(u, "UTF-8", '<', '>');
	}

    private String process(Graph g) {
        STGroup templateGroup = loadTemplateFromResource();
        ST template = templateGroup.getInstanceOf("graph");

        List<DOTNode> nodes = new LinkedList<>();

        g.forEachNode(n -> nodes.add(createDOTNode(n)));
        
        template.add("name", options.getModelName());
        template.add("nodes", nodes);
        template.add("edges", g.getEdges());
        String text = g.getText();
        text = text.replace("/*", "");
    	text = text.replaceAll("\\r", "");
    	text = text.replaceAll("\\n", "\\\\n");
        text = text.replaceAll("\"", "'");
        text = text.replace("@Description:", "");
        text = Pattern.compile("@Rationale(.*?)\\*/", Pattern.DOTALL).matcher(text).replaceAll("").trim();
        template.add("text", text);

        return template.render();
    }

    

	private String getConfigValue(Node node, String attribute) {
        return getConfigItem(node, attribute).get(attribute);
    }

    private ConfigItem<String, String> getConfigItem(Node node, String attribute) {
        // Traverse the configuration hierarchy to determine if there is a configuration item present
        // Hence: name -> type -> supertype (until finished) -> default configuration
        for (String key : getKeyHierarchy(node)) {
            if (config.contains(key) && config.get(key).contains(attribute)) {
				return config.get(key);
			}
        }
        System.out.println("Default for "+node.getName()+":"+node.getType()+" at "+attribute);
        return DEFAULT_CONFIG;
    }

    private List<String> getKeyHierarchy(Node node) {
        return Stream.concat(
            Stream.of(node.getName(), node.getType()),
            node.getInstanceHierarchy().stream()
        ).collect(Collectors.toList());
    }

    private DOTNode createDOTNode(Node node) {
        return new DOTNode(node, getConfigValue(node, "color"), getConfigValue(node, "shape"));
    }

}
