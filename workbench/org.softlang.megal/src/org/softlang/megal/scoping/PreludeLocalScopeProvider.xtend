package org.softlang.megal.scoping

import org.eclipse.xtext.scoping.impl.ImportedNamespaceAwareLocalScopeProvider
import org.eclipse.xtext.scoping.impl.ImportNormalizer
import org.eclipse.xtext.naming.QualifiedName

class PreludeLocalScopeProvider extends ImportedNamespaceAwareLocalScopeProvider {

	override getImplicitImports(boolean ignoreCase) {
		newArrayList(new ImportNormalizer(
			QualifiedName.create("megal", "prelude"),
			true, // wildcard
			ignoreCase
		))
	}
}