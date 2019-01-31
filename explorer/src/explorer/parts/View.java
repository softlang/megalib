package explorer.parts;

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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.GraphToGef;
import model.ModelDataToGraph;
import module.CustomModule;

public class View extends Application {

	private String title;
	private LinkedList<IDomain> domainList = new LinkedList();
	private LinkedList<IViewer> viewerList = new LinkedList();
	private static LinkedList<Graph> graphs = new LinkedList();
	private String path1;
	private String path2;
	
	public static void main(String[] args){
		Application.launch();
	}
	
	@Override
	public void start(final Stage primaryStage) throws Exception {	
		Parent root = FXMLLoader.load(getClass().getResource("explorer.fxml"));
		
		primaryStage.setTitle("FXML Welcome");
		primaryStage.setScene(new Scene(root));
		
		primaryStage.show();
		}

		protected Scene createScene(IViewer viewer) {
			return new Scene(((IViewer) viewer).getCanvas());
		}

		protected Module createModule() {
			return new CustomModule();
		}

}
