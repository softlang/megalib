/**
 * 
 */
package org.java.megalib.checker;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.java.megalib.checker.services.Checker;
import org.java.megalib.checker.services.IChecker;
import org.java.megalib.checker.services.Listener;

/**
 * @author mmay@uni-koblenz.de, aemmerichs@uni-koblenz.de
 *
 */
public class Main {
	private static IChecker checkerService;
	
	public static void main(String[] args) {
		checkerService = new Checker();
		try {
			//Listener result = checkerService.doCheck(getFilepathOfArguments(args));
			Listener result = checkerService.doCheck("./TestFiles/testFile.megal");
			printResults(result);
		}
//		catch (EmptyFileNameException ex) {
//			System.out.println("No filepath specified!");
//		}
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

	private static void printResults(Listener listener) {
		System.out.println("");
		System.out.println(listener.getEntities());
		System.out.println(listener.getRelations());
		System.out.println(listener.getObjects());
		System.out.println(listener.getFunctions());
	}
	
}
