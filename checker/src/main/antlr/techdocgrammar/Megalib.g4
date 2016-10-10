grammar Megalib;

//Head of the construct, containing entity or function
declaration: module? (imports)* ( subtypeDeclaration
								| instanceDeclaration
								| relationDeclaration
								| relationInstance
								| functionDeclaration
								| functionInstance
								| link )+ EOF;

module: 'module' name;

imports: 'import' name;

subtypeDeclaration: name '<' name;

//1.) instance, 2.) type, 3.) optional language
instanceDeclaration : name ':' name ( '<' name '>' )?;

relationDeclaration: name '<' name '#' name;

relationInstance: name  name  name;

functionDeclaration: name ':' name ('#' name)* '->' name ('#' name)* ;

functionInstance: name '(' name (',' name)* ')' '|->' (name | ('(' name (',' name)* ')'));

link: name '=' LINK;

//word used in definitions above
name: WORD;

//definition of word
WORD: '?'? ([a-zA-Z0-9] | '.')+ '+'?;
LINK: '"' (~(' '|'\t'|'\f'|'\n'|'\r'))+ '"' ;
WS: (' '|'\t'|'\f'|'\n'|'\r') -> skip;