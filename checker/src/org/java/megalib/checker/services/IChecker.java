/**
 * 
 */
package org.java.megalib.checker.services;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.java.megalib.models.MegaModel;

/**
 * @author mmay, aemmerichs
 *
 */
public interface IChecker {
	public MegaModel doCheck(String filepath) throws FileNotFoundException, IOException;
}
