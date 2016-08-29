package test.java.megalib.checker;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.java.megalib.checker.services.Checker;
import org.java.megalib.checker.services.ResultChecker;
import org.junit.Before;
import org.junit.Test;

public class CheckModels {
	
public Checker checker;
	
	@Before
	public void setUp(){
		checker = new Checker();
	}
	
	@Test
	public void testAll() {
		Checker checker = new Checker();
		File f  = new File("../models");
		for(File o : f.listFiles()){
			if(o.getAbsolutePath().endsWith(".megal")){
				try {
					byte[] data = FileUtils.readFileToString(o).getBytes();
					ByteArrayInputStream input = new ByteArrayInputStream(data);
					ResultChecker resultChecker = new ResultChecker(checker.getListener(input).getModel());
					System.out.println(o.getName());
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
