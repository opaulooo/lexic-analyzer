package paulo.example.classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class Lexico {
    private String caminhoArquivo;
    private String nomeArquivo;
    private int c;
    PushbackReader br;
    BufferedReader initialBr;
    private ArrayList<String> reservedWords = new ArrayList<String>(Arrays.asList(
            "and", "array", "begin", "case", "const", "div",
            "do", "downto", "else", "end", "file", "for",
            "function", "goto", "if", "in", "label", "mod",
            "nil", "not", "of", "or", "packed", "procedure",
            "program", "record", "repeat", "set", "then",
            "to", "type", "until", "var", "while", "with",
            "read", "write", "real", "integer"
    ));

    public Lexico(String nomeArquivo) {
        this.caminhoArquivo = Paths.get(nomeArquivo).toAbsolutePath().toString();
        this.nomeArquivo = nomeArquivo;

        try {
            this.initialBr = new BufferedReader(new FileReader(caminhoArquivo, StandardCharsets.UTF_8));
            this.br = new PushbackReader(initialBr);
            this.c = this.br.read();
        } catch (IOException err) {
            System.err.println("Não foi possível abrir o arquivo ou ler do arquivo: " + this.nomeArquivo);
            err.printStackTrace();
        }
    }

    public Token getToken(int linha, int coluna) {
        int tamanhoToken = 0;
        int qtdEspacos = 0;
        StringBuilder lexema = new StringBuilder("");
        char character;
        Token token = new Token();

        try {
            while (c != -1) {
                character = (char) c;
                if (!(c == 13 || c == 10)) {
                    if (character == ' ') {
                        while (character == ' ') {
                            c = this.br.read();
                            qtdEspacos++;
                            character = (char) c;
                        }
                    } else if (Character.isLetter(character)) {
                        while (Character.isLetter(character) || Character.isDigit(character)) {
                            lexema.append(character);
                            c = this.br.read();
                            tamanhoToken++;
                            character = (char) c;
                        }

                        if (returnIfIsReservedWord(lexema.toString())) {
                            token.setClasse(Classe.cPalRes);
                        } else {
                            token.setClasse(Classe.cId);
                        }
                        token.setTamanhoToken(tamanhoToken);
                        token.setColuna(coluna + qtdEspacos);
                        token.setLinha(linha);
                        Valor valor = new Valor(lexema.toString());
                        token.setValor(valor);
                        return token;
                    } else if (Character.isDigit(character)) {
                        int numberOfPoints = 0;
                        while (Character.isDigit(character) || character == '.') {
                            if (character == '.') {
                                numberOfPoints++;
                            }
                            lexema.append(character);
                            c = this.br.read();
                            tamanhoToken++;
                            character = (char) c;
                        }
                        if (numberOfPoints <= 1) {
                            if (numberOfPoints == 0) {
                                token.setClasse(Classe.cInt);
                                Valor valor = new Valor(Integer.parseInt(lexema.toString()));
                                token.setValor(valor);
                            } else {
                                token.setClasse(Classe.cReal);
                                Valor valor = new Valor(Float.parseFloat(lexema.toString()));
                                token.setValor(valor);
                            }

                            token.setTamanhoToken(tamanhoToken);
                            token.setColuna(coluna + qtdEspacos);
                            token.setLinha(linha);
                            return token;
                        }
                    } else {
                        tamanhoToken++;
                        switch (character) {
                            case ':': {
                                int proximo = this.br.read();
                                character = (char) proximo;

                                if (character == '=') {
                                    token.setClasse(Classe.cAssignment);
                                    tamanhoToken++;
                                } else {
                                    this.br.unread(proximo);
                                    token.setClasse(Classe.cTwoPoints);
                                }
                                break;
                            }
                            case '+': {
                                token.setClasse(Classe.cPlus);
                                break;
                            }
                            case '-': {
                                token.setClasse(Classe.cMinus);
                                break;
                            }
                            case '/': {
                                token.setClasse(Classe.cDivide);
                                break;
                            }
                            case '*': {
                                token.setClasse(Classe.cMultiply);
                                break;
                            }
                            case '>': {
                                int proximo = this.br.read();
                                character = (char) proximo;
                                if (character == '=') {
                                    tamanhoToken++;
                                    token.setClasse(Classe.cBiggerEquals);
                                } else {
                                    this.br.unread(proximo);
                                    token.setClasse(Classe.cBigger);
                                }
                                break;
                            }
                            case '<': {
                                int proximo = this.br.read();
                                character = (char) proximo;
                                if (character == '=') {
                                    tamanhoToken++;
                                    token.setClasse(Classe.cLessEquals);
                                } else if (character == '>') {
                                    tamanhoToken++;
                                    token.setClasse(Classe.cDiferente);
                                } else {
                                    this.br.unread(proximo);
                                    token.setClasse(Classe.cLess);
                                }
                                break;
                            }
                            case '=': {
                                token.setClasse(Classe.cEquals);
                                break;
                            }
                            case ',': {
                                token.setClasse(Classe.cComma);
                                break;
                            }
                            case ';': {
                                token.setClasse(Classe.cSemicolon);
                                break;
                            }
                            case '.': {
                                token.setClasse(Classe.cPoint);
                                break;
                            }
                            case '(': {
                                token.setClasse(Classe.cParLeft);
                                break;
                            }
                            case ')': {
                                token.setClasse(Classe.cParRight);
                                break;
                            }
                            default: {
                                token.setClasse(Classe.cEOF);
                                break;
                            }
                        }
                        token.setTamanhoToken(tamanhoToken);
                        token.setColuna(coluna + qtdEspacos);
                        token.setLinha(linha);
                        token.setValor(null);

                        tamanhoToken++;
                        c = this.br.read();

                        return token;
                    }
                } else {
                    c = this.br.read();
                    linha++;
                    qtdEspacos = 0;
                    tamanhoToken = 0;
                    coluna = 1;
                }
            }

            token.setClasse(Classe.cEOF);
            return token;
        } catch (

        IOException err) {
            System.err.println("Não foi possível abrir o arquivo ou ler do arquivo: " + this.nomeArquivo);
            err.printStackTrace();
        }
        return null;
    }

    boolean returnIfIsReservedWord(String word) {
        return this.reservedWords.contains(word);
    }
}