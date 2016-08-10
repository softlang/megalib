/**
 * 
 */
package org.java.megalib.checker.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.java.megalib.models.MegaModel;

/**
 * @author mmay, aemmerichs
 *
 */
public interface IChecker {
	public MegaModel checkFile(String filepath) throws FileNotFoundException, IOException;
	public Listener getListener(InputStream stream) throws IOException;
}
