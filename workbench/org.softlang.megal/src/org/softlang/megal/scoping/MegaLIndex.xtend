package org.softlang.megal.scoping

import javax.inject.Inject
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.resource.IContainer
import org.softlang.megal.megaL.MegaLPackage
import org.eclipse.emf.ecore.EClass

class MegaLIndex {
	@Inject ResourceDescriptionsProvider rdp
	@Inject extension IContainer.Manager cm

	def getResourceDescription(EObject o) {
		val index = rdp.getResourceDescriptions(o.eResource)
		index.getResourceDescription(o.eResource.URI)
	}

	def getExportedEObjectDescriptions(EObject o) {
		o.getResourceDescription.getExportedObjects
	}

	def getExportedClassesEObjectDescriptions(EObject o) {
		o.getResourceDescription.getExportedObjectsByType(MegaLPackage.eINSTANCE.module)
	}

	def getVisibleEObjectDescriptions(EObject o, EClass type) {
		o.getVisibleContainers.map [ container |
			container.getExportedObjectsByType(type)
		].flatten
	}

	def getVisibleClassesDescriptions(EObject o) {
		o.getVisibleEObjectDescriptions(MegaLPackage.eINSTANCE.module)
	}

	def getVisibleContainers(EObject o) {
		val index = rdp.getResourceDescriptions(o.eResource)
		val rd = index.getResourceDescription(o.eResource.URI)
		cm.getVisibleContainers(rd, index)
	}

	def getVisibleExternalClassesDescriptions(EObject o) {
		val allVisibleClasses = o.getVisibleClassesDescriptions
		val allExportedClasses = o.getExportedClassesEObjectDescriptions
		val difference = allVisibleClasses.toSet
		difference.removeAll(allExportedClasses.toSet)
		return difference.toMap[qualifiedName]
	}
}