/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jflex_analizador;

import java.io.File;

/**
 *
 * @author Diego Quiroga
 */
public class Principal {
    public static void main(String[] args) {
        String ruta= "C:/Users/Diego Quiroga/OneDrive/Documentos/NetBeansProjects/AUTOMATAS/src/jflex_analizador/Lexer.flex";
        generarLexer(ruta);
    }
    public static void generarLexer(String ruta){
        File archivo = new File(ruta);
        JFlex.Main.generate(archivo);
    }
}

