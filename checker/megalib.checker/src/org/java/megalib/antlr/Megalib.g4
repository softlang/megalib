grammar Megalib;

//Head of the construct, containing entity or function
declaration: (entity ';' | function ';')+ EOF;

//function used for relation init
function: object '<' object ('#' object)+;

//entity used for entity init
entity: object '<Entity' | object '<' object;

//word used in defintions above
object: WORD;

//definition of word
WORD: ('abc')*;
