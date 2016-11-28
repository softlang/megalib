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
		
		if (argumentsExists(args)) {
			if(new File(args[1]).exists()){
				if(!args[1].endsWith(".megal")){
					if(new File(args[1]).isDirectory())
						checkDirectory(args[1]);
					else
						System.out.println("Could not recognize '"+args[1]+"' as a megamodel based on the file ending.");
				}else {
					checkFile(args[1]);
				}
			}else
				System.err.println("The file '"+args[1]+"' does not exist.");
		}else{
			if(args.length>0){
				String list = "";
				for(String a : args)
					list+=a+",";
				System.err.println("The arguments "+list+" are not valid.");
			}else{
				checkDirectory(".");
			}
		}
	}

	private static void checkDirectory(String dir) {
		File d = new File(dir);
		assert(d.isDirectory());
		for(File f : d.listFiles()){
			if(f.isDirectory())
				checkDirectory(f.getPath());
			else
				if(f.getPath().endsWith(".megal"))
					checkFile(f.getPath());
		}
	}

	private static boolean argumentsExists(String[] arguments) {
		return arguments.length>0 && arguments[0].contains("-f") && !arguments[1].isEmpty();
	}
	
	private static void checkFile(String path){
		System.out.println("Checking "+path);
		MegaModelLoader ml = new MegaModelLoader();
		ml.loadFile(path);
		if(ml.getModel().getCriticalWarnings().isEmpty())
			System.out.println("  Congratulations! There are no well-formedness issues at creation time.");
		else
			return;
		Check check = new Check(ml.getModel());
		if(check.getWarnings().isEmpty())
			System.out.println("  Congratulations! There are no warnings.");
		else
			check.getWarnings().forEach(w -> System.out.println(w));
	}
}
