/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer;

import java.io.IOException;

import org.java.megalib.checker.services.ModelLoader;
import org.java.megalib.models.MegaModel;
import org.softlang.megalib.visualizer.exceptions.ModelReaderException;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class ModelReader {

    private VisualizerOptions options;

    private ModelLoader loader = new ModelLoader();

    public ModelReader(VisualizerOptions options) {
        this.options = options;
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
}
