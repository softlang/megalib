/**
 *
 */
package org.java.megalib.checker;

import java.io.File;
import java.io.IOException;

import org.java.megalib.checker.services.WellformednessCheck;
import org.java.megalib.checker.services.ModelLoader;

/**
 * @author mmay@uni-koblenz.de, aemmerichs@uni-koblenz.de
 * @author heinz
 */
public class Main {

    public static void main(String[] args) {
        try {
            if (validArguments(args)) {
                File f = new File(args[1]);
                if (!f.exists()) {
                    System.err.println("The file '" + args[1] + "' does not exist.");
                    return;
                }

                if (!args[1].endsWith(".megal")) {
                    if (f.isDirectory()) {
                        if (args.length > 2 && args[2].equals("-nocon")) {
                            checkDirectory(f.getAbsolutePath(), true);
                        } else {
                            checkDirectory(f.getAbsolutePath(), false);
                        }
                    } else {
                        System.err.println("Could not recognize '" + args[1] + "' as a megamodel.");
                    }
                } else {
                    if (args.length > 2 && args[2].equals("-nocon")) {
                        checkFile(f.getAbsolutePath(), true);
                    } else {
                        checkFile(f.getAbsolutePath(), false);
                    }
                }

            } else {
                if (args.length > 0) {
                    if (args.length == 1 && args[0].equals("-nocon")) {
                        checkDirectory(".", true);
                    }
                    System.err.println("The arguments " + args + " are not valid.");
                } else {
                    checkDirectory(".", false);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Checker handled main exception : " + e.toString());
        }
    }

    private static boolean validArguments(String[] arguments) {
        return arguments.length > 0 && arguments[0].contains("-f") && !arguments[1].isEmpty();
    }

    private static void checkDirectory(String dir, boolean nocon) {
        File d = new File(dir);
        assert (d.isDirectory());
        for (File f : d.listFiles()) {
            if (f.isDirectory()) {
                checkDirectory(f.getPath(), nocon);
            } else
                if (f.getPath().endsWith(".megal")) {
                    checkFile(f.getPath(), nocon);
                }
        }
    }

    private static void checkFile(String path, boolean nocon) {
        System.out.println("Checking " + path);
        ModelLoader ml = new ModelLoader();

        try {
            if (ml.loadFile(path)) {
                System.out.println("  Congratulations! There are no well-formedness issues at creation time.");
            } else
                return;
        }
        catch (IOException e) {
            System.err.println("Unable to load file " + path);
            return;
        }
        WellformednessCheck check = new WellformednessCheck(ml.getModel(), nocon);
        if (check.getWarnings().isEmpty()) {
            System.out.println("  Congratulations! There are no warnings.");
        } else {
            check.getWarnings().forEach(w -> System.out.println(w));
        }
    }

}
