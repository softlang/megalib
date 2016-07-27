grammar Megalib;

//Head of the construct, containing entity or function
declaration: module? (imports)* ( entity 
								| relation 
								| typeDeclaration 
								| relationDeclaration
								| functionDeclaration
								| function
								| description )+ EOF;

module: 'module' object;

imports: 'import' object;

description: object'='object;

//function used for relation init
relationDeclaration: object '<' object '#' object;

//entity used for entity init
entity: object '<' object;

typeDeclaration: object ':' object;

relation: object  object  object;

function: object '(' object ('x' object)* ')' Arrow object;

functionDeclaration: object ':' object ('x' object)* '->' object;

//word used in defintions above
object: WORD;

//definition of word
WORD: [a-zA-Z]+;
WS: (' '|'\t'|'\f'|'\n'|'\r') -> skip;

Arrow: '|->';