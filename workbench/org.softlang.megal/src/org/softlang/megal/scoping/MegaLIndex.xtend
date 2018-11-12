package org.softlang.megal.scoping

import javax.inject.Inject
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider
import org.eclipse.emf.ecore.EObject

class MegaLIndex {
	@Inject ResourceDescriptionsProvider rdp
	def getResourceDescription(EObject o) {
		val index = rdp.getResourceDescriptions(o.eResource)
		index.getResourceDescription(o.eResource.URI)
	}
	
	def getExportedEObjectDescriptions(EObject o){
		o.getResourceDescription.getExportedObjects
	}
}