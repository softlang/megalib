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

import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.zest.fx.ui.parts.ZestFxUiView;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;

@SuppressWarnings("restriction")
public class GraphView extends ZestFxUiView implements IShowInTarget{

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
				Graph g = createGraphFromJson(f);
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
	        if(!watchServiceRunning) {
	        	createWatchService(f);
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
	
	public void createWatchService(File f) {
	    new Thread(new Runnable() {
	        public void run(){
	            try {
	            	WatchKey keyTemp;
		            while ((keyTemp = w.take()) != null) {
		                for (WatchEvent<?> event : k.pollEvents()) {
		                	if(event.context().toString().equals(fname)) {
		                		//TODO: READ IN FILE AGAIN AND UPDATE GRAPH
		                		Path completeFilePath = Paths.get(path.toString(),event.context().toString());
		                        System.out.println(completeFilePath);
		                	}
		                }
		                k.reset();
		            }  
	            } catch (InterruptedException e) {
					e.printStackTrace();
				}	
	        }
	    }).start();
	}
	
	private Graph createGraphFromJson(File f) {
		//TODO: Parse JSON
		//Create Graph
		//Return it
		return null;
	}



}