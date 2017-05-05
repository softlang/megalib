/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.softlang.megalib.visualizer.cli.CommandLineArguments;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class VisualizerOptions {

    public static VisualizerOptions of(CommandLineArguments args) {
        Path filePath = Paths.get(args.getFilePath());
        return new VisualizerOptions(filePath.toAbsolutePath(), args.getType().toLowerCase());
    }

    private Path filePath;

    private String type;


    private VisualizerOptions(Path filePath, String type){
        this.filePath = filePath;
        this.type = type;
    }

    public String getModelName() {
        return filePath.getFileName().toString().replaceAll("\\.megal", "");
    }

    public Path getFilePath() {
        return filePath;
    }

    public String getTransformationType() {
        return type;
    }

}
