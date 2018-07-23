package org.java.megalib.checker.services;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.java.megalib.models.Relation;
import org.java.megalib.parser.ImportListener;
import org.java.megalib.parser.ParserException;

public class Importer {
	

	static Queue<String> resolveImports(String data, String abspath, ModelLoader ml) throws ParserException, IOException {
        ImportListener l = (ImportListener) ml.parse(data, new ImportListener());
        String modname = l.getName();
        // Resolve module name to file path
        int lvl = modname.split("\\.").length;
        File root = new File(abspath);
        // Get root folder
        for (int i = 0; i < lvl; i++) {
            root = root.getParentFile();
        }
        ml.setRoot(root);
        
        return getTodos(buildImportGraph(modname,ml,l), modname);
	}
        
    private static List<Relation> buildImportGraph(String modname, ModelLoader ml, ImportListener l) throws ParserException, IOException{
        List<Relation> imports = new LinkedList<>();
        Set<String> parsed = new HashSet<>();
        imports.addAll(l.getImports());
        parsed.add(modname);
        Set<String> toparse = l.getImports().parallelStream().map(r -> r.getObject()).collect(Collectors.toSet());
        toparse.remove(modname);
        String oldmodname = modname;

        
        // Fill the import graph
        while (!toparse.isEmpty()) {
            String p = toparse.iterator().next();
            String pdata;
			try {
				pdata = FileUtils.readFileToString(new File(ml.getRoot().getAbsolutePath() + "/" + p.replaceAll("\\.", "/")
				                                                   + ".megal"));
	            l = (ImportListener) ml.parse(pdata, new ImportListener());
	            oldmodname = l.getName();
	            if(!p.equals(oldmodname))
	                throw new ParserException("Error at Import Resolution: Identified wrong spelling in 'import " + p + "'. Expected '"+oldmodname+"'");
	            imports.addAll(l.getImports());
	            parsed.add(l.getName());
	            toparse.addAll(l.getImports().parallelStream().map(r -> r.getObject())
	                              .collect(Collectors.toSet()));
	            toparse.removeAll(parsed);
	            
			} catch (IOException e) {
				throw new ParserException("Error at Import resolution from "+oldmodname+" to "+p);
			}
            
        }
        return imports;
    }
        
    private static Queue<String> getTodos(List<Relation> imports, String modname) throws ParserException{
		Queue<String> todos = new LinkedList<>();
        while (!imports.isEmpty()) {
            Set<String> subjects = imports.parallelStream().map(r -> r.getSubject()).collect(Collectors.toSet());
            Set<String> objects = imports.parallelStream().map(r -> r.getObject()).collect(Collectors.toSet());
            Set<String> diff = new HashSet<>(objects);
            diff.removeAll(subjects);
            if (diff.isEmpty())
                throw new ParserException("Error : Cycle identified in imports");
            todos.addAll(diff);
            imports.removeIf(r -> diff.contains(r.getObject()));
        }
        if (!modname.equals("")) {
            todos.add(modname);
        }
		return todos;
    }
}
