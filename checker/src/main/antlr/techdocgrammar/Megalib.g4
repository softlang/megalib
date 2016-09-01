grammar Megalib;

//Head of the construct, containing entity or function
declaration: module? (imports)* ( entityDeclaration
								| entityInstance
								| relationDeclaration
								| relationInstance
								| functionDeclaration
								| functionInstance
								| link )+ EOF;

module: 'module' name;

imports: 'import' name;

entityDeclaration: name '<' name;

entityInstance: name ':' name;

relationDeclaration: name '<' name '#' name;

relationInstance: name  name  name;

functionDeclaration: name ':' name ('#' name)* '->' name ('#' name)* ;

functionInstance: name '(' name (',' name)* ')' '|->' (name | ('(' name (',' name)* ')'));

link: name '=' LINK;

//word used in defintions above
name: WORD;

//definition of word
WORD: ([a-zA-Z] | '.')+;
LINK: '"' (~(' '|'\t'|'\f'|'\n'|'\r'))+ '"' ;
WS: (' '|'\t'|'\f'|'\n'|'\r') -> skip;

Arrow: '|->';