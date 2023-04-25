package paulo.example.classes;

public class Sintatico {
    private Lexico lexico;
    private Token token;

    public Sintatico(String nomeArquivo) {
        this.lexico = new Lexico(nomeArquivo);
        token = lexico.getToken(0, 0);
    }

    public void analisar() {
        programa();
    }

    public void programa() {
        if (token.getClasse() == Classe.cPalRes &&
                token.getValor().getIDValor().equals("program")) {
            token = lexico.getToken(0, 0);

            if (token.getClasse() == Classe.cPoint) {
                token = lexico.getToken(0, 0);
            } else {
                System.out.println("Linha: " + token.getLinha() + " Coluna: " + token.getColuna()
                        + "Faltou ponto final no PROGRAM");
            }
        } else {
            System.out.println("Faltou começar por PROGRAM");
        }
    }

    public void id() {
        if (token.getClasse() != Classe.cId) {
            System.out.println(
                    "Linha: " + token.getLinha() + " Coluna: " + token.getColuna() + "Faltou o id após o program");
        }
    }
}