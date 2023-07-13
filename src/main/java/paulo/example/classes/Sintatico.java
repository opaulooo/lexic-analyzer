package paulo.example.classes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Sintatico {
    private Lexico lexico;
    private Token token;
    private int linha;
    private int coluna;

    private TabelaSimbolos tabela;

    private int endereco;
    private int contRotulo = 1;

    private List<Registro> ultimasVariaveisDeclaradas = new ArrayList<>();

    private String nomeArquivoSaida;
    private String caminhoArquivoSaida;
    private BufferedWriter bw;
    private FileWriter fw;

    private String rotulo = "";
    private String rotElse;

    public Sintatico(String nomeArquivo) {
        linha = 1;
        coluna = 1;
        this.lexico = new Lexico(nomeArquivo);
    }

    public void LerToken() {
        token = lexico.getToken(linha, coluna);
        coluna = token.getColuna() + token.getTamanhoToken();
        linha = token.getLinha();
        System.out.println(token);

    }

    public void Analisar() {
        LerToken();
        this.endereco = 0;

        nomeArquivoSaida = "teste.c";
        caminhoArquivoSaida = Paths.get(nomeArquivoSaida).toAbsolutePath().toString();

        bw = null;
        fw = null;

        try {
            fw = new FileWriter(caminhoArquivoSaida, Charset.forName("UTF-8"));
            bw = new BufferedWriter(fw);
            Programa();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("--------Tabela de Simbolos--------");
        System.out.println(this.tabela);
    }

    private String criarRotulo(String texto) {
        String retorno = "rotulo" + texto + contRotulo;
        contRotulo++;
        return retorno;
    }

    public void ErrorNotify(String msg) {
        System.err.println("l: " + token.getLinha() +
                "\nc: " + token.getColuna() +
                "\n" + msg);
    }

    public void Programa() {
        if ((token.getClasse() == Classe.cPalRes)
                && (token.getValor().getIDValor().equalsIgnoreCase("program"))) {
            LerToken();
            if (token.getClasse() == Classe.cId) {
                LerToken();
                FirstAction();
                Body();
                if (token.getClasse() == Classe.cPoint) {
                    LerToken();
                } else {
                    ErrorNotify("Não foi identificado o Ponto ('.')");
                }
                SecondAction();
            } else {
                ErrorNotify("Não foi identificado 'Programa'");
            }
        } else {
            ErrorNotify("Não foi iniciado com 'Programa'");
        }
    }

    public void FirstAction()
    {
        tabela=new TabelaSimbolos();

        tabela.setTabelaPai(null);

        Registro registro=new Registro();
        registro.setNome(token.getValor().getIDValor());
        registro.setCategoria(Categoria.PROGRAMAPRINCIPAL);

        registro.setNivel(0);
        registro.setOffset(0);
        registro.setTabelaSimbolos(tabela);
        registro.setRotulo("main");
        tabela.inserirRegistro(registro);
        String codigo = "#include <stdio.h>\n" +
                "\nint main(){\n";

        CodeGenerator(codigo);

    }

    public void SecondAction()
    {
        Registro registro=new Registro();
        registro.setNome(null);
        registro.setCategoria(Categoria.PROGRAMAPRINCIPAL);
        registro.setNivel(0);
        registro.setOffset(0);
        registro.setTabelaSimbolos(tabela);
        registro.setRotulo("finalCode");
        tabela.inserirRegistro(registro);
        String codigo = "\n}\n";
        CodeGenerator(codigo);
    }
    private void ThirdAction(String type) {
        String codigo= '\t'+type;
        for(int i=0;i<this.ultimasVariaveisDeclaradas.size();i++)
        {
            codigo=codigo+' '+ this.ultimasVariaveisDeclaradas.get(i).getNome();
            if(i == this.ultimasVariaveisDeclaradas.size()-1)
            {
                codigo=codigo + ';';
            }
            else{
                codigo=codigo + ',';
            }
        }
        CodeGenerator(codigo);
    }

    public void FourthAction()
    {
        Registro registro=new Registro();
        registro.setNome(token.getValor().getIDValor());
        registro.setCategoria(Categoria.VARIAVEL);
        registro.setNivel(0);
        registro.setOffset(0);
        registro.setTabelaSimbolos(tabela);
        this.endereco++;
        registro.setRotulo("variavel"+this.endereco);
        ultimasVariaveisDeclaradas.add(registro);
        this.tabela.inserirRegistro(registro);
    }


    private void CodeGenerator(String instrucoes) {
        try {
            if (rotulo.isEmpty()) {
                bw.write(instrucoes + "\n");
            } else {
                bw.write(rotulo + ": " +  instrucoes + "\n");
                rotulo = "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Body() {
        Create();
        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("begin"))) {
            LerToken();
            Sentencas();
            if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("end"))) {
                LerToken();
            } else {
                ErrorNotify("Não foi finalizado com 'END'");
            }
        } else {
            ErrorNotify("Não foi encontrado 'begin' no Body");
        }
    }

    public void Create() {
        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("var"))) {
            LerToken();
            dvar();
            mais_dc();
        }
    }

    public void typeVar() {
        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("integer"))) {
            ThirdAction("int");
            LerToken();
        } else if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("real"))) {
            ThirdAction("float");
            LerToken();
        } else {
            ErrorNotify("Não foi feita a criação do INTEGER");
        }
    }

    public void Vars() {
        if (token.getClasse() == Classe.cId) {
            FourthAction();
            LerToken();
            AddVar();
        } else {
            ErrorNotify("Não foi identificado o ID");
        }
    }

    public void AddVar() {
        if (token.getClasse() == Classe.cComma) {
            LerToken();
            Vars();
        }
    }

    public void Sentencas() {
        Command();
        AddSentencas();
    }

    public void AddSentencas() {
        if (token.getClasse() == Classe.cSemicolon) {
            LerToken();
            cont_Sentencas();
        } else {
            ErrorNotify("Não foi identificado o ponto e vírgula (';')");
        }
    }

    public void cont_Sentencas() {
        if (((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("read"))) ||
                ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("write"))) ||
                ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("for"))) ||
                ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("repeat"))) ||
                ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("while"))) ||
                ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("if"))) ||
                ((token.getClasse() == Classe.cId))) {
            Sentencas();
        }
    }

    public void mais_dc() {
        if (token.getClasse() == Classe.cSemicolon) {
            LerToken();
            cont_dc();
        } else {
            ErrorNotify("Não foi identificado ponto e vírgula (';')");
        }
    }

    public void cont_dc() {
        if (token.getClasse() == Classe.cId) {
            dvar();
            mais_dc();
        }
    }

    public void dvar() {
        Vars();
        if (token.getClasse() == Classe.cTwoPoints) {
            LerToken();
            typeVar();
        } else {
            ErrorNotify("Não foi identificado os dois pontos (':')");
        }
    }

    public List<Token> ReadVar(List<Token> tokens) {
        if (token.getClasse() == Classe.cId) {
            tokens.add(token);
            LerToken();
            tokens = AddReadVar(tokens);
        }else {
            ErrorNotify(" -FALTOU O IDENTIFICADOR");
        }
        return tokens;
    }

    public List<Token> AddReadVar(List<Token> tokens) {
        if (token.getClasse() == Classe.cComma) {
            LerToken();
            tokens = ReadVar(tokens);
        }
        return tokens;
    }

    public String WriteVar(String code) {

        if (token.getClasse() == Classe.cId) {
            code=code+token.getValor().getIDValor();
            LerToken();
            code=AddWriteVar(code);
        }else {
            ErrorNotify(" -FALTOU O IDENTIFICADOR");
        }

        return code;
    }

    public String AddWriteVar(String code) {
        if (token.getClasse() == Classe.cComma) {
            code=code+ ',';
            LerToken();
            code=WriteVar(code);
        }
        return code;
    }

    public void Command() {

        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("read"))) {
            String code="\tscanf";
            LerToken();
            if (token.getClasse() == Classe.cParLeft) {
                LerToken();
                List<Token> tokens = new ArrayList<Token>();
                tokens = ReadVar(tokens);

                for(Token i: tokens){
                    code=code+"%d ";
                }
                code=code+"\", ";
                for(Token i: tokens){
                    if(i == tokens.get(tokens.size()-1)){
                        code=code+"&"+i.getValor().getIDValor();
                    }else{
                        code=code+"&"+i.getValor().getIDValor()+", ";
                    }
                }

                if (token.getClasse() == Classe.cParRight) {
                    code=code+");";
                    CodeGenerator(code);
                    LerToken();
                } else {
                    ErrorNotify("Não foi identificado o parênteses direito (')')");
                }
            } else {
                ErrorNotify("Não foi identificado o parênteses direito ('(')");
            }
        } else
        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("write"))) {
            String references="\tprintf";
            String code = "";
            LerToken();
            if (token.getClasse() == Classe.cParLeft) {
                references = references + "(\"";
                LerToken();
                code=code+WriteVar("");
                if (code.length() >  0) {
                    references = references + "%d ".repeat(code.split(",").length);
                    references = references + "\", ";
                } else {
                    references = references + "\"";
                }

                if (token.getClasse() == Classe.cParRight) {
                    code=code+");";
                    CodeGenerator(references + code);
                    LerToken();
                } else {
                    ErrorNotify("Não foi identificado o parênteses direito (')')");
                }
            } else {
                ErrorNotify("Não foi identificado o parênteses direito ('(')");
            }
        } else
        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("for"))) {
            String code="\n\tfor(";
            LerToken();
            if (token.getClasse() == Classe.cId) {
                String identifier = token.getValor().getIDValor();
                code=code+identifier;
                LerToken();

                if (token.getClasse() == Classe.cAssignment) {
                    LerToken();
                    Expressao();
                    if ((token.getClasse() == Classe.cPalRes)
                            && (token.getValor().getIDValor().equalsIgnoreCase("to"))) {
                        code=code+";";
                        LerToken();
                        code=code+identifier;
                        code=code+"<="+Expressao()+";";
                        code=code+identifier + "++)";
                        if ((token.getClasse() == Classe.cPalRes)
                                && (token.getValor().getIDValor().equalsIgnoreCase("do"))) {
                            LerToken();
                            if ((token.getClasse() == Classe.cPalRes)
                                    && (token.getValor().getIDValor().equalsIgnoreCase("begin"))) {
                                code=code+"{";
                                CodeGenerator(code);
                                LerToken();
                                Sentencas();
                                if ((token.getClasse() == Classe.cPalRes)
                                        && (token.getValor().getIDValor().equalsIgnoreCase("end"))) {
                                    String finalCode = "\t}";
                                    CodeGenerator(finalCode);
                                    LerToken();
                                } else {
                                    ErrorNotify("Não foi identificado o 'END' dentro do FOR");
                                }
                            } else {
                                ErrorNotify("Não foi identificado o 'BEGIN' dentro do FOR");
                            }
                        } else {
                            ErrorNotify("Não foi identificado o 'DO' dentro do FOR");
                        }
                    } else {
                        ErrorNotify("Não foi identificado o 'TO' dentro do FOR");
                    }
                } else {
                    ErrorNotify("Não foi identificado o ':' e '=' dentro do FOR");
                }
            } else {
                ErrorNotify("Não foi identificado o ID no início do FOR");
            }
        } else

        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("repeat"))) {
            String code="\n\tdo {\n\t";
            LerToken();
            CodeGenerator(code);
            Sentencas();
            if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("until"))) {
                LerToken();
                if (token.getClasse() == Classe.cParLeft) {
                    String finalCode="\n\t}while";
                    finalCode=finalCode+"(";
                    LerToken();
                    finalCode=finalCode+Condicao();
                    if (token.getClasse() == Classe.cParRight) {
                        finalCode=finalCode+");";
                        CodeGenerator(finalCode);
                        LerToken();
                    } else {
                        ErrorNotify("Não foi identificado o ')' no fim do 'REPEAT'");
                    }
                } else {
                    ErrorNotify("Não foi identificado o '(' no início do 'REPEAT'");
                }
            } else {
                ErrorNotify("Não foi identificado 'UNTIL' no 'REPEAT'");
            }
        }

        else if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("while"))) {
            String code="\n\twhile";
            LerToken();
            if (token.getClasse() == Classe.cParLeft) {
                code=code+"(";
                LerToken();
                code=code+Condicao();
                if (token.getClasse() == Classe.cParRight) {
                    code=code+")";
                    LerToken();
                    if ((token.getClasse() == Classe.cPalRes)
                            && (token.getValor().getIDValor().equalsIgnoreCase("do"))) {
                        LerToken();
                        if ((token.getClasse() == Classe.cPalRes)
                                && (token.getValor().getIDValor().equalsIgnoreCase("begin"))) {
                            code=code+"{\n";
                            CodeGenerator(code);
                            LerToken();
                            Sentencas();
                            if ((token.getClasse() == Classe.cPalRes)
                                    && (token.getValor().getIDValor().equalsIgnoreCase("end"))) {
                                code="\t}\n";
                                CodeGenerator(code);
                                LerToken();
                            } else {
                                ErrorNotify("Não foi identificado 'END' no 'WHILE'");
                            }
                        } else {
                            ErrorNotify("Não foi identificado 'BEGIN' no 'WHILE'");
                        }
                    } else {
                        ErrorNotify("Não foi identificado 'DO' no 'WHILE'");
                    }
                } else {
                    ErrorNotify("Não foi identificado ')' no 'WHILE'");
                }
            } else {
                ErrorNotify("Não foi identificado '(' no 'WHILE'");
            }
        } else if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("if"))) {
            String code="";
            LerToken();
            if (token.getClasse() == Classe.cParLeft) {
                code=code+"\n\tif(";
                LerToken();
                code=code+Condicao();
                if (token.getClasse() == Classe.cParRight) {
                    code=code+")";
                    LerToken();
                    if ((token.getClasse() == Classe.cPalRes)
                            && (token.getValor().getIDValor().equalsIgnoreCase("then"))) {
                        LerToken();
                        if ((token.getClasse() == Classe.cPalRes)
                                && (token.getValor().getIDValor().equalsIgnoreCase("begin"))) {
                            code=code +" {";
                            CodeGenerator(code);
                            LerToken();
                            Sentencas();
                            if ((token.getClasse() == Classe.cPalRes)
                                    && (token.getValor().getIDValor().equalsIgnoreCase("end"))) {
                                LerToken();
                                String finalCode = "";
                                finalCode = finalCode + "\t}";
                                CodeGenerator(finalCode);
                                pfalsa();
                            } else {
                                ErrorNotify("Não foi identificado 'END' no 'WHILE'");
                            }
                        } else {
                            ErrorNotify("Não foi identificado 'BEGIN' no 'WHILE'");
                        }
                    } else {
                        ErrorNotify("Não foi identificado 'DO' no 'WHILE'");
                    }
                } else {
                    ErrorNotify("Não foi identificado ')' no 'WHILE'");
                }
            } else {
                ErrorNotify("Não foi identificado '(' no 'WHILE'");
            }
        } else if (token.getClasse() == Classe.cId) {
            String code="\n\t";
            code=code+token.getValor().getIDValor();
            LerToken();
            if (token.getClasse() == Classe.cAssignment) {
                code=code+"=";
                LerToken();
                code=code+Expressao()+";";
                CodeGenerator(code);
            } else {
                ErrorNotify("Não foi identificada nenhuma atribuição");
            }
        }
    }

    public String Condicao() {
        return Expressao()+Relacao()+Expressao();
    }

    public void pfalsa() {
        String code = "";
        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("else"))) {
            code = code + "\telse";
            LerToken();
            if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("begin"))) {
                code = code + "{";
                CodeGenerator(code);
                LerToken();
                Sentencas();
                if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("end"))) {
                    String codigoFinal = "\n\t}";
                    CodeGenerator(codigoFinal);
                    LerToken();
                } else {
                    ErrorNotify("Não foi finalizado com 'END'");
                }
            } else {
                ErrorNotify("Não foi inicializado com com 'BEGIN'");
            }
        }
    }

    public String Relacao() {
        if (token.getClasse() == Classe.cEquals) {
            LerToken();
            return "=";
        } else if (token.getClasse() == Classe.cBigger) {
            LerToken();
            return ">";
        } else if (token.getClasse() == Classe.cLess) {
            LerToken();
            return "<";
        } else if (token.getClasse() == Classe.cBiggerEquals) {
            LerToken();
            return ">=";
        } else if (token.getClasse() == Classe.cLessEquals) {
            LerToken();
            return "<=";
        } else if (token.getClasse() == Classe.cDiferente) {
            LerToken();
            return "!=";
        } else {
            ErrorNotify("Não foi identificado o operador de relação");
        }
        return "";
    }

    public String Expressao() {
        return Termo() + DemaisTermos();
    }

    public String DemaisTermos() {
        if (token.getClasse() == Classe.cPlus || token.getClasse() == Classe.cMinus) {
            return PlusOrMinusOperation() + Termo() + DemaisTermos();
        }
        return "";
    }

    public String PlusOrMinusOperation() {
        String op = "";
        if (token.getClasse() == Classe.cPlus) {
            op = "+";
            LerToken();
        } else if (token.getClasse() == Classe.cMinus) {
            op = "-";
            LerToken();
        }else {
            ErrorNotify("Não foi identificado o operador de adição ou subtração");
        }
        return op;
    }

    public String Termo() {
        return Factors() + MoreFactors();
    }

    public String MoreFactors() {
        if (token.getClasse() == Classe.cMultiply || token.getClasse() == Classe.cDivide) {
            return MultiplyOrDivideOperation() + Factors() + MoreFactors();
        }
        return "";
    }

    public String MultiplyOrDivideOperation() {
        String op = "";
        if (token.getClasse() == Classe.cMultiply) {
            op = "*";
            LerToken();
        } else if (token.getClasse() == Classe.cDivide) {
            op = "/";
            LerToken();
        } else {
            ErrorNotify("Não foi identificado o operador de multiplicação ou divisão");
        }

        return op;
    }

    public String Factors() {
        String returnFator = "";
        if (token.getClasse() == Classe.cId) {
            returnFator = token.getValor().getIDValor();
            LerToken();
        } else if (token.getClasse() == Classe.cInt) {
            returnFator = String.valueOf(token.getValor().getValorINT());
            LerToken();
        } else if (token.getClasse() == Classe.cReal) {
            returnFator = String.valueOf(token.getValor().getValorDEC());
            LerToken();
        }else if (token.getClasse() == Classe.cParLeft){
            returnFator="(";
            LerToken();
            returnFator = returnFator + Expressao();
            if (token.getClasse() == Classe.cParRight){
                returnFator=returnFator + ")";
                LerToken();
            }else {
                ErrorNotify("Não foi identificado o parênteses direito (')')");
            }
        }else {
            ErrorNotify("Não foi identificado o Factors 'IN NUM EXP'");
        }

        return returnFator;
    }
}
