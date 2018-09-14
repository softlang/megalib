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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import model.GraphToGef;
import model.ModelDataToGraph;
import module.CustomModule;

public class ExplorerController {
	
	private String path1;
	
	private String path2;
	
	private LinkedList<Graph> graphs = new LinkedList<>();
	private LinkedList<IDomain> domainList = new LinkedList<>();
	private LinkedList<IViewer> viewerList = new LinkedList<>();

	@FXML
	public ListView<String> contentListView;
	
	@FXML
	public BorderPane viewPane;
	
	public void loadGraphs() {
		TextInputDialog dialog = new TextInputDialog("E:/Uni/Forschungspraktikum/megalib/models");
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
			graphs.add(g2g.createGraph(g));
		}
				
		for(int i = 0; i<graphs.size(); i++){
			Injector injector = Guice.createInjector(createModule());
			IDomain domain = injector.getInstance(IDomain.class);
			domainList.add(domain);
			viewerList.add(domain.getAdapter(AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE)));
		}

		
		InfiniteCanvas canvas = (InfiniteCanvas)viewerList.get(0).getCanvas();
		viewPane.setCenter(canvas);
	    canvas.sceneProperty().addListener((observable, oldValue, newValue) -> {
	      if (canvas.getScene() != null) {
	        domainList.get(0).activate();
	        try {
	        	viewerList.get(0).getContents().setAll(Collections.singletonList(graphs.get(0)));
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	      }
	    });
	}
	
	protected Module createModule() {
		return new CustomModule();
	}
}
