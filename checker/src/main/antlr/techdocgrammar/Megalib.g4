grammar Megalib;

//Head of the construct, containing entity or function
declaration: module? (imports)* statement+ EOF;

statement:	 subtypeDeclaration
			   | instanceDeclaration
			   | relationDeclaration
			   | relationInstance
			   | functionDeclaration
			   | functionInstance
			   | link ;

module: 'module' ID;

imports: 'import' ID ('where{' (substitution)+ '}')?;

substitution : ID 'substitutes' ID;

subtypeDeclaration: ID '<' ID;

instanceDeclaration: ID ':' ID (TAB ID ID)*;

relationDeclaration: ID '<' ID '#' ID;

relationInstance: ID ID ID (TAB ID ID)*;

functionDeclaration: ID ':' ID ('#' ID)* '->' ID ('#' ID)* ;

functionInstance: ID '(' ID (',' ID)* ')' '|->' (ID | ('(' ID (',' ID)* ')'));

link: ID '=' LINK;

ID: '?'? WORD ('.' WORD)* '+'?;
TAB: '\t'|'    ';
WORD: ([a-zA-Z0-9])+;
LINK: '"' (~[ \t\f\n\r])+ '"' ;
BLOCKCOMMENT: '/*' .*? '*/' -> skip;
LINECOMMENT: '//' (~[\n\r])* -> skip;
WS: (' '|'\f'|'\n'|'\r') -> skip;