/**
 *
 */
package org.java.megalib.checker.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.io.FileUtils;
import org.java.megalib.models.MegaModel;
import org.java.megalib.models.Relation;

import main.antlr.techdocgrammar.MegalibBaseListener;
import main.antlr.techdocgrammar.MegalibLexer;
import main.antlr.techdocgrammar.MegalibParser;

public class MegaModelLoader {

    private Queue<String> todos;
    private File root;

    private MegaModel model;

    public MegaModelLoader() {
        todos = new LinkedList<>();
        model = new MegaModel();
        loadPrelude();
    }

    public MegaModel getModel() {
        return model;
    }

    private void loadPrelude() {
        File f = null;
        String data = "";
        try {
            f = new File("../models/Prelude.megal");
            data = FileUtils.readFileToString(f);
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        model = loadString(data);
    }

    public void loadFile(String filepath) {
        File f = null;
        String data = "";
        try {
            f = new File(filepath);
            if (!f.exists())
                throw new FileNotFoundException();
            data = FileUtils.readFileToString(f);
        }
        catch (IOException e) {
            model.addWarning("Error : The file '" + filepath + "' could not be loaded.");
            return;
        }
        if (!loadCompleteModelFrom(data, f.getAbsolutePath())) {
            System.exit(1);
        }

    }

    public MegaModel loadString(String data) {
        try {
            return ((MegalibParserListener) parse(data, new MegalibParserListener(model))).getModel();
        }
        catch (MegalibParserException e) {
            System.err.println(e.getMessage());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean loadCompleteModelFrom(String data, String abspath) {
        try {
            resolveImports(data, abspath);
            System.out.println("Loading:");
            todos.forEach(t -> System.out.println(" " + t));
            System.out.println();
            while (!todos.isEmpty()) {
                String p = todos.poll();
                p = root.getAbsolutePath() + "/" + p.replaceAll("\\.", "/") + ".megal";
                String pdata = FileUtils.readFileToString(new File(p));
                model = ((MegalibParserListener) parse(pdata, new MegalibParserListener(model))).getModel();
                if (!model.getCriticalWarnings().isEmpty()) {
                    model.getCriticalWarnings().forEach(w -> System.err.println(w));
                    throw new WellFormednessException("Resolve critical errors first"
                                                      + (abspath.equals("") ? " in " + abspath : ""));
                }
            }
            return true;
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
        catch (WellFormednessException e) {
            System.err.println(e.getMessage());
            return false;
        }
        catch (MegalibParserException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public MegalibBaseListener parse(String data, MegalibBaseListener listener) throws MegalibParserException,
                                                                                IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(data.getBytes());
        ANTLRInputStream antlrStream = new ANTLRInputStream(stream);
        MegalibLexer lexer = new MegalibLexer(antlrStream);
        CommonTokenStream token = new CommonTokenStream(lexer);

        MegalibParser parser = new MegalibParser(token);
        parser.addErrorListener(new MegalibErrorListener());
        ParseTreeWalker treeWalker = new ParseTreeWalker();
        treeWalker.walk(listener, parser.declaration());
        for (ANTLRErrorListener el : parser.getErrorListeners()) {
            if (el instanceof MegalibErrorListener) {
                if (((MegalibErrorListener) el).getCount() > 0)
                    throw new MegalibParserException("Syntactic errors exist : Fix them before further checks");
            }
        }
        return listener;
    }

    private void resolveImports(String data, String abspath) throws MegalibParserException, IOException {
        List<Relation> imports = new LinkedList<>();
        Set<String> processed = new HashSet<>();
        Set<String> toProcess = new HashSet<>();

        MegalibImportListener l = (MegalibImportListener) parse(data, new MegalibImportListener());
        String loadedModuleName = l.getName();
        imports.addAll(l.getImports());
        processed.add(loadedModuleName);
        toProcess.addAll(l.getImports().parallelStream().map(r -> r.getObject()).collect(Collectors.toSet()));
        toProcess.removeAll(processed);

        // Resolve module name to file path
        int lvl = loadedModuleName.split("\\.").length;
        root = new File(abspath);
        // Get root folder
        for (int i = 0; i < lvl; i++) {
            root = root.getParentFile();
        }
        // Fill the import map
        while (!toProcess.isEmpty()) {
            String p = toProcess.iterator().next();
            p = root.getAbsolutePath() + "/" + p.replaceAll("\\.", "/") + ".megal";
            String pdata = FileUtils.readFileToString(new File(p));
            l = (MegalibImportListener) parse(pdata, new MegalibImportListener());
            imports.addAll(l.getImports());
            processed.add(l.getName());
            toProcess.addAll(l.getImports().parallelStream().map(r -> r.getObject()).collect(Collectors.toSet()));
            toProcess.removeAll(processed);
        }
        // order import map in a set-based approach
        while (!imports.isEmpty()) {
            Set<String> subjects = imports.parallelStream().map(r -> r.getSubject()).collect(Collectors.toSet());
            Set<String> objects = imports.parallelStream().map(r -> r.getObject()).collect(Collectors.toSet());
            Set<String> diff = new HashSet<>(objects);
            diff.removeAll(subjects);
            if (diff.isEmpty())
                throw new MegalibParserException("Error : Cycle identified in imports");
            todos.addAll(diff);
            imports.removeIf(r -> diff.contains(r.getObject()));
        }
        if (!loadedModuleName.equals("")) {
            todos.add(loadedModuleName);
        }
    }

}
