package org.softlang.megal

import org.softlang.megal.megaL.Module
import org.softlang.megal.megaL.Instance
import org.softlang.megal.megaL.Type
import org.softlang.megal.megaL.RelDecl
import org.softlang.megal.megaL.FunDecl

class MegaLModelUtil {
	
	def instances(Module m){
		m.statements.filter(Instance)
	}
	
	def types(Module m){
		m.statements.filter(Type)
	}
	
	def relations(Module m){
		m.statements.filter(RelDecl)
	}
	
	def functions(Module m){
		m.statements.filter(FunDecl)
	}
}