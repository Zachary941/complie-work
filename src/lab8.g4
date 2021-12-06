grammar lab8;
compUnit     : (decl | funcDef)+;
decl         : constDecl | varDecl;
constDecl    : 'const' BType constDef  (',' constDef)* ';';
constDef     : Ident  ('[' constExp ']')* '=' constInitVal;
constInitVal : constExp
                | '{'  (constInitVal ( ',' constInitVal )*)?  '}';
constExp     : addExp;
varDecl      : BType varDef  (',' varDef )* ';';
varDef       : Ident ( '[' constExp ']' )*
                               | Ident ( '[' constExp ']' )* '=' initVal;
initVal      : exp
                | '{' ( initVal ( ',' initVal )* )? '}';
funcDef      : funcType Ident '(' (funcFParams)? ')' block;
funcFParams  : funcFParam ( ',' funcFParam )*;
funcFParam   : BType Ident ('[' ']' ( '[' exp ']' )*)?;
funcType     : 'void' | 'int';
block        : '{'  (blockItem)* '}';
blockItem    : decl | stmt;
stmt         : lVal '=' exp ';'
                | block
                | 'break' ';'
                | 'continue' ';'
                |  exp? ';'
                | 'if' '(' cond ')' stmt ( 'else' stmt )?
                | 'while' '(' cond ')' stmt
                | 'return' (exp)? ';';
exp          : addExp;
cond         : lOrExp; // [new]
lVal         : Ident ('[' exp ']')*;
primaryExp   : '(' exp ')' | lVal | Number;
unaryExp     : primaryExp
                | Ident '(' (funcRParams)? ')'
                | unaryOp unaryExp;
unaryOp      : '+' | '-' | '!';  // 保证 '!' 只出现在 Cond 中 [changed]
funcRParams  : exp ( ',' exp )*;
mulExp       : unaryExp
                | mulExp ('*' | '/' | '%') unaryExp;
addExp       : mulExp
                | addExp ('+' | '-') mulExp;
relExp       : addExp
                | relExp ('<' | '>' | '<=' | '>=') addExp;  // [new]
eqExp        : relExp
                | eqExp ('==' | '!=') relExp;  // [new]
lAndExp      : eqExp
                | lAndExp '&&' eqExp  ;// [new]
lOrExp       : lAndExp
                | lOrExp '||' lAndExp;  // [new]
BType       : 'int';
Number             : Decimal_const | Octal_const | Hexadecimal_const;
Ident              :((Nondigit)(Nondigit|Decimal_const
                    |Octal_const|Hexadecimal_const|'int'|'if')*)
                    |('int'|'if')((Nondigit|Decimal_const
                              |Octal_const|Hexadecimal_const|'int'|'if')+);

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
WHITE_SPACE: [ \t\n\r] -> skip;