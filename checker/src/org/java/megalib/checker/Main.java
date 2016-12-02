/**
 * 
 */
package org.java.megalib.checker;

import java.io.File;

import org.java.megalib.checker.services.Check;
import org.java.megalib.checker.services.MegaModelLoader;

/**
 * @author mmay@uni-koblenz.de, aemmerichs@uni-koblenz.de
 * @author heinz
 *
 */
public class Main {

	public static void main(String[] args) {
		try {
			if (validArguments(args)) {
				if (new File(args[1]).exists()) {
					if (!args[1].endsWith(".megal")) {
						if (new File(args[1]).isDirectory()) {
							if (args.length > 2 && args[2].equals("-nocon"))
								checkDirectory(args[1], true);
							else
								checkDirectory(args[1], false);
						} else
							System.err.println(
									"Could not recognize '" + args[1] + "' as a megamodel based on the file ending.");
					} else {
						if (args.length > 2 && args[2].equals("-nocon"))
							checkFile(args[1], true);
						else
							checkFile(args[1], false);
					}
				} else
					System.err.println("The file '" + args[1] + "' does not exist.");
			} else {
				if (args.length > 0) {
					if (args.length == 1 && args[0].equals("-nocon"))
						checkDirectory(".", true);
					System.err.println("The arguments " + args + " are not valid.");
				} else {
					checkDirectory(".", false);
				}
			} 
		} catch (Exception e) {
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
			if (f.isDirectory())
				checkDirectory(f.getPath(), nocon);
			else if (f.getPath().endsWith(".megal"))
				checkFile(f.getPath(), nocon);
		}
	}

	private static void checkFile(String path, boolean nocon) {
		System.out.println("Checking " + path);
		MegaModelLoader ml = new MegaModelLoader();
		ml.loadFile(path);
		if (ml.getModel().getCriticalWarnings().isEmpty())
			System.out.println("  Congratulations! There are no well-formedness issues at creation time.");
		else
			return;
		Check check = new Check(ml.getModel(), nocon);
		if (check.getWarnings().isEmpty())
			System.out.println("  Congratulations! There are no warnings.");
		else
			check.getWarnings().forEach(w -> System.out.println(w));
	}

}
