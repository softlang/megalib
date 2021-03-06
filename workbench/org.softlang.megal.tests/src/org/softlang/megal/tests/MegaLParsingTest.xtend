/*
 * generated by Xtext 2.12.0
 */
package org.softlang.megal.tests

import com.google.inject.Inject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.softlang.megal.megaL.Module

@RunWith(XtextRunner)
@InjectWith(MegaLInjectorProvider)
class MegaLParsingTest {
	@Inject
	ParseHelper<Module> parseHelper
	
	@Test
	def void loadModel() {
		val result = parseHelper.parse('''
			module Parser
			
			import antlr.Core
			
			{- 
			   @Description: "Hello" 
			   @Rationale: "World!"
			-}
			Language < Entity .
		''')
		Assert.assertNotNull(result)
		for (er : result.eResource.errors) {
			println(er)
		}
		Assert.assertTrue(result.eResource.errors.isEmpty)
	}
}
