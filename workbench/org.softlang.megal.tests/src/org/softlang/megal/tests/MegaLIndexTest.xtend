package org.softlang.megal.tests

import com.google.inject.Inject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.Test
import org.junit.runner.RunWith
import org.softlang.megal.megaL.Module
import org.softlang.megal.scoping.MegaLIndex
import org.junit.Assert

@RunWith(XtextRunner)
@InjectWith(typeof(MegaLInjectorProvider))
class MegaLIndexTest {
	@Inject extension ParseHelper<Module>
	@Inject extension MegaLIndex
	@Test def void testExportedEObjectDescriptions(){
		'''
		module antlr
		
		{-
		@Description: ""
		@Rationale: ""
		-}
		Language < Entity.
		Java : Language.
		
		'''.parse.assertExportedEObjectDescriptions
					("antlr, antlr.Language, antlr.Java")
	}
	
	def private assertExportedEObjectDescriptions(Module o, CharSequence expected){
		Assert::assertEquals(expected.toString, o.getExportedEObjectDescriptions.map[qualifiedName].join(", "))
	}
}