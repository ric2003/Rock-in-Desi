package pt.ulusofona.aed.rockindeisi2023;
import java.util.List;

public class Songs {
    String id;
    String name;
    int year;
    List<String> artistas;
    int duracao;
    int explicita;
    int popularidade;
    double dancabilidade;
    float vivacidade;
    float volumeM;

    public Songs(String id, String name, int year) {
        this.id = id;
        this.name = name;
        this.year = year;

    }

    public Songs(String id) {
        this.id = id;
    }

    public Songs(String id, List<String> artistas) {
        this.id = id;
        this.artistas = artistas ;

    }

    public List<String> getArtistas() {
        return artistas;
    }

    public Songs() {}

    @Override //string builder is 10ms faster
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(" | ").append(name).append(" | ").append(year);

        if (year < 1995) {
            return sb.toString();
        }

        int duracao_secs = duracao / 1000;
        String duracao_MinSec = (duracao_secs / 60) + ":" + String.format("%02d", duracao_secs % 60);
        sb.append(" | ").append(duracao_MinSec).append(" | ").append(popularidade);

        if (year >= 2000) {
            List<String> artistList = artistas;
            int numArtistas = 0;

            for (int j = 0; j < artistList.size(); j++) {
                String currentArtist = artistList.get(j);
                if (!artistList.subList(0, j).contains(currentArtist)) {
                    numArtistas++;
                }
            }
            sb.append(" | ").append(numArtistas);
        }

        return sb.toString();
    }

}
/*@Override
    public String toString() {
        if(year< 1995){
            return id + " | " + name + " | " + year;
        }else if(year < 2000){
            int duracao_secs = duracao/1000;
            String duracao_MinSec = (duracao_secs / 60) + ":" + String.format("%02d", duracao_secs % 60);
            return id + " | " + name + " | " + year +  " | " + duracao_MinSec + " | " + popularidade;
        }else if(year >= 2000){
            int duracao_secs = duracao/1000;
            String duracao_MinSec = (duracao_secs / 60) + ":" + String.format("%02d", duracao_secs % 60);

            List<String> artistList = artistas;
            int numArtistas = 0;

            for (int j = 0; j < artistList.size(); j++) {
                String currentArtist = artistList.get(j);
                if (!artistList.subList(0, j).contains(currentArtist)) {
                    numArtistas++;
                }
            }
            return id + " | " + name + " | " + year +  " | " + duracao_MinSec + " | " + popularidade +" | "+numArtistas;
        }
        return null;
    }*/