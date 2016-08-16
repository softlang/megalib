/**
 * 
 */
package org.java.megalib.checker;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.java.megalib.checker.services.Checker;
import org.java.megalib.checker.services.IChecker;

/**
 * @author mmay@uni-koblenz.de, aemmerichs@uni-koblenz.de
 *
 */
public class Main {
	private static IChecker checkerService;
	
	public static void main(String[] args) {
		checkerService = new Checker();
		try {
			checkerService.checkFile(getFilepathOfArguments(args));
		}
		catch (FileNotFoundException ex) {
			System.out.println("The File wasn't found on your harddrive!");
		} 
		catch (IOException ex) {
			System.out.println("Something went wrong with your file. Is it possibly emtpy?");
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
