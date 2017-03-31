grammar Megalib;

//Head of the construct, containing entity or function
declaration: module (imports)* block+ EOF;

block: BLOCKCOMMENT statement+;

statement:	subtypeDeclaration
         | instanceDeclaration
			   | relationDeclaration
			   | relationInstance
			   | functionDeclaration
			   | functionApplication 
			   | namespace;

module: 'module' ID;

imports: 'import' ID ('where' '{' substitutionGroup (';' substitutionGroup)* '}')?;

substitutionGroup : '[' substitution (',' substitution)* ']' ;

substitution : ID '/' ID;

relationDeclaration: ID '<' ID '#' ID (';' link)* '.';

subtypeDeclaration: ID '<' ID (';' link)* '.';

instanceDeclaration: ID ':' ID (';' link)* (';' relationship)* '.';

relationInstance: ID (link | relationship) (';' (link | relationship))* '.';

relationship: ID ID;

link: ('~=' | '=') LINK;

namespace: WORD '::' LINK;

functionDeclaration: ID ':' ID ('#' ID)* '->' ID ('#' ID)* '.';

functionApplication: ID '(' ID (',' ID)* ')' '|->' (ID | ('(' ID (',' ID)* ')')) '.';

ID: ('?'|'^')? WORD ('.' WORD)*;
WORD: ([a-zA-Z0-9+#\-])+;
LINK: '"' (~[ \t\f\n\r"])+ '"' ;
BLOCKCOMMENT: '/*' .*? '*/';
LINECOMMENT: '//' (~[\n\r])* -> skip;
WS: (' '|'\f'|'\n'|'\r'|'\t') -> skip;