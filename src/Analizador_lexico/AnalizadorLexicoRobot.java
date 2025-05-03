package Analizador_lexico;

/**
 *
 * @author Diego Quiroga
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnalizadorLexicoRobot extends JFrame {

    private JTextArea instruccionesArea;
    private JTextArea simbolosArea;
    private JTable tokenTable;
    private DefaultTableModel tableModel;
    private JButton btnAnalizar;

    // Lista de tokens reconocidos
    private List<Token> tokens = new ArrayList<>();

    // Mapa para contar diferentes tipos de tokens
    private Map<String, Integer> contadorTokens = new HashMap<>();

    // Definir los tipos de tokens básicos
    private static final String TIPO_LETRA = "letra";
    private static final String TIPO_DIGITO = "digito";
    private static final String TIPO_ESPACIO = "espacio";
    private static final String TIPO_PUNTO = "punto";
    private static final String TIPO_IGUAL = "igual";
    private static final String TIPO_PALABRA = "palabra";
    private static final String TIPO_NUMERO = "numero";
    private static final String TIPO_OPERADOR = "operador";
    private static final String TIPO_PALABRA_RESERVADA = "palabra_reservada";
    private static final String TIPO_DESCONOCIDO = "Simbolo no Reconocido";

    // Lista de palabras reservadas
    private Set<String> palabrasReservadas = new HashSet<>();

    // Lista de operadores
    private Set<String> operadores = new HashSet<>();

    public AnalizadorLexicoRobot() {
        // Inicializar palabras reservadas
        inicializarPalabrasReservadas();

        // Inicializar operadores
        inicializarOperadores();

        setTitle("Analizador Léxico Robot");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel superior con botones
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(0, 173, 181));

        btnAnalizar = new JButton("Analizar Código");

        topPanel.add(btnAnalizar);

        add(topPanel, BorderLayout.NORTH);

        // Panel central con áreas de texto y tabla
        JPanel centerPanel = new JPanel(new GridLayout(1, 3));

        // Panel de instrucciones
        JPanel instruccionesPanel = new JPanel(new BorderLayout());
        instruccionesPanel.setBorder(BorderFactory.createTitledBorder("Instrucciones"));
        instruccionesArea = new JTextArea();
        instruccionesArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        // Numeración de líneas
        TextLineNumber tln = new TextLineNumber(instruccionesArea);
        JScrollPane instruccionesScroll = new JScrollPane(instruccionesArea);
        instruccionesScroll.setRowHeaderView(tln);
        instruccionesPanel.add(instruccionesScroll);

        // Panel de tabla de símbolos
        JPanel simbolosPanel = new JPanel(new BorderLayout());
        simbolosPanel.setBorder(BorderFactory.createTitledBorder("Tabla de Símbolos"));
        simbolosArea = new JTextArea();
        simbolosArea.setEditable(false);
        JScrollPane simbolosScroll = new JScrollPane(simbolosArea);
        simbolosPanel.add(simbolosScroll);

        // Panel para la tabla de tokens
        JPanel resultadosPanel = new JPanel(new BorderLayout());
        resultadosPanel.setBorder(BorderFactory.createTitledBorder("Tokens"));

        // Tabla de tokens
        String[] columnNames = {"TOKEN", "TIPO"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tokenTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(tokenTable);

        resultadosPanel.add(tableScroll, BorderLayout.CENTER);

        centerPanel.add(instruccionesPanel);
        centerPanel.add(simbolosPanel);
        centerPanel.add(resultadosPanel);

        add(centerPanel, BorderLayout.CENTER);

        // Configurar eventos
        configurarEventos();

        // Cargar ejemplo inicial
        cargarEjemplo();
    }

    private void inicializarPalabrasReservadas() {
        // Añadir palabras reservadas para el robot
        palabrasReservadas.add("Robot");
        palabrasReservadas.add("iniciar");
        palabrasReservadas.add("base");
        palabrasReservadas.add("cuerpo");
        palabrasReservadas.add("garra");
        palabrasReservadas.add("velocidad");
        palabrasReservadas.add("avanzar");
        palabrasReservadas.add("retroceder");
        palabrasReservadas.add("girar");
        palabrasReservadas.add("detener");
        palabrasReservadas.add("esperar");
        palabrasReservadas.add("mov_base");
        palabrasReservadas.add("mov_brazo");
        palabrasReservadas.add("mov_garra");
        palabrasReservadas.add("abrir");
        palabrasReservadas.add("cerrar");
    }

    private void inicializarOperadores() {
        operadores.add("=");
        operadores.add("+");
        operadores.add("-");
        operadores.add("*");
        operadores.add("/");
        operadores.add(">");
        operadores.add("<");
        operadores.add(">=");
        operadores.add("<=");
        operadores.add("==");
        operadores.add("!=");
    }

    private void configurarEventos() {
        btnAnalizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analizarCodigo();
            }
        });
    }

    private void cargarEjemplo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Robot r1\n");
        sb.append("r1.iniciar\n");
        sb.append("r1.base=60\n");
        sb.append("r1.cuerpo=55\n");
        sb.append("r1.garra=100\n");
        sb.append("r1.velocidad=10\n");
        sb.append("r1.avanzar\n");
        sb.append("r1.girar=90\n");

        instruccionesArea.setText(sb.toString());
    }

    private void analizarCodigo() {
        // Limpiar resultados anteriores
        tokens.clear();
        contadorTokens.clear();
        tableModel.setRowCount(0);

        // Obtener el texto completo
        String codigo = instruccionesArea.getText();

        // Analizar el texto 
        analizarTexto(codigo);

        // Mostrar tokens en la tabla
        mostrarTokens();

        // Actualizar tabla de símbolos con contadores
        actualizarTablaSimbolos();
    }

    private void analizarTexto(String texto) {
        StringBuilder tokenActual = new StringBuilder();
        boolean esPosibleOperador = false;

        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);

            if (c == '\n') {
                // Finalizar token en curso antes del salto de línea
                procesarTokenActual(tokenActual);
                tokenActual.setLength(0);
                esPosibleOperador = false;

                // Agregar token de nueva línea
                registrarToken(String.valueOf(c), "espacio");
                continue;
            }

            if (Character.isLetter(c) || (Character.isDigit(c) && tokenActual.length() > 0 && Character.isLetter(tokenActual.charAt(0)))) {
                // Estamos en un identificador o palabra
                if (esPosibleOperador && tokenActual.length() > 0) {
                    procesarTokenActual(tokenActual);
                    tokenActual.setLength(0);
                    esPosibleOperador = false;
                }
                tokenActual.append(c);

                // También registrar la letra individual si es la primera del token
                /*if (tokenActual.length() == 1 && Character.isLetter(c)) {
                    registrarToken(String.valueOf(c), TIPO_LETRA);
                }*/
            } else if (Character.isDigit(c)) {
                // Estamos en un número o parte de un identificador
                if (esPosibleOperador && tokenActual.length() > 0) {
                    procesarTokenActual(tokenActual);
                    tokenActual.setLength(0);
                    esPosibleOperador = false;
                }
                tokenActual.append(c);

                // También registrar el dígito individual
                registrarToken(String.valueOf(c), TIPO_DIGITO);
            } else if (Character.isWhitespace(c)) {
                // Finalizar cualquier token en curso
                if (tokenActual.length() > 0) {
                    procesarTokenActual(tokenActual);
                    tokenActual.setLength(0);
                }
                esPosibleOperador = false;

                // Registrar el espacio
                registrarToken(String.valueOf(c), TIPO_ESPACIO);
            } else if (c == '.') {
                // Finalizar cualquier token en curso
                if (tokenActual.length() > 0) {
                    procesarTokenActual(tokenActual);
                    tokenActual.setLength(0);
                }
                esPosibleOperador = false;

                // Registrar el punto
                registrarToken(String.valueOf(c), TIPO_PUNTO);
            } else if (esOperador(String.valueOf(c))) {
                // Si ya teníamos un posible operador, procesarlo
                if (tokenActual.length() > 0) {
                    // Verificar si es un operador compuesto
                    if (esPosibleOperador && esOperador(tokenActual.toString() + c)) {
                        tokenActual.append(c);
                        procesarTokenActual(tokenActual);
                        tokenActual.setLength(0);
                        esPosibleOperador = false;
                    } else {
                        procesarTokenActual(tokenActual);
                        tokenActual.setLength(0);
                        tokenActual.append(c);
                        esPosibleOperador = true;
                    }
                } else {
                    tokenActual.append(c);
                    esPosibleOperador = true;
                }
            } else {
                // Finalizar cualquier token en curso
                if (tokenActual.length() > 0) {
                    procesarTokenActual(tokenActual);
                    tokenActual.setLength(0);
                }
                esPosibleOperador = false;

                // Registrar el carácter desconocido
                registrarToken(String.valueOf(c), TIPO_DESCONOCIDO);
            }
        }

        // Finalizar cualquier token pendiente al final del texto
        if (tokenActual.length() > 0) {
            procesarTokenActual(tokenActual);
        }
    }

    private void procesarTokenActual(StringBuilder tokenActual) {
        if (tokenActual.length() == 0) {
            return;
        }
        String token = tokenActual.toString();

        // Determinar el tipo de token
        String tipo;

        if (esOperador(token)) {
            tipo = TIPO_OPERADOR;
        } else if (esPalabraReservada(token)) {
            tipo = TIPO_PALABRA_RESERVADA;
        } else if (esNumero(token)) {
            tipo = TIPO_NUMERO;
        } else {
            tipo = TIPO_PALABRA;
        }

        registrarToken(token, tipo);
    }

    private boolean esOperador(String token) {
        return operadores.contains(token);
    }

    private boolean esPalabraReservada(String token) {
        return palabrasReservadas.contains(token);
    }

    private boolean esNumero(String token) {
        try {
            Integer.parseInt(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void registrarToken(String tokenTexto, String tipo) {
        // Si es un espacio en blanco, asegurarse de que sea visible en la tabla
        String textoMostrado = tokenTexto;
        if (tokenTexto.equals(" ")) {
            textoMostrado = "[espacio]";
        } else if (tokenTexto.equals("\t")) {
            textoMostrado = "[tab]";
        } else if (tokenTexto.equals("\n")) {
            textoMostrado = "[nueva línea]";
        } else if (tokenTexto.equals("\r")) {
            textoMostrado = "[retorno]";
        }

        // Registrar token en la lista
        tokens.add(new Token(textoMostrado, tipo));

        // Incrementar contador de este tipo de token
        contadorTokens.put(tipo, contadorTokens.getOrDefault(tipo, 0) + 1);
    }

    private void mostrarTokens() {
        // Limpiar la tabla
        tableModel.setRowCount(0);

        // Añadir tokens a la tabla
        for (Token token : tokens) {
            Object[] row = {
                token.getToken(),
                token.getTipo(),};
            tableModel.addRow(row);
        }
    }

    private void actualizarTablaSimbolos() {
        // Mostrar el contador de tokens
        StringBuilder sb = new StringBuilder();
        sb.append("CONTADOR DE TOKENS:\n\n");

        // Mostrar el total de tokens
        sb.append("Total de tokens: ").append(tokens.size()).append("\n\n");

        // Mostrar contadores por tipo
        sb.append("Por tipo:\n");
        for (Map.Entry<String, Integer> entry : contadorTokens.entrySet()) {
            sb.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        // Mostrar palabras reservadas encontradas
        Set<String> palabrasReservadasEncontradas = new HashSet<>();
        for (Token token : tokens) {
            if (token.getTipo().equals(TIPO_PALABRA_RESERVADA)) {
                palabrasReservadasEncontradas.add(token.getToken());
            }
        }

        if (!palabrasReservadasEncontradas.isEmpty()) {
            sb.append("\nPalabras reservadas encontradas:\n");
            for (String palabra : palabrasReservadasEncontradas) {
                sb.append("- ").append(palabra).append("\n");
            }
        }

        simbolosArea.setText(sb.toString());
    }

    private class Token {

        private String token;
        private String tipo;

        public Token(String token, String tipo) {
            this.token = token;
            this.tipo = tipo;
        }

        public String getToken() {
            return token;
        }

        public String getTipo() {
            return tipo;
        }
    }

    public class TextLineNumber extends JPanel {

        private final JTextArea textComponent;
        private final Font font;

        public TextLineNumber(JTextArea textComponent) {
            this.textComponent = textComponent;
            this.font = new Font("monospaced", Font.PLAIN, 12);
            setPreferredSize(new Dimension(30, 1));
            setBackground(new Color(230, 230, 230));
            setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Configuración gráfica
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setFont(font);
            g2d.setColor(Color.GRAY);

            // Altura de línea
            FontMetrics fm = g2d.getFontMetrics();
            int lineHeight = fm.getHeight();

            // Dibujar números de línea
            int startOffset = 0;
            Rectangle rect = g2d.getClipBounds();
            int startLineNumber = 1;
            int x = getWidth() - fm.stringWidth(String.valueOf(startLineNumber)) - 5;

            for (int i = 0; i * lineHeight <= rect.y + rect.height; i++) {
                if (i * lineHeight >= rect.y) {
                    String lineNumber = String.valueOf(startLineNumber + i);
                    g2d.drawString(lineNumber, x, i * lineHeight + fm.getAscent());
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AnalizadorLexicoRobot().setVisible(true);
            }
        });
    }
}
