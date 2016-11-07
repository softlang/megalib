grammar Megalib;

//Head of the construct, containing entity or function
declaration: module? (imports)* statements EOF;


statements:	( subtypeDeclaration
			   | instanceDeclaration
			   | relationDeclaration
			   | relationInstance
			   | functionDeclaration
			   | functionInstance
			   | link
			   | COMMENT)+ ;

module: 'module' name;

imports: 'import' name;

subtypeDeclaration: name '<' name;

// instance : type (possible for artifacts <language,role,manifestation>)
instanceDeclaration : name ':' name ( '<' name ',' name ',' name  '>' )?;

relationDeclaration: name '<' name '#' name;

relationInstance: name  name  name;

functionDeclaration: name ':' name ('#' name)* '->' name ('#' name)* ;

functionInstance: name '(' name (',' name)* ')' '|->' (name | ('(' name (',' name)* ')'));

link: name '=' LINK;

name: '?'? WORD ('.' WORD)* '+'?;

COMMENT: '//' (WORD|'?'|'.'|'<'|'#'|':'|'>'|','|';'|'\"'|'('|')'|' ')+;
WORD: ([a-zA-Z0-9] | '\'' | '-')+;
LINK: '"' (~(' '|'\t'|'\f'|'\n'|'\r'))+ '"' ;
WS: (' '|'\t'|'\f'|'\n'|'\r') -> skip;