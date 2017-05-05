/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer;

import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;
import org.java.megalib.checker.services.ModelLoader;
import org.java.megalib.models.MegaModel;
import org.java.megalib.parser.ParserException;
import org.java.megalib.parser.ParserListener;
import org.softlang.megalib.visualizer.exceptions.ModelReaderException;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class ModelReader {

    private VisualizerOptions options;

    private ModelLoader loader = new ModelLoader();

    private ParserListener listener;

    public ModelReader(VisualizerOptions options) {
        this.options = options;
    }

    /**
     * Reads the model that is based on the options filename. This model is simplified and reduced to its main relations, hence imports and basic language
     * components are not included within this model. To get a complete view on the model read by this reader, use {@link #readFull() readFull()} method.
     *
     * @return a simplfied view on the model without imports or basic language components
     * @throws ModelReaderException if there went something wrong during model loading process, see {@link ModelLoader#parse(java.lang.String, main.antlr.techdocgrammar.MegalibBaseListener)  ModelLoader.parse}
     */
    public MegaModel read() throws ModelReaderException {
        return parse(readFile());
    }

    /**
     * Creates a whole model view, including imports and basic language components. If you want to get a simplified view on the model, use {@link  #read() read()} method.
     *
     * @return the whole view on the model, including imports and basic language components
     * @throws ModelReaderException if there went something wrong during the model loading, see {@link ModelLoader#loadFile(java.lang.String) ModelLoader.loadFile}
     */
    public MegaModel readFull() throws ModelReaderException {
        try {
            loader.loadFile(options.getFilePath().toAbsolutePath().toString());

            return loader.getModel();
        } catch (IOException ex) {
            throw new ModelReaderException(ex);
        }
    }

    private MegaModel parse(String data) throws ModelReaderException {
        try {
            listener = new ParserListener(loader.getModel());
            loader.parse(data, listener);

            return listener.getModel();
        } catch (ParserException | IOException ex) {
            throw new ModelReaderException(ex);
        }
    }

    private String readFile() throws ModelReaderException {
        try {
            return Files.lines(options.getFilePath()).collect(Collectors.joining("\n"));
        } catch (IOException ex) {
            throw new ModelReaderException(ex);
        }
    }
}
