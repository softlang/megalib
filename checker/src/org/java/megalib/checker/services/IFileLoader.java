/**
 * 
 */
package org.java.megalib.checker.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author mmay, aemmerichs
 *
 */
public interface IFileLoader {
	public FileInputStream load(String filepath) throws FileNotFoundException;
}
