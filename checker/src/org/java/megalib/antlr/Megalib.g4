grammar Megalib;

//Head of the construct, containing entity or function
declaration: module? (imports)* ( entityDeclaration
								| entityInstance
								| relationDeclaration
								| relationInstance
								| functionDeclaration
								| functionInstance
								| link )+ EOF;

module: 'module' object;

imports: 'import' object;

entityDeclaration: object '<' object;

entityInstance: object ':' object;

relationDeclaration: object '<' object '#' object;

relationInstance: object  object  object;

functionDeclaration: object ':' object ('x' object)* '->' object;

functionInstance: object '(' object ('x' object)* ')' Arrow object;

link: object '=' WORD;

//word used in defintions above
object: WORD;

//definition of word
WORD: [a-zA-Z]+;
LINK: '\"http' ('s')? '://' [a-zA-z0-9]+ '.' [a-z] ('/' [a-zA-Z0-9]+)* '\"';
WS: (' '|'\t'|'\f'|'\n'|'\r') -> skip;

Arrow: '|->';