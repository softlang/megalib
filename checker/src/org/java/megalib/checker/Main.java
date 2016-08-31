/**
 * 
 */
package org.java.megalib.checker;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.java.megalib.checker.services.MegaModelLoader;

/**
 * @author mmay@uni-koblenz.de, aemmerichs@uni-koblenz.de
 *
 */
public class Main {
	
	public static void main(String[] args) {
		try {
			MegaModelLoader.createFromFile(getFilepathOfArguments(args));
		}
		catch (FileNotFoundException ex) {
			System.out.println("The File wasn't found on your harddrive!");
		} 
		catch (IOException ex) {
			System.out.println("Something went wrong with your file. Is it possibly emtpy?");
		} catch (EmptyFileNameException e) {
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
