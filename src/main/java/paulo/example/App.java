package paulo.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import paulo.example.classes.Classe;
import paulo.example.classes.Lexico;
import paulo.example.classes.Token;

// Esquema de voltar um caractere
//do {
//    reader.mark(1);
//} while(Character.isWhitespace(reader.read()))
//
//reader.reset();

// Criar uma String com os caracteres lidos
//StringBuilder response= new StringBuilder();
//while ((c = bufferedReader.read()) != -1) {
//    response.append( (char)c ) ;
//}
//String result = response.toString();

public class App 
{
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Modo de usar: java -jar NomePrograma NomeArquivoCodigo");
            //return;
        }
        //String nomeArquivo = args[0];
        String nomeArquivo = "teste.js";

        substituirTabulacao(nomeArquivo);

        Lexico lexico = new Lexico(nomeArquivo);

        Token token;
        int linha = 0;
        int coluna = 0;

        do {
            token = lexico.getToken(linha, coluna);
            System.out.println(token);
            coluna = token.getColuna()+1;
            linha = token.getLinha();
        } while (token.getClasse() != Classe.cEOF);

    }

    public static void substituirTabulacao(String nomeArquivo) {
        Path caminhoArquivo = Paths.get(nomeArquivo);
        int numeroEspacosPorTab = 4;
        StringBuilder juntando = new StringBuilder();
        String espacos;

        for (int cont = 0; cont < numeroEspacosPorTab; cont++) {
            juntando.append(" ");
        }
        espacos = juntando.toString();

        String conteudo;
        try {
            conteudo = new String(Files.readAllBytes(caminhoArquivo), StandardCharsets.UTF_8);
            conteudo = conteudo.replace("\t", espacos);
            Files.write(caminhoArquivo, conteudo.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
