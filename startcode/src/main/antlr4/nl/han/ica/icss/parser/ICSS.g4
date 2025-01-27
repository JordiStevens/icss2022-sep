grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER: ---
stylesheet: varDeclaration* styleRule*  EOF;

styleRule: selector OPEN_BRACE body CLOSE_BRACE;

selector: LOWER_IDENT | ID_IDENT | CLASS_IDENT;
body: (varDeclaration | declaration | ifStatement)*;

varDeclaration: varReference ASSIGNMENT_OPERATOR value SEMICOLON;
declaration: propertyName COLON value SEMICOLON;

propertyName: LOWER_IDENT;
value: literal | operation;


boolLiteral: TRUE | FALSE;
colorLiteral: COLOR;
pixelLiteral: PIXELSIZE;
percentageLiteral: PERCENTAGE;
scalarLiteral: SCALAR;

literal: boolLiteral | colorLiteral | pixelLiteral | percentageLiteral | scalarLiteral | varReference;

varReference: CAPITAL_IDENT;

operation: operation MUL operation | operation (PLUS | MIN) operation | literal;

ifStatement: IF BOX_BRACKET_OPEN (varReference | boolLiteral) BOX_BRACKET_CLOSE
    OPEN_BRACE body CLOSE_BRACE
    elseStatement ?;

elseStatement: ELSE OPEN_BRACE body CLOSE_BRACE;

