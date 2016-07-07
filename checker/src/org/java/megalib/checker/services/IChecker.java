/**
 * 
 */
package org.java.megalib.checker.services;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author mmay, aemmerichs
 *
 */
public interface IChecker {
	public Listener doCheck(String filepath) throws FileNotFoundException, IOException;
}
