grammar Megalib;

//Head of the construct, containing entity or function
declaration: module? (imports)* group+ EOF;

group: BLOCKCOMMENT statement+;

statement:	 subtypeDeclaration
			   | instanceDeclaration
			   | relationDeclaration
			   | relationInstance
			   | functionDeclaration
			   | functionInstance
			   | link ;

module: 'module' ID;

imports: 'import' ID ('where' '{' substitution+ '}')?;

substitution : ID 'substitutes' ID;

subtypeDeclaration: ID '<' ID (';' '=' LINK)? '.';

instanceDeclaration: ID ':' ID (';' '=' LINK)* (';' ID ID)* '.';

relationDeclaration: ID '<' ID '#' ID '.';

relationInstance: ID ID ID ('=' LINK)* (ID ID)*;

functionDeclaration: ID ':' ID ('#' ID)* '->' ID ('#' ID)* ;

functionInstance: ID '(' ID (',' ID)* ')' '|->' (ID | ('(' ID (',' ID)* ')'));

link: ID '=' LINK;

ID: ('?'|'^')? WORD ('.' WORD)*;
WORD: ([a-zA-Z0-9+#\-])+;
LINK: '"' (~[ \t\f\n\r])+ '"' ;
BLOCKCOMMENT: '/*' .*? '*/';
LINECOMMENT: '//' (~[\n\r])* -> skip;
WS: (' '|'\f'|'\n'|'\r'|'\t') -> skip;