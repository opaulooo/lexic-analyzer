package paulo.example.classes;

import java.text.DecimalFormat;

public class Valor {
    private int valorINT;
    private double valorDEC;
    private String idValor;
    private TipoValor tipo;

    private enum TipoValor {
        INT, DEC, ID
    }

    public Valor() {
    }

    public Valor(int valorINT) {
        this.valorINT = valorINT;
        tipo = TipoValor.INT;
    }

    public Valor(String idValor) {
        this.idValor = idValor;
        tipo = TipoValor.ID;
    }

    public Valor(double valorDEC) {
        this.valorDEC = valorDEC;
        tipo = TipoValor.DEC;
    }

    public int getValorINT() {
        return valorINT;
    }

    public void setValorINT(int valorINT) {
        this.valorINT = valorINT;
        tipo = TipoValor.INT;
    }

    public double getValorDEC() {
        return valorDEC;
    }

    public void setValorDEC(double valorDEC) {
        this.valorDEC = valorDEC;
        tipo = TipoValor.DEC;
    }

    public String getIDValor() {
        return idValor;
    }

    public void setIDValor(String idValor) {
        this.idValor = idValor;
        tipo = TipoValor.ID;
    }

    @Override
    public String toString() {
        if (tipo == TipoValor.INT) {
            return "\n\tInteiro: " + valorINT;
        } else if (tipo == TipoValor.DEC) {
            DecimalFormat df = new DecimalFormat("#.######");
            return "\n\tDecimal: " + df.format(valorDEC);
        } else {
            return "\n\tId: " + idValor;
        }
    }
}
