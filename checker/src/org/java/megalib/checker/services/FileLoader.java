package org.java.megalib.checker.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileLoader implements IFileLoader{

	@Override
	public FileInputStream load(String filepath) throws FileNotFoundException {
		File file = new File(filepath);
		return new FileInputStream(file);
	}
}
