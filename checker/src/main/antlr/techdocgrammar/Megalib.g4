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
			   | LINECOMMENT
			   | BLOCKCOMMENT)+ ;

module: 'module' name;

imports: 'import' name ('where{' (substitution)+ '}')?;

substitution : name 'substitutes' name;

subtypeDeclaration: name '<' name;

instanceDeclaration : name ':' name;

relationDeclaration: name '<' name '#' name;

relationInstance: name  name  name;

functionDeclaration: name ':' name ('#' name)* '->' name ('#' name)* ;

functionInstance: name '(' name (',' name)* ')' '|->' (name | ('(' name (',' name)* ')'));

link: name '=' LINK;

name: '?'? WORD ('.' WORD)* '+'?;

BLOCKCOMMENT: '/*' (WORD|'?'|'.'|'<'|'#'|':'|'>'|','|';'|'\"'|'('|')'|' ')+ '*/';
LINECOMMENT: '//' (WORD|'?'|'.'|'<'|'#'|':'|'>'|','|';'|'\"'|'('|')'|' ')+;
WORD: ([a-zA-Z0-9] | '\'' | '-')+;
LINK: '"' (~(' '|'\t'|'\f'|'\n'|'\r'))+ '"' ;
WS: (' '|'\t'|'\f'|'\n'|'\r') -> skip;