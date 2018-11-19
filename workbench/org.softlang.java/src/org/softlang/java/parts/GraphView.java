package org.softlang.java.parts;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.LinkedList;

import org.apache.commons.io.FileUtils;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.graph.Node.Builder;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.ui.parts.ZestFxUiView;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;


@SuppressWarnings("restriction")
public class GraphView extends ZestFxUiView implements IShowInTarget {

	String fname;
	WatchService w;
	WatchKey k;
	Path path;
	Boolean watchServiceRunning = false;

	@Override
	public boolean show(ShowInContext cxt) {
		Object in = cxt.getInput();
		if (in instanceof File) {
			File f = (File) in;
			fname = f.getName();
			setWatchService(f);
			Graph g = createGraphFromJson(f.toPath());
			setGraph(g);
			return true;
		}
		return false;
	}

	public void setWatchService(File f) {
		try {
			w = FileSystems.getDefault().newWatchService();
			path = Paths.get(f.getParent());
			k = path.register(w, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
			if (!watchServiceRunning) {
				createWatchService(f);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createWatchService(File f) {
		ScheduledService<Graph> s = new ScheduledService() {
			@Override
			protected Task<Graph> createTask() {
				Task<Graph> task = new Task<Graph>() {
					@Override
					protected Graph call() throws Exception {
						WatchKey keyTemp;
						while ((keyTemp = w.take()) != null) {
							for (WatchEvent<?> event : k.pollEvents()) {
								if (event.context().toString().equals(fname)) {
									Path completeFilePath = Paths.get(path.toString(),event.context().toString());
									k.reset();
									return createGraphFromJson(completeFilePath);
								}
							}
							k.reset();
						}
						return null;
					}
				};
				return task;
		};
		};
		s.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				setGraph(s.getValue());
			}
		});
		s.start();
	}

	private Graph createGraphFromJson(Path f) {
		String json;
		try {
			nodeList = new LinkedList<Node>();
			edgeList = new LinkedList<Edge>();
			LinkedList<String> urls = null;
			LinkedList<String> bindings = null;
			
			json = FileUtils.readFileToString(f.toFile());
			JsonElement root = new JsonParser().parse(json);
			JsonArray nodes =  (JsonArray) root.getAsJsonObject().get("Nodes");
			JsonArray edges = (JsonArray) root.getAsJsonObject().get("Edges");
			for(JsonElement j: nodes) {
				String name = j.getAsJsonObject().get("name").getAsString();
				if(j.getAsJsonObject().has("links")) {
					urls = new LinkedList();
					for(JsonElement u: j.getAsJsonObject().get("links").getAsJsonArray()) {
					urls.add(u.getAsString());
					}
				}
				if(j.getAsJsonObject().has("bindings")) {
					bindings = new LinkedList();
					for(JsonElement b: j.getAsJsonObject().get("bindings").getAsJsonArray()) {
					bindings.add(b.getAsString());
					}
				}
			//TODO: Add function which creates Nodes according to above data
			//e.g. createNode(name, links, bindings)
			//TODO: Same for edges
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return null;
	}

}