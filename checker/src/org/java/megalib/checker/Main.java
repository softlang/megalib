/**
 * 
 */
package org.java.megalib.checker;

import org.java.megalib.checker.services.MegaModelLoader;

/**
 * @author mmay@uni-koblenz.de, aemmerichs@uni-koblenz.de
 *
 */
public class Main {
	
	public static void main(String[] args) {
		try {
			new MegaModelLoader().createFromFile(getFilepathOfArguments(args));
		}
		catch (EmptyFileNameException e) {
			e.printStackTrace();
		}
	}
	
	private static String getFilepathOfArguments(String[] arguments) throws EmptyFileNameException{
		if (argumentsExists(arguments)) {
			return arguments[2];
		}
		throw new EmptyFileNameException();
	}

	private static boolean argumentsExists(String[] arguments) {
		return arguments.length>0 && arguments[0].contains("-f") && !arguments[2].isEmpty();
	}	
}
