package module;

/* 
 * 
 * Parts of this class has been taken from the gef-example:
 * https://github.com/eclipse/gef/blob/master/org.eclipse.gef.dot.examples/src/org/eclipse/gef/dot/examples/DotLayoutExample.java
 * 
 * 
 */

import java.io.File;
import java.util.HashMap;

import org.eclipse.gef.common.attributes.IAttributeCopier;
import org.eclipse.gef.common.attributes.IAttributeStore;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.DotExecutableUtils;
import org.eclipse.gef.dot.internal.DotExport;
import org.eclipse.gef.dot.internal.DotFileUtils;
import org.eclipse.gef.dot.internal.language.dot.GraphType;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.GraphCopier;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;
import org.eclipse.gef.zest.fx.ZestProperties;

public class DotCustomLayout implements ILayoutAlgorithm{

			protected static final String LABEL = ZestProperties.LABEL__NE;
			private String dotExecutablePath;

			//set path to graphvizz executable
			public DotCustomLayout(String path) {
				dotExecutablePath = path;
			}
			
			@Override
			public void applyLayout(LayoutContext layoutContext, boolean clean) {

				Graph dotGraph = new GraphCopier(new IAttributeCopier() {

					@Override
					public void copy(IAttributeStore source,
							IAttributeStore target) {
						if (source instanceof Node && target instanceof Node) {
							// transfer name for identification purpose
							// add " " to labels, because dot has problem with ?
							DotAttributes._setName((Node) target,
									"\""+(String) ((Node) source).attributesProperty()
											.get(LABEL)+"\"");
						}
					}
				}).copy(layoutContext.getGraph());

				// set graph type
				DotAttributes._setType(dotGraph, GraphType.DIGRAPH);

				// export the Graph with DotAttributs to a DOT string and call the
				// dot executable to add layout info to it
				File tmpFile = DotFileUtils
						.write(new DotExport().exportDot(dotGraph));
				String[] dotResult = DotExecutableUtils.executeDot(
						new File(dotExecutablePath), true, tmpFile, null, null);
				if (!dotResult[1].isEmpty()) {
					System.err.println(dotResult[1]);
				}
				tmpFile.delete();
				
				/*This was the nice way, but does not work with actual imported antlr runtime...
				Graph layoutedDotGraph = new DotImport().importDot(dotResult[0])
						.get(0);
				*/
				
				//using self-written string-prase way, may change later to above way
				String[] splittedDotResult = dotResult[0].split(";");
				HashMap<String,Point> NamePointMap = new HashMap();
				
				//ignore first two instances, because they are no Nodes
				for(int i = 2; i<splittedDotResult.length; i++) {
					String[] splittedForName = splittedDotResult[i].split(" ");
					//check if it is no Relation
					if(splittedForName.length > 1 && !splittedForName[1].equals("->")) {
						String[] splittedForPos = splittedDotResult[i].split(",");
						//create point and set position according to info from string (plus a small factor to make
						//the graph look nicer)
						Point p = new Point();
						p.x = (Double.parseDouble(splittedForPos[1].replaceAll(" ", "").replaceAll("\t", "").replaceAll("pos=\"", "")))*1.8;
						p.y = (Double.parseDouble(splittedForPos[2].replaceAll("\"", "")))*1.8;
						String label = splittedForName[0].replaceAll(" ", "").replaceAll("\t", "");
						label = label.replaceAll("\"", "");
						label = label.substring(2, label.length());
						NamePointMap.put(label, p);
					}
				}
				
				//set Node positions according to dot positions
				for (Node target : layoutContext.getGraph().getNodes()) {
						LayoutProperties.setLocation(target, NamePointMap.get(target.getAttributes().get(LABEL)));
						}
					}
				
			
		}

