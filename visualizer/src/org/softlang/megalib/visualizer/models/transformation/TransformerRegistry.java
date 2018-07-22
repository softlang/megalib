package org.softlang.megalib.visualizer.models.transformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.softlang.megalib.visualizer.VisualizerOptions;

public class TransformerRegistry {
    private static final Map<String,Function<VisualizerOptions,? extends Transformer>> TRANSFORMERS = new HashMap<>();
    private static final Map<String, String> CommandToFileEnding  = new HashMap<>();
    
    
    public static void registerTransformer(String name, String fileEnding,
                                           Function<VisualizerOptions,? extends Transformer> creatingFunc) {
        TRANSFORMERS.put(name, creatingFunc);
        CommandToFileEnding.put(name, fileEnding);
    }

    public static String getFileEnding(String commandName) {
    	return CommandToFileEnding.get(commandName);
    }
    
    public static Transformer getInstance(VisualizerOptions options) {
        return Optional.ofNullable(TRANSFORMERS.get(options.getTransformationType()))
                .map(t -> t.apply(options))
                .orElseThrow(() -> new IllegalArgumentException());
    }

    public static List<String> getRegisteredTransformerNames() {
        return TRANSFORMERS.keySet().stream().collect(Collectors.toList());
    }
}
