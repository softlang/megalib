grammar Megalib;

//Head of the construct, containing entity or function
declaration: ((entity | relation | typeDeclaration | relationDeclaration) ';')+ EOF;

//function used for relation init
relation: object '<' object '#' object;

//entity used for entity init
entity: object '<Entity' | object '<' object;

typeDeclaration: object ':' object;

relationDeclaration: object ' ' object ' ' object;

function: ;

functionDeclaration: ;

//word used in defintions above
object: WORD;

//definition of word
WORD: [a-z,A-Z]+;
