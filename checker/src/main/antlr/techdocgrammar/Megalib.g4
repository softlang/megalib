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

imports: 'import' ID ('where{' (TAB substitution)+ '}')?;

substitution : ID 'substitutes' ID;

subtypeDeclaration: ID '<' ID (TAB '=' LINK)?;

instanceDeclaration: ID ':' ID (TAB '=' LINK)* (TAB ID ID)*;

relationDeclaration: ID '<' ID '#' ID;

relationInstance: ID ID ID (TAB ID ID)*;

functionDeclaration: ID ':' ID ('#' ID)* '->' ID ('#' ID)* ;

functionInstance: ID '(' ID (',' ID)* ')' '|->' (ID | ('(' ID (',' ID)* ')'));

link: ID '=' LINK;

//Tabs enforce formatting
TAB: '\t'|'    ';
ID: '?'? WORD ('.' WORD)*;
WORD: ([a-zA-Z0-9+#])+;
LINK: '"' (~[ \t\f\n\r])+ '"' ;
BLOCKCOMMENT: '/*' .*? '*/';
LINECOMMENT: '//' (~[\n\r])* -> skip;
WS: (' '|'\f'|'\n'|'\r') -> skip;