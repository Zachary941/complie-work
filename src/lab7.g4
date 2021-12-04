grammar lab7;
compUnit     : (decl | funcDef)+;// [changed]
decl         : constDecl |  varDecl;
constDecl    : 'const' bType constDef  (',' constDef)* ';';
bType       : 'int';
constDef     : ident ( '[' constExp ']' )* '=' constInitVal;
constInitVal : constExp | '{' (constInitVal ( ',' constInitVal )*)?   '}';
constExp     : addExp;
varDecl      : bType varDef  (',' varDef )* ';';
varDef       : ident ( '[' constExp ']' )*
                | ident ('[' constExp ']')* '=' initVal;
initVal      : exp | '{' ( initVal ( ',' initVal )* )? '}';
funcDef      : funcType ident '(' (funcFParams)? ')' block;// [changed]
funcType     : 'void' | 'int';// [changed]
funcFParams  : funcFParam ( ',' funcFParam )* ;// [new]
funcFParam   : bType ident ('[' ']' ( '[' exp ']' )*)?; // [new]
block        : '{'  (blockItem)* '}';
blockItem    : decl | stmt;
stmt         : lVal '=' exp ';'
                | block
                | 'break' ';'
                | 'continue' ';'
                |  exp? ';'
                | 'if' '(' cond ')' stmt ( 'else' stmt )?
                | 'while' '(' cond ')' stmt
                | 'return' (exp)? ';'; // [changed]
exp          : addExp;
cond         : lOrExp; // [new]
lVal         : ident ('[' exp ']')* ;
primaryExp   : '(' exp ')' | lVal | number;
unaryExp     : primaryExp
                | ident '(' (funcRParams)? ')'
                | unaryOp unaryExp;
unaryOp      : '+' | '-' | '!';
funcRParams  : exp ( ',' exp )*;
mulExp       : unaryExp
                | mulExp ('*' | '/' | '%') unaryExp;
addExp       : mulExp
                | addExp ('+' | '-') mulExp;
relExp       : addExp
                | relExp ('<' | '>' | '<=' | '>=') addExp;
eqExp        : relExp
                | eqExp ('==' | '!=') relExp; 
lAndExp      : eqExp
                | lAndExp '&&' eqExp  ;
lOrExp       : lAndExp
                | lOrExp '||' lAndExp;
number             : Decimal_const | Octal_const | Hexadecimal_const;
ident              :Nondigit(Nondigit|Decimal_const
                    |Octal_const|Hexadecimal_const|'int'|'if')*;

Nondigit           : [a-zA-Z_];

Decimal_const      : Nonzero_digit  Digit*;

Octal_const        : '0'  Octal_digit*;

Hexadecimal_const  : Hexadecimal_prefix Hexadecimal_digit
                      Hexadecimal_digit*;

Hexadecimal_prefix : '0x' | '0X';
fragment
Nonzero_digit      : '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9';
fragment
Octal_digit        : '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7';
Digit              : '0' | Nonzero_digit;
fragment
Hexadecimal_digit  : '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
                      | 'a' | 'b' | 'c' | 'd' | 'e' | 'f'
                      | 'A' | 'B' | 'C' | 'D' | 'E' | 'F';
