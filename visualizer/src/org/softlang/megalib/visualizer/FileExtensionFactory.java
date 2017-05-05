/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class FileExtensionFactory {

    private static Map<String, String> FILE_EXTENSIONS = new HashMap<>();

    public static void registerFileExtension(String transformer, String extension) {
        FILE_EXTENSIONS.put(transformer, extension);
    }

    public static String get(String transformer) {
        return FILE_EXTENSIONS.containsKey(transformer) ? FILE_EXTENSIONS.get(transformer) : "";
    }

}
