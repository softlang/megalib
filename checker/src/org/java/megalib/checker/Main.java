/**
 * 
 */
package org.java.megalib.checker;

import org.java.megalib.checker.services.Check;
import org.java.megalib.checker.services.EmptyFileNameException;
import org.java.megalib.checker.services.MegaModelLoader;

/**
 * @author mmay@uni-koblenz.de, aemmerichs@uni-koblenz.de
 * @author heinz
 *
 */
public class Main {
	
	public static void main(String[] args) {
		try {
			MegaModelLoader ml = new MegaModelLoader();
			ml.loadFile(getFilepathOfArguments(args));
			
			if(ml.getModel().getCriticalWarnings().isEmpty())
				System.out.println("Congratulations! There are no well-formedness issues at creation time.");
			else
				return;
			Check check = new Check(ml.getModel());
			if(check.getWarnings().isEmpty())
				System.out.println("Congratulations! There are no warnings.");
			else
				check.getWarnings().forEach(w -> System.out.println(w));
		}
		catch (EmptyFileNameException e) {
			e.printStackTrace();
		}
	}
	
	private static String getFilepathOfArguments(String[] arguments) throws EmptyFileNameException{
		if (argumentsExists(arguments)) {
			return arguments[1];
		}
		throw new EmptyFileNameException();
	}

	private static boolean argumentsExists(String[] arguments) {
		return arguments.length>0 && arguments[0].contains("-f") && !arguments[1].isEmpty();
	}	
}
