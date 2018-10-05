package view;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.zest.fx.ZestFxModule;
import org.eclipse.gef.zest.fx.parts.ZestFxContentPartFactory;
import org.softlang.megalib.visualizer.models.transformation.TransformerConfiguration;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.GefConfigurationBuilder;
import model.GraphToGef;
import model.ModelDataToGraph;
import module.CustomModule;

public class ExplorerController {
	
	private String path1;
	
	private String path2;
	
	private LinkedList<Graph> graphs = new LinkedList<>();
	private LinkedList<IDomain> domainList = new LinkedList<>();
	private LinkedList<IViewer> viewerList = new LinkedList<>();
	
	private LinkedList<Graph> legends = new LinkedList<>();
	private LinkedList<IDomain> domainListLegends = new LinkedList<>();
	private LinkedList<IViewer> viewerListLegends = new LinkedList<>();
	

	@FXML
	public ListView<String> contentListView;
	
	@FXML
	public BorderPane viewPane;
	
	@FXML
	public BorderPane legendPane;
	
	public void reset_data(){
		graphs = new LinkedList<>();
		domainList = new LinkedList<>();
		viewerList = new LinkedList<>();
		
		legends = new LinkedList<>();
		domainListLegends = new LinkedList<>();
		viewerListLegends = new LinkedList<>();
	}
	
	public void loadGraphs() {
		reset_data();

		TextInputDialog dialog = new TextInputDialog("Enter path");
		dialog.setTitle("Text Input Dialog");
		dialog.setHeaderText("Look, a Text Input Dialog");
		dialog.setContentText("Please enter your name:");
		Optional<String> result = dialog.showAndWait();
		
		result.ifPresent(input -> path1 = input);
		
		TextInputDialog dialog2 = new TextInputDialog("antlr/App.megal");
		dialog2.setTitle("Text Input Dialog");
		dialog2.setHeaderText("Look, a Text Input Dialog");
		dialog2.setContentText("Please enter your name:");
		Optional<String> result2 = dialog2.showAndWait();
		result2.ifPresent(input -> path2 = input);
		
		ModelDataToGraph modelDataToGraph = new ModelDataToGraph();
		List<org.softlang.megalib.visualizer.models.Graph> gs = modelDataToGraph.createGraph(path1, path2);
		for( org.softlang.megalib.visualizer.models.Graph g: gs) {
			GraphToGef g2g = new GraphToGef();
			graphs.add(g2g.createGraphSpringLayout(g));
			
			//create Legend
	        TransformerConfiguration transformerConfig = new GefConfigurationBuilder().buildConfiguration();
	        org.softlang.megalib.visualizer.models.Graph gLegend = new org.softlang.megalib.visualizer.models.Graph(g.getName()+"_legend","Legend for Model: "+g.getName());
	        for(org.softlang.megalib.visualizer.models.Node n: g.getNodes().values()) {
	        	for(String s : n.getInstanceHierarchy()) {
	        		if(transformerConfig.contains(s)) {
	        			n = new org.softlang.megalib.visualizer.models.Node(s,s,"");
	        			gLegend.add(n);
	        		}
	        	}
	        }
	        legends.add(g2g.createGraphGridLayout(gLegend));
		}
		
		List<String> contentItems = new LinkedList();
		for(int i = 0; i <graphs.size();i++){
			contentItems.add("Block "+i);
		}
		contentListView.setItems(FXCollections.observableArrayList(contentItems));
		
		
		for(int i = 0; i<graphs.size(); i++){
			Injector injector = Guice.createInjector(createModule());
			IDomain domain = injector.getInstance(IDomain.class);
			domainList.add(domain);
			viewerList.add(domain.getAdapter(AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE)));
		}
		
		for(int i = 0; i<legends.size(); i++){
			Injector injector = Guice.createInjector(createModule());
			IDomain domain = injector.getInstance(IDomain.class);
			domainListLegends.add(domain);
			viewerListLegends.add(domain.getAdapter(AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE)));
		}

		viewPane.setCenter(viewerList.get(0).getCanvas());
		legendPane.setCenter(viewerListLegends.get(0).getCanvas());
		domainList.get(0).activate();
		domainListLegends.get(0).activate();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for(int i = 0; i<graphs.size();i++){
					viewerList.get(i).getContents().setAll(Collections.singletonList(graphs.get(i)));
					viewerListLegends.get(i).getContents().setAll(Collections.singletonList(legends.get(i)));
				}
			}
		});
	}
	
	protected Module createModule() {
		return new CustomModule();
	}
}
