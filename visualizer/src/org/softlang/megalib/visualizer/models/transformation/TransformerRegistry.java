package org.softlang.megalib.visualizer.models.transformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.softlang.megalib.visualizer.VisualizerOptions;

public class TransformerRegistry {
    private static final Map<String,Function<VisualizerOptions,? extends Transformer>> TRANSFORMERS = new HashMap<>();

    public static void registerTransformer(String name,
                                           Function<VisualizerOptions,? extends Transformer> creatingFunc) {
        TRANSFORMERS.put(name, creatingFunc);
    }

    public static Transformer getInstance(VisualizerOptions options) {
        return TRANSFORMERS.entrySet().stream()
        				   .filter(e -> e.getKey().equals(options.getTransformationType()))
                           .map(Entry::getValue)
                           .findFirst()
                           .orElseThrow(IllegalStateException::new)
                           .apply(options);
    }

    public static List<String> getRegisteredTransformerNames() {
        return TRANSFORMERS.keySet().stream().collect(Collectors.toList());
    }
}
