package pt.ulusofona.aed.rockindeisi2023;

public class EstatisticasArquivo {

    String nomeFicheiro;
    int linhasOK;
    int linhasNOK;
    int primeiraLinhaNOK;

    public EstatisticasArquivo(String nomeFicheiro, int linhasOK, int linhasNOK, int primeiraLinhaNOK) {
        this.nomeFicheiro = nomeFicheiro;
        this.linhasOK = linhasOK;
        this.linhasNOK = linhasNOK;
        this.primeiraLinhaNOK = primeiraLinhaNOK;
    }
    public EstatisticasArquivo() {

    }

    @Override
    public String toString() {
        return nomeFicheiro+" | "+linhasOK+" | "+linhasNOK+" | "+primeiraLinhaNOK;
    }
}
