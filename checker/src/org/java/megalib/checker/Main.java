/**
 * 
 */
package org.java.megalib.checker;

import org.java.megalib.checker.services.Checker;
import org.java.megalib.checker.services.MegaModelLoader;

/**
 * @author mmay@uni-koblenz.de, aemmerichs@uni-koblenz.de
 *
 */
public class Main {
	
	public static void main(String[] args) {
		try {
			Checker resultChecker = new Checker(new MegaModelLoader().createFromFile(getFilepathOfArguments(args)));
			resultChecker.doChecks();
			resultChecker.getWarnings().forEach(w -> System.out.println(w));
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
