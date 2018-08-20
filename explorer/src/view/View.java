package view;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.zest.fx.ZestFxModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.GraphToGef;
import model.ModelToGraph;
import module.CustomModule;

public class View extends Application {

	private String title;
	private LinkedList<IDomain> domainList = new LinkedList();
	private LinkedList<IViewer> viewerList = new LinkedList();
	private static LinkedList<Graph> graphs = new LinkedList();
	private String path1;
	private String path2;
	
	public static void main(String[] args){
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter Path to Models Folder");
		String path1 = scanner.next();
		System.out.println("Enter Path to the model");
		ModelToGraph m = new ModelToGraph();
		String path2 = scanner.next();
		List<model.Graph> gs = m.createGraph(path1, path2);
		for( model.Graph g: gs){
			GraphToGef g2g = new GraphToGef();
			graphs.add(g2g.createGraph(g));
		}
		Application.launch();
	}
	
	@Override
	public void start(final Stage primaryStage) throws Exception {
	// configure application
			for(int i = 0; i<graphs.size(); i++){
				Injector injector = Guice.createInjector(createModule());
				IDomain domain = injector.getInstance(IDomain.class);
				domainList.add(domain);
				viewerList.add(domain.getAdapter(AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE)));
				
			}
			primaryStage.setScene(createScene(viewerList.get(0)));
			primaryStage.setResizable(true);
			primaryStage.setWidth(getStageWidth());
			primaryStage.setHeight(getStageHeight());
			primaryStage.setTitle(title);
			primaryStage.show();
		
			for(int i = 1; i<graphs.size();i++){
            Stage newWindow = new Stage();
            newWindow.setScene(createScene(viewerList.get(i)));
            newWindow.setResizable(true);
            newWindow.setWidth(getStageWidth());
            newWindow.setHeight(getStageHeight());
            newWindow.setTitle(title);
            newWindow.show();}

			// activate domain only after viewers have been hooked
			for(int i = 0; i<graphs.size();i++){
				domainList.get(i).activate();
			}

			// set contents in the JavaFX application thread because it alters the
			// scene graph
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					for(int i = 0; i<graphs.size();i++){
						viewerList.get(i).getContents().setAll(Collections.singletonList(graphs.get(i)));
					}
				}
			});
		}

		protected Scene createScene(IViewer viewer) {
			return new Scene(((IViewer) viewer).getCanvas());
		}

		protected int getStageHeight() {
			return 500;
		}

		protected int getStageWidth() {
			return 800;
		}

		protected Module createModule() {
			return new CustomModule();
		}

}
