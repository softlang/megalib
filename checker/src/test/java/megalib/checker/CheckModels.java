package test.java.megalib.checker;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.java.megalib.checker.services.Checker;
import org.java.megalib.checker.services.MegaModelLoader;
import org.junit.Test;

public class CheckModels {
	
	@Test
	public void testAll() {
		File f  = new File("../models");
		for(File o : f.listFiles()){
			if(o.getAbsolutePath().endsWith(".megal")){
				try {
					Checker resultChecker = new Checker(MegaModelLoader.createFromFile(o.getAbsolutePath()));
					resultChecker.doChecks();
					assertEquals(0,resultChecker.getWarnings().size());
					
				} catch (IOException e) {
					System.err.println("Failed reading "+o.getAbsolutePath());
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}

}
