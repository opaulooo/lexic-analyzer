package paulo.example.classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class Lexico {
    private String caminhoArquivo;
    private String nomeArquivo;

    public Lexico(String nomeArquivo) {
        this.caminhoArquivo = Paths.get(nomeArquivo).toAbsolutePath().toString();
        this.nomeArquivo = nomeArquivo;
    }

    public Token getToken(int linha, int coluna) {
        int c;
        char caractere;
        int e;
        StringBuilder lexema = new StringBuilder("");
        Token token = new Token(); 

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo, StandardCharsets.UTF_8))) {
            e = 0;

            while ((c = br.read()) != -1) { // -1 fim da stream
                if (!(c == 13 || c == 10)) {
                    caractere = (char) c;

                    if (e == 0) {
                        if (Character.isLetter(caractere)) {
                            lexema.append(caractere);
                            e = 1;
                        } else if (Character.isDigit(caractere)) {
                            lexema.append(caractere);
                            e = 2;
                        } else {
                            System.out.println("Outra coisa: " + caractere);
                        }
                    } else if (e == 1) {
                        if (Character.isLetter(caractere) || Character.isDigit(caractere)) {
                            lexema.append(caractere);
                            e = 1;
                        } else {
                            Valor valor = new Valor();
                            token.setClasse(Classe.cIdent);
                            valor.setValorIdentificador(lexema.toString());
                            token.setValor(valor);
                            e = 3;
                        }
                    } else if (e == 2) {
                        if (Character.isDigit(caractere)) {
                            lexema.append(caractere);
                            e = 2;
                        } else {
                            token.setClasse(Classe.cInt);
                            Valor valor = new Valor();
                            valor.setValorIntteiro(Integer.parseInt(lexema.toString()));
                            token.setValor(valor);
                            System.out.println("Foi para o 3");
                            e = 3;
                        }
                    } 
                    
                    if (e == 3) {
                        System.out.println("Entrou no 3");
                        return token;
                    }
                }               
            }

            token.setClasse(Classe.cEOF);

            return token;
        } catch (IOException err) {
            System.err.println("Não foi possível abrir o arquivo ou ler do arquivo: " + this.nomeArquivo);
            err.printStackTrace();
        }
        return null;
    }
}
