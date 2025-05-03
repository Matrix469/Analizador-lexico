package jflex_analizador;
import static jflex_analizador.Tokens.*;
%%
%class Lexer
%type Tokens
%{
    public String lexeme;
%}

L = [a-zA-Z_]
D = [0-9]
espacio = [ \t\r\n]
%%

/* Palabras reservadas del robot */
Robot |
iniciar |
avanzar |
retroceder |
girar |
detener |
esperar |
abrir |
cerrar {lexeme=yytext(); return Reservadas;}

/*Metodos*/
cuerpo|
garra|
velocidad|
base|
cerrarGarra|
abrirGarra|
repetir {lexeme=yytext(); return Metodos;}

/* Operadores */
"=" {lexeme=yytext(); return Igual;}
"+" {lexeme=yytext(); return Suma;}
"-" {lexeme=yytext(); return Resta;}
"*" {lexeme=yytext(); return Multiplicacion;}
"/" {lexeme=yytext(); return Division;}
">" {lexeme=yytext(); return Mayor;}
"<" {lexeme=yytext(); return Menor;}
">=" {lexeme=yytext(); return MayorIgual;}
"<=" {lexeme=yytext(); return MenorIgual;}
"==" {lexeme=yytext(); return IgualIgual;}
"!=" {lexeme=yytext(); return Diferente;}

/* Identificadores y números */
{L}({L}|{D})* {lexeme=yytext(); return Identificador;}
("(-"{D}+")")|{D}+ {lexeme=yytext(); return Numero;}

/* Puntos y otros símbolos */
"." {lexeme=yytext(); return Punto;}

/* Espacios en blanco (ahora como tokens) */
{espacio}+ {lexeme=yytext(); return Espacio;}  // Cambiado para que devuelva token Espacio

"//".* {/* Ignorar comentarios de una línea */}

/* Cualquier otro carácter no reconocido */
. {lexeme=yytext(); return ERROR;}