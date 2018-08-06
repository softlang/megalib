package view;

import java.util.Collections;
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
import javafx.stage.Stage;
import model.GraphToGef;
import model.ModelToGraph;

public class View extends Application {

	private String title;
	private IDomain domain;
	private IViewer viewer;
	private static Graph graph;
	private String path1;
	private String path2;
	
	public static void main(String[] args){
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter Path to Models Folder");
		String path1 = scanner.next();
		System.out.println("Enter Path to the model");
		ModelToGraph m = new ModelToGraph();
		String path2 = scanner.next();
		graph = GraphToGef.createGraph(m.createGraph(path1,path2));	
		Application.launch();
	}
	
	@Override
	public void start(final Stage primaryStage) throws Exception {
	// configure application
			Injector injector = Guice.createInjector(createModule());
			domain = injector.getInstance(IDomain.class);
			viewer = domain.getAdapter(
					AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE));
			primaryStage.setScene(createScene(viewer));

			primaryStage.setResizable(true);
			primaryStage.setWidth(getStageWidth());
			primaryStage.setHeight(getStageHeight());
			primaryStage.setTitle(title);
			primaryStage.show();

			// activate domain only after viewers have been hooked
			domain.activate();

			// set contents in the JavaFX application thread because it alters the
			// scene graph
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					viewer.getContents().setAll(Collections.singletonList(graph));
				}
			});
		}

		protected Scene createScene(IViewer viewer) {
			return new Scene(((IViewer) viewer).getCanvas());
		}

		protected int getStageHeight() {
			return 480;
		}

		protected int getStageWidth() {
			return 640;
		}

		protected Module createModule() {
			return new ZestFxModule();
		}

}
