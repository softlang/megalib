package test.java.megalib.checker;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.java.megalib.checker.services.Checker;
import org.java.megalib.checker.services.MegaModelLoader;
import org.junit.Test;

public class ModelsTest {
	
	@Test
	public void testAll() {
		File f  = new File("../models");
		for(File o : f.listFiles()){
			if(o.getAbsolutePath().endsWith(".megal")){
				System.err.println(o.getName());
				Checker resultChecker = new Checker(new MegaModelLoader().createFromFile(o.getAbsolutePath()));
				resultChecker.doChecks();
				resultChecker.getWarnings().forEach(w -> System.out.println(w));
				assertEquals(0,resultChecker.getWarnings().size());
			}
		}
	}

}
