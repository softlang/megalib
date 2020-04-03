/**
 *
 */
package org.java.megalib.checker.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.io.FileUtils;
import org.java.megalib.models.MegaModel;
import org.java.megalib.parser.ErrorListener;
import org.java.megalib.parser.ParserException;
import org.java.megalib.parser.ParserListener;

import main.antlr.techdocgrammar.MegalibBaseListener;
import main.antlr.techdocgrammar.MegalibLexer;
import main.antlr.techdocgrammar.MegalibParser;

public class ModelLoader {

    private File root;

    private MegaModel model;
    private List<String> typeErrors;

    public ModelLoader(String preludePath){
        model = new MegaModel();
        typeErrors = new ArrayList<>();
        try{
            loadFile(preludePath);
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public ModelLoader(){
        this("../models/common/Prelude.megal");
    }

    public MegaModel getModel() {
        return model;
    }

    public File getRoot() {
    		return root;
    }

    public List<String> getTypeErrors() {
        return Collections.unmodifiableList(typeErrors);
    }

    public boolean loadFile(String filepath) throws IOException {
        File f = null;
        String data = "";
        f = new File(filepath);
        if (!f.exists())
            throw new FileNotFoundException();
        data = FileUtils.readFileToString(f);
        if (!loadCompleteModelFrom(data, f.getAbsolutePath()))
            return false;
        return true;
    }

    public List<String> loadString(String data) throws ParserException, IOException {
        data = "module Test " + data;
        ParserListener pl = (ParserListener) parse(data, new ParserListener(model));
        model = pl.getModel();
        typeErrors.addAll(pl.getTypeErrors());
        return typeErrors;
    }

    private boolean loadCompleteModelFrom(String data, String abspath) {
        try {
            Queue<String> todos = Importer.resolveImports(data, abspath, this);
            if(!abspath.contains("common")) {
            	System.out.println("Loading:");
            	todos.forEach(t -> System.out.println(" "+t));
            	System.out.println();
            }
            while (!todos.isEmpty()) {
                String p = todos.poll();
                p = root.getAbsolutePath() + "/" + p.replaceAll("\\.", "/") + ".megal";
                String pdata = FileUtils.readFileToString(new File(p));
                ParserListener pl = (ParserListener) parse(pdata, new ParserListener(model));
                model = pl.getModel();
                typeErrors.addAll(pl.getTypeErrors());
                if (!pl.getTypeErrors().isEmpty())
                    return false;
            }
            return true;
        }
        catch(IOException | ParserException e){
            typeErrors.add(e.getMessage());
            return false;
        }
    }

    public MegalibBaseListener parse(String data, MegalibBaseListener listener) throws ParserException, IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(data.getBytes());
        ANTLRInputStream antlrStream = new ANTLRInputStream(stream);
        MegalibLexer lexer = new MegalibLexer(antlrStream);
        CommonTokenStream token = new CommonTokenStream(lexer);

        MegalibParser parser = new MegalibParser(token);
        parser.addErrorListener(new ErrorListener());
        ParseTreeWalker treeWalker = new ParseTreeWalker();
        treeWalker.walk(listener, parser.declaration());
        for (ANTLRErrorListener el : parser.getErrorListeners()) {
            if (el instanceof ErrorListener) {
                if (((ErrorListener) el).getCount() > 0)
                    throw new ParserException("Syntactic errors exist. Cannot perform further checks.");
            }
        }
        return listener;
    }

	void setRoot(File root) {
		this.root = root;
	}
}
