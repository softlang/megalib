package org.softlang.megal

import org.eclipse.xtext.generator.IGenerator2
import org.softlang.megal.generator.MegaLJsonGenerator
import com.google.inject.Binder
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider
import org.softlang.megal.scoping.MegaLImportedNamespaceAwareLocalScopeProvider

/*
 * generated by Xtext 2.12.0
 */

class MegaLRuntimeModule extends AbstractMegaLRuntimeModule {
	
	override void configureIScopeProviderDelegate(Binder binder) {
		binder.bind(org.eclipse.xtext.scoping.IScopeProvider).annotatedWith(
			com.google.inject.name.Names.named(AbstractDeclarativeScopeProvider.NAMED_DELEGATE)).to(
			MegaLImportedNamespaceAwareLocalScopeProvider); 
	}


}
