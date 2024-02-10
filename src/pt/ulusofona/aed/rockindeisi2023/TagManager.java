package pt.ulusofona.aed.rockindeisi2023;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagManager {
    public Map<String, List<String>> artistTags;

    public TagManager(Map<String, List<String>> artistTags) {
        this.artistTags = artistTags;
    }

    public void associateTags(String artist, List<String> tags) {
        // Verificar se o artista existe
        if (!artistTags.containsKey(artist)) {
            System.out.println("Inexistent artist");
            return;
        }

        List<String> existingTags = artistTags.get(artist);

        // Adicionar apenas as tags não duplicadas
        for (String tag : tags) {
            if (!existingTags.contains(tag.toUpperCase())) {
                existingTags.add(tag.toUpperCase());
            }
        }

        // Imprimir a linha formatada com as tags do artista
        System.out.print(artist + " | ");
        for (int i = 0; i < existingTags.size(); i++) {
            System.out.print(existingTags.get(i));
            if (i < existingTags.size() - 1) {
                System.out.print(",");
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        // Crie um HashMap com todos os artistas e suas tags
        Map<String, List<String>> artistTagsMap = new HashMap<>();

        // Adicione artistas e suas tags ao HashMap
        List<String> artist1Tags = new ArrayList<>();
        artist1Tags.add("rock");
        artist1Tags.add("pop");
        artist1Tags.add("jazz");
        artistTagsMap.put("Artista1", artist1Tags);

        List<String> artist2Tags = new ArrayList<>();
        artist2Tags.add("folk");
        artist2Tags.add("blues");
        artistTagsMap.put("Artista2", artist2Tags);

        List<String> artist3Tags = new ArrayList<>();
        artist3Tags.add("hip-hop");
        artist3Tags.add("rap");
        artistTagsMap.put("Artista3", artist3Tags);

    }
}
