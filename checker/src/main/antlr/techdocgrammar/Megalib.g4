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

imports: 'import' ID ('where' '{' (INDENT substitution)+ '}')?;

substitution : ID 'substitutes' ID;

subtypeDeclaration: ID '<' ID (INDENT '=' LINK)?;

instanceDeclaration: ID ':' ID (INDENT '=' LINK)* (INDENT ID ID)*;

relationDeclaration: ID '<' ID '#' ID;

relationInstance: ID ID ID (INDENT ID ID | INDENT '=' LINK)*;

functionDeclaration: ID ':' ID ('#' ID)* '->' ID ('#' ID)* ;

functionInstance: ID '(' ID (',' ID)* ')' '|->' (ID | ('(' ID (',' ID)* ')'));

link: ID '=' LINK;

//Tabs enforce formatting
INDENT: ('\n'|'\r') (' '|'\t')+;
ID: '?'? WORD ('.' WORD)*;
WORD: ([a-zA-Z0-9+#\-])+;
LINK: '"' (~[ \t\f\n\r])+ '"' ;
BLOCKCOMMENT: '/*' .*? '*/';
LINECOMMENT: '//' (~[\n\r])* -> skip;
WS: (' '|'\f'|'\n'|'\r'|'\t') -> skip;