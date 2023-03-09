package paulo.example.classes;

public class Valor {
    private int valorIntteiro;
    private double valorDecimal;
    private String valorIdentificador;


    public int getValorIntteiro() {
        return valorIntteiro;
    }

    public void setValorIntteiro(int valorIntteiro) {
        this.valorIntteiro = valorIntteiro;
    }

    public double getValorDecimal() {
        return valorDecimal;
    }

    public void setValorDecimal(double valorDecimal) {
        this.valorDecimal = valorDecimal;
    }

    public String getValorIdentificador() {
        return valorIdentificador;
    }

    public void setValorIdentificador(String valorIdentificador) {
        this.valorIdentificador = valorIdentificador;
    }

    @Override
    public String toString() {
        return "Valor [valorIntteiro=" + valorIntteiro + ", valorDecimal=" + valorDecimal + ", valorIdentificador="
                + valorIdentificador + "]";
    }
}
