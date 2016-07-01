grammar Megalib;
@header {
package org.java.megalib.antlr;
}

//Head of the construct, containing entity or function
declartation: (((entity)|(function))(' ')*)* EOF;

//function used for relation init
function: object '<' (object ('#' object)+);

//entity used for entity init
entity: object '< Entity' | object '<' object;

//word used in defintions above
object: Word;

//definition of word
Word: ('abc')*; 

ENTNAME : [a-z,A-Z,0-9]+ ;
ENTTYPENAME : [a-z,A-Z,0-9]+ ;
