package paulo.example.classes;

public class Sintatico {
    private Lexico lexico;
    private Token token;
    private int linha;
    private int coluna;

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
        Programa();
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
                Body();
                if (token.getClasse() == Classe.cPoint) {
                    LerToken();
                } else {
                    ErrorNotify("Não foi identificado o Ponto ('.')");
                }
            } else {
                ErrorNotify("Não foi identificado 'Programa'");
            }
        } else {
            ErrorNotify("Não foi iniciado com 'Programa'");
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

    public void typeVar() {
        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("integer"))) {
            LerToken();
        } else if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("real"))) {
            LerToken();
        } else {
            ErrorNotify("Não foi feita a criação do INTEGER");
        }
    }

    public void Vars() {
        if (token.getClasse() == Classe.cId) {
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

    public void ReadVar() {
        if (token.getClasse() == Classe.cId) {
            LerToken();
            AddReadVar();
        } else {
            ErrorNotify("Não foi identificado o ID");
        }
    }

    public void AddReadVar() {
        if (token.getClasse() == Classe.cComma) {
            LerToken();
            ReadVar();
        }
    }

    public void WriteVar() {
        if (token.getClasse() == Classe.cId) {
            LerToken();
            AddWriteVar();
        } else {
            ErrorNotify("Não foi identificado o ID");
        }
    }

    public void AddWriteVar() {
        if (token.getClasse() == Classe.cComma) {
            LerToken();
            WriteVar();
        }
    }

    public void Command() {

        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("read"))) {
            LerToken();
            if (token.getClasse() == Classe.cParLeft) {
                LerToken();
                ReadVar();
                if (token.getClasse() == Classe.cParRight) {
                    LerToken();
                } else {
                    ErrorNotify("Não foi identificado o parênteses direito (')')");
                }
            } else {
                ErrorNotify("Não foi identificado o parênteses direito ('(')");
            }
        } else
        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("write"))) {
            LerToken();
            if (token.getClasse() == Classe.cParLeft) {
                LerToken();
                WriteVar();
                if (token.getClasse() == Classe.cParRight) {
                    LerToken();
                } else {
                    ErrorNotify("Não foi identificado o parênteses direito (')')");
                }
            } else {
                ErrorNotify("Não foi identificado o parênteses direito ('(')");
            }
        } else

        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("for"))) {
            LerToken();
            if (token.getClasse() == Classe.cId) {
                LerToken();

                if (token.getClasse() == Classe.cAssignment) {
                    LerToken();
                    Expressao();
                    if ((token.getClasse() == Classe.cPalRes)
                            && (token.getValor().getIDValor().equalsIgnoreCase("to"))) {
                        LerToken();
                        Expressao();
                        if ((token.getClasse() == Classe.cPalRes)
                                && (token.getValor().getIDValor().equalsIgnoreCase("do"))) {
                            LerToken();
                            if ((token.getClasse() == Classe.cPalRes)
                                    && (token.getValor().getIDValor().equalsIgnoreCase("begin"))) {
                                LerToken();
                                Sentencas();
                                if ((token.getClasse() == Classe.cPalRes)
                                        && (token.getValor().getIDValor().equalsIgnoreCase("end"))) {
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
            LerToken();
            Sentencas();
            if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("until"))) {
                LerToken();
                if (token.getClasse() == Classe.cParLeft) {
                    LerToken();
                    Condicao();
                    if (token.getClasse() == Classe.cParRight) {
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
            LerToken();
            if (token.getClasse() == Classe.cParLeft) {
                LerToken();
                Condicao();
                if (token.getClasse() == Classe.cParRight) {
                    LerToken();
                    if ((token.getClasse() == Classe.cPalRes)
                            && (token.getValor().getIDValor().equalsIgnoreCase("do"))) {
                        LerToken();
                        if ((token.getClasse() == Classe.cPalRes)
                                && (token.getValor().getIDValor().equalsIgnoreCase("begin"))) {
                            LerToken();
                            Sentencas();
                            if ((token.getClasse() == Classe.cPalRes)
                                    && (token.getValor().getIDValor().equalsIgnoreCase("end"))) {
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
            LerToken();
            if (token.getClasse() == Classe.cParLeft) {
                LerToken();
                Condicao();
                if (token.getClasse() == Classe.cParRight) {
                    LerToken();
                    if ((token.getClasse() == Classe.cPalRes)
                            && (token.getValor().getIDValor().equalsIgnoreCase("then"))) {
                        LerToken();
                        if ((token.getClasse() == Classe.cPalRes)
                                && (token.getValor().getIDValor().equalsIgnoreCase("begin"))) {
                            LerToken();
                            Sentencas();
                            if ((token.getClasse() == Classe.cPalRes)
                                    && (token.getValor().getIDValor().equalsIgnoreCase("end"))) {
                                LerToken();
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
            LerToken();
            if (token.getClasse() == Classe.cAssignment) {
                LerToken();
                Expressao();
            } else {
                ErrorNotify("Não foi identificada nenhuma atribuição");
            }
        }
    }

    public void Condicao() {
        Expressao();
        Relacao();
        Expressao();
    }

    public void pfalsa() {
        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("else"))) {
            LerToken();
            if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("begin"))) {
                LerToken();
                Sentencas();
                if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getIDValor().equalsIgnoreCase("end"))) {
                    LerToken();
                } else {
                    ErrorNotify("Não foi finalizado com 'END'");
                }
            } else {
                ErrorNotify("Não foi inicializado com com 'BEGIN'");
            }
        }
    }

    public void Relacao() {
        if (token.getClasse() == Classe.cEquals) {
            LerToken();
        } else if (token.getClasse() == Classe.cBigger) {
            LerToken();
        } else if (token.getClasse() == Classe.cLess) {
            LerToken();
        } else if (token.getClasse() == Classe.cBiggerEquals) {
            LerToken();
        } else if (token.getClasse() == Classe.cLessEquals) {
            LerToken();
        } else if (token.getClasse() == Classe.cDiferente) {
            LerToken();
        } else {
            ErrorNotify("Não foi identificado o operador de relação");
        }
    }

    public void Expressao() {
        Termo();
        DemaisTermos();
    }

    public void DemaisTermos() {
        if (token.getClasse() == Classe.cPlus || token.getClasse() == Classe.cMinus) {
            PlusOrMinusOperation();
            Termo();
            DemaisTermos();
        }
    }

    public void PlusOrMinusOperation() {
        if (token.getClasse() == Classe.cPlus || token.getClasse() == Classe.cMinus) {
            LerToken();
        } else {
            ErrorNotify("Não foi identificado o operador de adição ou subtração");
        }
    }

    public void Termo() {
        Factors();
        MoreFactors();
    }

    public void MoreFactors() {
        if (token.getClasse() == Classe.cMultiply || token.getClasse() == Classe.cDivide) {
            MultiplyOrDivideOperation();
            Factors();
            MoreFactors();
        }
    }

    public void MultiplyOrDivideOperation() {
        if (token.getClasse() == Classe.cMultiply || token.getClasse() == Classe.cDivide) {
            LerToken();
        } else {
            ErrorNotify("Não foi identificado o operador de multiplicação ou divisão");
        }
    }

    public void Factors() {
        if (token.getClasse() == Classe.cId) {
            LerToken();
        } else if (token.getClasse() == Classe.cInt || token.getClasse() == Classe.cReal) {
            LerToken();
        } else if (token.getClasse() == Classe.cParLeft) {
            LerToken();
            Expressao();
            if (token.getClasse() == Classe.cParRight) {
                LerToken();
            } else {
                ErrorNotify("Não foi identificado o parênteses direito (')')");
            }
        } else {
            ErrorNotify("Não foi identificado o Factors 'IN NUM EXP'");
        }
    }

}
