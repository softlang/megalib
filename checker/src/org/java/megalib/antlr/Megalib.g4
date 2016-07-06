grammar Megalib;

//Head of the construct, containing entity or function
declaration: ((entity | relation | typeDeclaration | relationDeclaration| functionDeclaration| function) ';')+ EOF;

//function used for relation init
relationDeclaration: object '<' object '#' object;

//entity used for entity init
entity: object '<Entity' | object '<' object;

typeDeclaration: object ':' object;

relation: object ' ' object ' ' object;

function: object'('object ('#' object)* ') ->' object;

functionDeclaration: object ':' object ('#' object)* '-->' object;

//word used in defintions above
object: WORD;

WS: (' '|'\t'|'\f'|'\n'|'\r')+ -> skip;
WORD: [a-zA-Z]+;