package test.java.megalib.checker.services;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.java.megalib.checker.services.Check;
import org.java.megalib.checker.services.MegaModelLoader;
import org.junit.Test;

public class ModelsCheck {
	
	@Test
	public void testAll() {
		File f  = new File("../models");
		for(File o : f.listFiles()){
			if(o.getAbsolutePath().endsWith(".megal")){
				System.err.println(o.getName());
				MegaModelLoader ml = new MegaModelLoader();
				ml.loadFile(o.getAbsolutePath());
				Check check = new Check(ml.getModel());
				check.getWarnings().forEach(w -> System.out.println(w));
				assertEquals(0,check.getWarnings().size());
			}
		}
	}
}