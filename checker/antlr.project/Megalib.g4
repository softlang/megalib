grammar Megalib;
@header {
package java.megalib.antlr;
}

model : decl* ;

decl : entdecl | enttypedecl | reldecl ;
entdecl : ENTNAME ':' enttype ;
enttypedecl : ENTTYPENAME '<' enttype ;
reldecl : ENTNAME relname ENTNAME ;


enttype : 
	'Language' |
	'Technology' |
	'Artifact' |
	'Function' |
	'Concept';
relname : 
	'partOf' |
	'subsetOf' |
	'elementOf' |
	'implements' |
	'defines' |
	'conformsTo' |
	'correspondsTo' |
	'facilitates' |
	'uses';

ENTNAME : [a-z,A-Z,0-9]+ ;
ENTTYPENAME : [a-z,A-Z,0-9]+ ;
