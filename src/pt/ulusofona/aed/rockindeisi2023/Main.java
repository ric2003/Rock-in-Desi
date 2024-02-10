package pt.ulusofona.aed.rockindeisi2023;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Comparator;
import static java.lang.System.currentTimeMillis;


public class Main {
    static int LinhasOK_songs, LinhasOK_SongArtist, LinhasOK_SongDetails,
            LinhasNOK_songs, LinhasNOK_SongArtist, LinhasNOK_SongDetails,
            PLinhaNOK_songs, PLinhaNOK_SongArtist, PLinhaNOK_SongDetails;
    static ArrayList<String> namesArtist;
    static ArrayList<String> songsArrayList = new ArrayList<>(200000);
    static HashMap<String, Songs> songsMap = new HashMap<>(200000);
    static ArrayList<Songs> matchedSongsArrayList = new ArrayList<>(200000);
    static HashMap<String, TagsArtista> tagsHashmap = new HashMap<>();
    //static ArrayList<String> artistTags = new ArrayList<>();
    static HashMap<String, Integer> nameCount = new HashMap<>();
    static HashMap<String, Artist> artistMap = new HashMap<>();

    // \1/ Essa função recebe uma string como entrada e retorna um ArrayList de Strings contendo os nomes de artistas encontrados dentro dessa string, considerando a presença de nomes entre aspas ("") ou plicas ('').
    public static ArrayList<String> parseMultipleArtists(String str) {
        ArrayList<String> result = new ArrayList<>();

        String cleanInput = str.trim().substring(1, str.length() - 1); //aqui é removido qualquer tipo de espaços extra no inicio e final da string
        Pattern pattern = Pattern.compile("\"\"(.*?)\"\"|'(.*?)'"); //aqui é filtrado as aspas e plicas que pertencem ao nome do artista

        Matcher matcher = pattern.matcher(cleanInput);
        while (matcher.find()) {
            String match = matcher.group(1) != null ? matcher.group(1) : matcher.group(2); //group 1 é as aspas, group 2 é as  plicas
            result.add(match);
        }
        return result;
    }

    // \2/ esta função vai verificar se foi possivel dar load no ficheiro Songs.txt e se tem algum problema nas linhas do ficheiro
    public static boolean loadSongs(String fileName) {
        int lineNumber = 0;
        LinhasOK_songs = 0;
        LinhasNOK_songs = 0;
        PLinhaNOK_songs = -1;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.isEmpty()) {
                    String[] parts = line.split("@");

                    if (parts.length == 3) {
                        String id = parts[0].trim();
                        String name = parts[1].trim();
                        int year = Integer.parseInt(parts[2].trim());

                        if (!(songsMap.containsKey(id.trim()))) {
                            songsMap.put(id, new Songs(id, name, year));
                            songsArrayList.add(id);
                            LinhasOK_songs++;
                        } else {
                            LinhasNOK_songs++;
                            if (LinhasNOK_songs == 1) {
                                PLinhaNOK_songs = lineNumber;
                            }
                        }
                    } else {
                        LinhasNOK_songs++;
                        if (LinhasNOK_songs == 1) {
                            PLinhaNOK_songs = lineNumber;
                        }
                    }
                } else {
                    LinhasNOK_songs++;
                    if (LinhasNOK_songs == 1) {
                        PLinhaNOK_songs = lineNumber;
                    }
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            return false;
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return false;
        }


    }

    // \3/ esta função vai verificar se foi possivel dar load no ficheiro SongArtists.txt e se tem algum problema nas linhas do ficheiro e tambem dar add dos artistas a um array list com o nome de todos eles
    public static boolean loadSongArtists(String fileName) {
        namesArtist = new ArrayList<>();
        int lineNumber = 0;
        LinhasOK_SongArtist = 0;
        LinhasNOK_SongArtist = 0;
        PLinhaNOK_SongArtist = -1;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (!line.isEmpty()) {
                    try {
                        String[] parts = line.split("@");
                        if (parts.length == 2) {
                            String songId = parts[0].trim();
                            String artistsStr = parts[1].trim();
                            Boolean ta_mal_ta_errado = false;
                            if (artistsStr.startsWith("[") && artistsStr.endsWith("]")) {
                                if (artistsStr.split(",").length > 1) {
                                    ta_mal_ta_errado = true;
                                }
                            }
                            if (!ta_mal_ta_errado && ((artistsStr.startsWith("[") && artistsStr.endsWith("]")) || (artistsStr.startsWith("\"") && artistsStr.endsWith("\"")))) {
                                ArrayList<String> artists = parseMultipleArtists(artistsStr);

                                if (songsMap.containsKey(songId.trim())) {
                                    LinhasOK_SongArtist++;
                                    Songs existingSong = songsMap.get(songId);
                                    if (existingSong.artistas == null) {
                                        for (int i = 0; i < artists.size(); i++) {
                                            String artist = artists.get(i);
                                            if (!artistMap.containsKey(artist)) {
                                                artistMap.put(artist, new Artist(artist));
                                            }
                                            artist = "[" + artist + "]";
                                            artists.set(i, artist);
                                        }
                                        existingSong.artistas = artists;
                                         // Increment OK count
                                        //165,627
                                    }
                                } else {
                                    LinhasNOK_SongArtist++; // Increment NOK count
                                    if (LinhasNOK_SongArtist == 1) {
                                        PLinhaNOK_SongArtist = lineNumber;
                                    }
                                }
                            } else {
                                LinhasNOK_SongArtist++; // Increment NOK count
                                if (LinhasNOK_SongArtist == 1) {
                                    PLinhaNOK_SongArtist = lineNumber;
                                }
                            }
                        } else {
                            LinhasNOK_SongArtist++; // Increment NOK count
                            if (LinhasNOK_SongArtist == 1) {
                                PLinhaNOK_SongArtist = lineNumber;
                            }
                        }
                    } catch (Exception e) {
                        LinhasNOK_SongArtist++; // Increment NOK count
                        if (LinhasNOK_SongArtist == 1) {
                            PLinhaNOK_SongArtist = lineNumber;
                        }
                        System.out.println("Error parsing line " + lineNumber + ": " + line);
                    }
                } else {
                    LinhasNOK_SongArtist++; // Increment NOK count
                    if (LinhasNOK_SongArtist == 1) {
                        PLinhaNOK_SongArtist = lineNumber;
                    }
                }
            }
            for (Songs song : songsMap.values()) {
                if (song != null && song.artistas != null) {
                    for (String artist : song.artistas) {
                        namesArtist.add(artist);
                    }
                }
            }
            for (String name : namesArtist) {
                nameCount.put(name, nameCount.getOrDefault(name, 0) + 1);
            }
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("File doesn't exist");
            return false;
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return false;
        }
    }

    // \4/ esta função vai verificar se foi possivel dar load no ficheiro SongDetails.txt e se tem algum problema nas linhas do ficheiro
    public static boolean loadSongDetails(String fileName) {
        int lineNumber = 0;
        LinhasOK_SongDetails = 0;
        LinhasNOK_SongDetails = 0;
        PLinhaNOK_SongDetails = -1;


        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("@");

                    if (parts.length == 7) {
                        String songId = parts[0].trim();
                        int duracao = Integer.parseInt(parts[1].trim());
                        int explicita = Integer.parseInt(parts[2].trim());
                        int popularidade = Integer.parseInt(parts[3].trim());
                        double dancabilidade = Double.parseDouble(parts[4].trim());
                        float vivacidade = Float.parseFloat(parts[5].trim());
                        float volumeM = Float.parseFloat(parts[6].trim());

                        LinhasOK_SongDetails++;
                        if (songsMap.containsKey(songId.trim())) {
                            Songs existingSong = songsMap.get(songId);
                            //174,354
                            if (existingSong.duracao == 0) {
                                existingSong.duracao = duracao;
                                existingSong.explicita = explicita;
                                existingSong.popularidade = popularidade;
                                existingSong.dancabilidade = dancabilidade;
                                existingSong.vivacidade = vivacidade;
                                existingSong.volumeM = volumeM;
                            }
                        }
                    } else {
                        LinhasNOK_SongDetails++;
                        if (LinhasNOK_SongDetails == 1) {
                            PLinhaNOK_SongDetails = lineNumber;
                        }
                    }
                } else {
                    LinhasNOK_SongDetails++;
                    if (LinhasNOK_SongDetails == 1) {
                        PLinhaNOK_SongDetails = lineNumber;
                    }
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    static long durationMillis;
    static int count = 0;

    // \5/ esta função adiciona toda a informaçao das musicas num só arraylist
    public static void toArraylistFromHashmap() {
        // Iterate over the ID ArrayList
        for (String id : songsArrayList) {
            // Check if the ID exists in the HashMap
            if (songsMap.containsKey(id)) {
                // Get the song object from the HashMap based on the ID
                Songs song = songsMap.get(id);
                // Add the song object to the matchedSongsArrayList
                if (song.duracao != 0 && song.artistas != null) {
                    matchedSongsArrayList.add(song);
                }
            }
        }

    }

    // \6/ esta função vai verificar se foi possivel dar load nos 3 ficheiros
    public static boolean loadFiles(File folder) {
        songsArrayList.clear();
        songsMap.clear();
        nameCount.clear();
        matchedSongsArrayList.clear();
        artistMap.clear();

        File file1 = new File(folder, "songs.txt");
        File file2 = new File(folder, "song_artists.txt");
        File file3 = new File(folder, "song_details.txt");

        if (!(file1.exists() && file2.exists() && file3.exists())) {
            return false;
        }

        boolean vailido = loadSongs(file1.getPath()) && loadSongArtists(file2.getPath()) &&
                loadSongDetails(file3.getPath());

        toArraylistFromHashmap();
        return vailido;
    }

    // \7/ esta função retorna uma string com as seguintes informaçoes do ficheiro em questao nomeFicheiro | linhasOK | linhasNOK | primeiraLinhaNOK
    public static EstatisticasArquivo calcEstatisticasArquivo(String filename) {
        switch (filename) {
            case "songs.txt":
                return new EstatisticasArquivo("songs.txt", LinhasOK_songs, LinhasNOK_songs, PLinhaNOK_songs);
            case "song_artists.txt":
                return new EstatisticasArquivo("song_artists.txt", LinhasOK_SongArtist, LinhasNOK_SongArtist, PLinhaNOK_SongArtist);
            case "song_details.txt":
                return new EstatisticasArquivo("song_details.txt", LinhasOK_SongDetails, LinhasNOK_SongDetails, PLinhaNOK_SongDetails);
            default:
                return null;
        }
    }

    // \8/ esta função conta o numero de musicas que um artista tem/participou
    public static int musicas_colaborado(String nameToFInd) {

        int count = nameCount.getOrDefault(nameToFInd, 0);
        /*int count1 = 0;
        for (String name : namesArtist) {
            if (name.equals(nameToFInd)) {
                count++;
            }
        }*/
        return count;
    }

    // \9/ esta funçao vai retornar um arraylist com os objetos de um tipo em especifico mantendo a ordem no ficheiro original
    public static ArrayList getObjects(TipoEntidade tipo) {

        ArrayList finalList = new ArrayList();
        switch (tipo) {
            case TEMA:
                for (Songs song : matchedSongsArrayList) {
                    finalList.add(song.toString());
                }
                return finalList;
            case ARTISTA:
                if (matchedSongsArrayList != null && matchedSongsArrayList.size() > 0) {
                    for (int i = 0; i < matchedSongsArrayList.size(); i++) {
                        for (int j = 0; j < matchedSongsArrayList.get(i).artistas.size(); j++) {
                            String artista_individual = matchedSongsArrayList.get(i).artistas.get(j);
                            if (artista_individual.charAt(1) == 'A' || artista_individual.charAt(1) == 'B'
                                    || artista_individual.charAt(1) == 'C' || artista_individual.charAt(1) == 'D') {
                                if (!finalList.contains("Artista: " + artista_individual)) {
                                    finalList.add("Artista: " + artista_individual);
                                }
                            } else {
                                if (!finalList.contains("Artista: " + artista_individual + " | "
                                        + musicas_colaborado(artista_individual))) {
                                    finalList.add("Artista: " + artista_individual + " | "
                                            + musicas_colaborado(artista_individual));
                                }
                            }
                        }
                    }
                }
                return finalList;

            case INPUT_INVALIDO:

                ArrayList<EstatisticasArquivo> estatisticasList = new ArrayList<EstatisticasArquivo>();
                estatisticasList.add(calcEstatisticasArquivo("songs.txt"));
                estatisticasList.add(calcEstatisticasArquivo("song_details.txt"));
                estatisticasList.add(calcEstatisticasArquivo("song_artists.txt"));

                return estatisticasList;
            default:
                return null;

        }
    }

    // \10/ esta função vai analisar um comando em especifico e fazer com que o mesmo aceite um numero x de argumentos
    public static Query parseCommand(String command) {
        String[] parts = command.split(" ");
        String firstPart = parts[0];
        String argStr = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

        HashSet<String> validCommands = new HashSet<>(Arrays.asList(
                "EXIT", "COUNT_SONGS_YEAR", "GET_SONGS_BY_ARTIST", "GET_MOST_DANCEABLE", "ADD_TAGS",
                "REMOVE_TAGS", "GET_ARTISTS_FOR_TAG", "GET_ARTIST_RELEASE_IN_YEAR", "COUNT_DUPLICATE_SONGS_YEAR",
                "GET_ARTISTS_ONE_SONG", "GET_TOP_ARTISTS_WITH_SONGS_BETWEEN", "MOST_FREQUENT_WORDS_IN_ARTIST_NAME",
                "GET_UNIQUE_TAGS", "GET_UNIQUE_TAGS_IN_BETWEEN_YEARS", "GET_RISING_STARS", "GET_ARTISTS_WITH_MIN_DURATION",
                "GET_SONG_TITLES_CONSIDERING_WORDS"
        ));

        if (firstPart.equals("EXIT")) {
            return null;
        }

        String[] args;
        if (validCommands.contains(firstPart)) {
            switch (firstPart) {
                case "ADD_TAGS":
                case "REMOVE_TAGS":
                    args = argStr.split(";");
                    break;
                case "GET_MOST_DANCEABLE":
                case "GET_TOP_ARTISTS_WITH_SONGS_BETWEEN":
                case "MOST_FREQUENT_WORDS_IN_ARTIST_NAME":
                case "GET_RISING_STARS":
                case "GET_SONG_TITLES_CONSIDERING_WORDS":
                    args = argStr.split(" ", 3);
                    break;
                case "GET_SONGS_BY_ARTIST":
                case "GET_UNIQUE_TAGS_IN_BETWEEN_YEARS":
                case "GET_ARTISTS_WITH_MIN_DURATION":
                case "GET_ARTISTS_ONE_SONG":
                    args = argStr.split(" ", 2);
                    break;
                case "GET_UNIQUE_TAGS":
                    args = new String[0];
                    break;
                default:
                    args = argStr.split(" ", 1);
                    break;
            }

            return new Query(firstPart, args);
        }

        return null;
    }

    // \11/ esta função irá executar a query em questao e retornar o seu output
    static QueryResult execute(String command) {
        QueryResult resultQuery;
        Query queryToExec = parseCommand(command);

        if (queryToExec == null || queryToExec.name == null) {
            //System.out.println("Invalid command. Try again");
            return null;
        }

        String[] args = queryToExec.args;
        String arg1 = args.length > 0 ? args[0] : "";
        String arg2 = args.length > 1 ? args[1] : "";
        String arg3 = args.length > 2 ? args[2] : "";

        Map<String, Function<Query, QueryResult>> operationMap = new HashMap<>();
        operationMap.put("EXIT", query -> null);
        operationMap.put("COUNT_SONGS_YEAR", query -> Main.count_songs_year(Integer.parseInt(arg1)));
        operationMap.put("COUNT_DUPLICATE_SONGS_YEAR", query -> Main.count_duplicate_songs_year(Integer.parseInt(arg1)));
        operationMap.put("GET_SONGS_BY_ARTIST", query -> Main.get_songs_by_artist(Integer.parseInt(arg1), arg2));
        operationMap.put("GET_MOST_DANCEABLE", query -> Main.get_most_danceable(Integer.parseInt(arg1), Integer.parseInt(arg2), Integer.parseInt(arg3)));
        operationMap.put("GET_ARTISTS_ONE_SONG", query ->
                Main.get_artist_one_song(Integer.parseInt(arg1), Integer.parseInt(arg2)));
        operationMap.put("GET_TOP_ARTISTS_WITH_SONGS_BETWEEN", query -> Main.get_top_artists_with_songs_between(Integer.parseInt(arg1), Integer.parseInt(arg2), Integer.parseInt(arg3)));
        operationMap.put("MOST_FREQUENT_WORDS_IN_ARTIST_NAME", query ->
                Main.get_most_frequent_words_in_artist_name(Integer.parseInt(arg1), Integer.parseInt(arg2), Integer.parseInt(arg3)));
        operationMap.put("GET_UNIQUE_TAGS", query -> Main.getUniqueTags());
        operationMap.put("GET_UNIQUE_TAGS_IN_BETWEEN_YEARS", query -> Main.getUniqueTagsInBetweenYears(Integer.parseInt(arg1), Integer.parseInt(arg2)));
        operationMap.put("GET_RISING_STARS", query -> Main.getRisingStars(Integer.parseInt(arg1), Integer.parseInt(arg2), arg3));
        operationMap.put("ADD_TAGS", query -> Main.associateTags(arg1, List.of(Arrays.copyOfRange(args, 1, args.length))));
        operationMap.put("REMOVE_TAGS", query -> Main.removeTags(arg1, List.of(Arrays.copyOfRange(args, 1, args.length))));
        operationMap.put("GET_ARTISTS_FOR_TAG", query -> Main.getArtistsByTag(arg1));
        operationMap.put("GET_ARTISTS_WITH_MIN_DURATION", query -> Main.getArtistsWithMinDuration(Integer.parseInt(arg1), Integer.parseInt(arg2)));
        operationMap.put("GET_SONG_TITLES_CONSIDERING_WORDS", query -> Main.getSongTitlesConsideringWords(Integer.parseInt(arg1), arg2, arg3));
        Function<Query, QueryResult> operation = operationMap.getOrDefault(queryToExec.name, query -> {
            System.out.println("Illegal command. Try again");
            return null;
        });
        resultQuery = operation.apply(queryToExec);

        return resultQuery;
    }


    // \12/ query obrigatória
    public static QueryResult count_songs_year(int ano) {
        long startTime = System.currentTimeMillis();
        int count = 0;//todo check later if i lose points
        for (int i = 0; i < matchedSongsArrayList.size(); i++) {
            if (matchedSongsArrayList.get(i).year == ano) {
                count++;
            }
        }
        String s = String.valueOf(count);
        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        return new QueryResult(s, durationMillis);
    }

    // \13/ query obrigatória
    public static QueryResult get_songs_by_artist(int num_resultados, String artista) {
        long startTime = System.currentTimeMillis();
        int count = 0;
        StringBuilder musicas = new StringBuilder();

        for (int i = 0; i < matchedSongsArrayList.size() && count < num_resultados; i++) {
            if (matchedSongsArrayList.get(i).artistas.contains("[" + artista + "]")) {
                musicas.append(matchedSongsArrayList.get(i).name)
                        .append(" : ")
                        .append(matchedSongsArrayList.get(i).year)
                        .append("\n");
                count++;
            }
        }
        if (count == 0) {
            musicas.append("No songs");
        }
        String s = musicas.toString();
        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        return new QueryResult(s, durationMillis);
    }

    // \14/ query obrigatória
    public static QueryResult get_most_danceable(int ano_inicio, int ano_fim, int num_resultados) {
        long startTime = System.currentTimeMillis();
        int count = 0;
        ArrayList<Songs> musicas_entre_anos = new ArrayList<>();
        StringBuilder musicas = new StringBuilder();

        for (int i = 0; i < matchedSongsArrayList.size(); i++) {
            if (matchedSongsArrayList.get(i).year >= ano_inicio && matchedSongsArrayList.get(i).year <= ano_fim) {
                musicas_entre_anos.add(matchedSongsArrayList.get(i));
            }
        }
        Collections.sort(musicas_entre_anos, new Comparator<Songs>() {
            @Override
            public int compare(Songs song1, Songs song2) {
                return Double.compare(song2.dancabilidade, song1.dancabilidade);
            }
        });

        for (int i = 0; i < num_resultados && i < musicas_entre_anos.size(); i++) {
            if(count!=0){
                musicas.append("\n");
            }
            musicas.append(musicas_entre_anos.get(i).name)
                    .append(" : ")
                    .append(musicas_entre_anos.get(i).year)
                    .append(" : ")
                    .append(String.valueOf(musicas_entre_anos.get(i).dancabilidade));
            count++;

        }
        String s = musicas.toString();
        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        return new QueryResult(s, durationMillis);
    }

    // \15/ query obrigatória
    public static QueryResult associateTags(String artist, List<String> tags) {
        long startTime = System.currentTimeMillis();
        // Verificar se o artista existe
        if (!artistMap.containsKey(artist)) {
            long endTime = System.currentTimeMillis();
            long durationMillis = endTime - startTime;
            return new QueryResult("Inexistent artist", durationMillis);
        }

        Artist artistObj = artistMap.get(artist);
        List<String> existingTags = artistObj.tags;
        List<String> uppercaseTags = new ArrayList<>();

        // Converter as tags existentes para letras maiúsculas e adicioná-las
        for (String existingTag : existingTags) {
            String uppercaseTag = existingTag.toUpperCase();
            if (!uppercaseTags.contains(uppercaseTag)) {
                uppercaseTags.add(uppercaseTag);
            }
        }

        // Adicionar apenas as tags não duplicadas
        for (String tag : tags) {
            String uppercaseTag = tag.toUpperCase();
            if (!uppercaseTags.contains(uppercaseTag)) {
                uppercaseTags.add(uppercaseTag);
                artistObj.tags.add(uppercaseTag); // Save new tag in all caps to artist
            }
        }

        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(artist).append(" | ");
        for (int i = 0; i < uppercaseTags.size(); i++) {
            resultBuilder.append(uppercaseTags.get(i));
            if (i < uppercaseTags.size() - 1) {
                resultBuilder.append(",");
            }
        }
        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        return new QueryResult(resultBuilder.toString(), durationMillis);
    }

    // \16/ query obrigatória
    public static QueryResult removeTags(String artist, List<String> tagsToRemove) {
        long startTime = System.currentTimeMillis();

        // Verificar se o artista existe
        if (!artistMap.containsKey(artist)) {
            long endTime = System.currentTimeMillis();
            long durationMillis = endTime - startTime;
            return new QueryResult("Inexistent artist", durationMillis);
        }

        Artist artistObj = artistMap.get(artist);
        List<String> existingTags = artistObj.tags;
        List<String> uppercaseTags = new ArrayList<>();

        // Converter as tags existentes para letras maiúsculas e adicioná-las
        for (String existingTag : existingTags) {
            String uppercaseTag = existingTag.toUpperCase();
            uppercaseTags.add(uppercaseTag);
        }

        List<String> tagsToRemoveUpper = new ArrayList<>();
        for (String tagToRemove : tagsToRemove) {
            String uppercaseTag = tagToRemove.toUpperCase().trim();
            tagsToRemoveUpper.add(uppercaseTag);
        }

        Iterator<String> iterator = existingTags.iterator();
        while (iterator.hasNext()) {
            String tag = iterator.next();
            String uppercaseTag = tag.toUpperCase();
            if (tagsToRemoveUpper.contains(uppercaseTag)) {
                iterator.remove();
            }
        }

        // Atualiza a lista de tags em maiúsculas
        List<String> updatedUppercaseTags = new ArrayList<>();
        for (String tag : existingTags) {
            updatedUppercaseTags.add(tag.toUpperCase());
        }

        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(artist).append(" | ");
        if (updatedUppercaseTags.isEmpty()) {
            resultBuilder.append("No tags");
        } else {
            for (int i = 0; i < updatedUppercaseTags.size(); i++) {
                resultBuilder.append(updatedUppercaseTags.get(i));
                if (i < updatedUppercaseTags.size() - 1) {
                    resultBuilder.append(",");
                }
            }
        }
        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        return new QueryResult(resultBuilder.toString(), durationMillis);
    }

    // \17/ query obrigatória
    public static QueryResult getArtistsByTag(String tag) {
        long startTime = System.currentTimeMillis();
        List<String> matchingArtists = new ArrayList<>();
        String lowercaseTag = tag.toLowerCase();

        for (Artist artist : artistMap.values()) {
            List<String> artistTags = artist.tags;
            for (String artistTag : artistTags) {
                if (artistTag.toLowerCase().equals(lowercaseTag)) {
                    matchingArtists.add(artist.name);
                    break;
                }
            }
        }
        Collections.sort(matchingArtists);

        if (matchingArtists.isEmpty()) {
            long endTime = System.currentTimeMillis();
            long durationMillis = endTime - startTime;
            return new QueryResult("No results", durationMillis);
        } else {
            long endTime = System.currentTimeMillis();
            long durationMillis = endTime - startTime;
            String result = String.join(";", matchingArtists);

            return new QueryResult(result, durationMillis);
        }
    }

    // \18/ query obrigatória
    public static QueryResult getArtistsWithMinDuration(int year, int duration) {
        StringBuilder sb = new StringBuilder();
        long startTime = System.currentTimeMillis();
        int count = 0;

        // Iterate over the songs and check the year and duration
        for (Songs song : matchedSongsArrayList) {
            if (song.year == year && song.duracao > duration * 1000) {
                List<String> artists = song.artistas;
                // Append each artist's name, song name, and duration to the StringBuilder
                for (String artist : artists) {
                    String modifiedString = artist.substring(1, artist.length() - 1);
                    if (count != 0) {
                        sb.append("\n");
                    }
                    sb.append(modifiedString).append(" | ").append(song.name).append(" | ").append(song.duracao);
                    count++;
                }
            }
        }

        // If no results are found, return "No artists"
        if (sb.length() == 0) {
            long durationMillis = System.currentTimeMillis() - startTime;
            return new QueryResult("No artists", durationMillis);

        }

        // Return the formatted string with multiple lines
        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        return new QueryResult(sb.toString(), durationMillis);
    }

    // \19/ query obrigatória
    public static QueryResult getSongTitlesConsideringWords(int numberOfResults, String include, String exclude) {
        StringBuilder sb = new StringBuilder();
        long startTime = System.currentTimeMillis();
        int count = 0; // Counter for the number of matching results

        // Iterate over the songs and check the title for inclusion and exclusion words
        for (Songs song : matchedSongsArrayList) {
            String title = song.name;

            // Case-insensitive comparison of inclusion and exclusion words as complete words
            if (isWordIncluded(title, include) && !isWordIncluded(title, exclude)) {
                if (count != 0) {
                    sb.append("\n");
                }
                sb.append(title);
                count++;

                // Break the loop if the desired number of results is reached
                if (count == numberOfResults) {
                    break;
                }
            }
        }

        // If no results are found, return "No results"
        if (sb.length() == 0) {
            long durationMillis = System.currentTimeMillis() - startTime;
            return new QueryResult("No results", durationMillis);
        }

        // Return the formatted string with multiple lines
        long durationMillis = System.currentTimeMillis() - startTime;
        return new QueryResult(sb.toString(), durationMillis);
    }

    // \20/ função para ver se uma palavra pertence a uma string
    public static boolean isWordIncluded(String title, String word) {
        String pattern = "\\b" + word + "\\b"; // Pattern to match the word as a complete word
        return title.toLowerCase().matches(".*" + pattern + ".*");
    }



    /*public static QueryResult get_artist_one_song(int ano_inicio, int ano_fim) {
        long startTime = System.currentTimeMillis();

        if (ano_inicio >= ano_fim) {
            long endTime = System.currentTimeMillis();
            long durationMillis = endTime - startTime;
            return new QueryResult("Invalid period", durationMillis);
        }
        ArrayList<Songs> musicas_entre_anos = new ArrayList<>();

        for (int i = 0; i < matchedSongsArrayList.size(); i++) {
            Songs musica = matchedSongsArrayList.get(i);
            if (musica.year >= ano_inicio && musica.year <= ano_fim) {
                musicas_entre_anos.add(musica);
            }
        }

        // Remove artistas com duas músicas ou mais
        for (int i = 0; i < musicas_entre_anos.size() - 1; i++) {
            Songs currentMusica = musicas_entre_anos.get(i);
            List<String> currentArtistas = currentMusica.getArtistas();
            for (int j = i + 1; j < musicas_entre_anos.size(); j++) {
                Songs nextMusica = musicas_entre_anos.get(j);
                List<String> nextArtistas = nextMusica.getArtistas();
                nextArtistas.removeIf(currentArtistas::contains); // Remove common artists
                if (nextArtistas.isEmpty()) {
                    musicas_entre_anos.remove(j);
                    j--; // Update the index after removal
                }
            }
        }

        // Constrói a string com os artistas que têm apenas uma música
        TreeSet<Songs> sortedSongs = new TreeSet<>((song1, song2) -> {
            String artist1 = song1.getArtistas().get(0);
            String artist2 = song2.getArtistas().get(0);
            return artist1.compareTo(artist2);
        });

        // Add the songs to the TreeSet
        sortedSongs.addAll(musicas_entre_anos);

        // Iterate through the sorted songs
        StringBuilder artistas = new StringBuilder();
        for (Songs musica : sortedSongs) {
            List<String> artistList = musica.getArtistas();
            for (String artist : artistList) {
                String artista = artist.substring(1, artist.length() - 1);
                artistas.append(artista)
                        .append(" | ")
                        .append(musica.name)
                        .append(" | ")
                        .append(musica.year)
                        .append("\n");
            }
        }

        String resultado = artistas.toString().trim();

        if (resultado.isEmpty()) {
            resultado = "No results";
        }

        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        return new QueryResult(resultado, durationMillis);
    }*/



    // \21/ query opcional
    public static QueryResult count_duplicate_songs_year(int ano) {
        long startTime = System.currentTimeMillis();
        Set<String> uniqueElements = new HashSet<>();
        Set<String> duplicateElements = new HashSet<>();
        int count = 0;

        for (int i = 0; i < matchedSongsArrayList.size(); i++) {
            if (matchedSongsArrayList.get(i).year == ano) {
                String element = matchedSongsArrayList.get(i).name.toLowerCase();
                if (uniqueElements.contains(element)) {
                    duplicateElements.add(element);
                } else {
                    uniqueElements.add(element);
                }
            }
        }

        count = duplicateElements.size();
        String s = String.valueOf(count);
        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        return new QueryResult(s, durationMillis);
    }

    // \22/ query opcional
    public static QueryResult get_artist_one_song(int ano_inicio, int ano_fim) {
        long startTime = System.currentTimeMillis();

        if (ano_inicio >= ano_fim) {
            long endTime = System.currentTimeMillis();
            long durationMillis = endTime - startTime;
            return new QueryResult("Invalid period", durationMillis);
        }

        ArrayList<Songs> musicas_entre_anos = new ArrayList<>();

        for (int i = 0; i < matchedSongsArrayList.size(); i++) {
            Songs musica = matchedSongsArrayList.get(i);
            if (musica.year >= ano_inicio && musica.year <= ano_fim) {
                musicas_entre_anos.add(musica);
            }
        }

        Map<String, Songs> stringCountMap = new HashMap<>();

        Map<String, Integer> artistCountMap = new HashMap<>(); // Track count of each artist

        for (int i = 0; i < musicas_entre_anos.size(); i++) {
            Songs musica = musicas_entre_anos.get(i);
            for (int j = 0; j < musica.artistas.size(); j++) {
                String artist = musica.artistas.get(j);

                // Update artist count
                int count = artistCountMap.getOrDefault(artist, 0);
                artistCountMap.put(artist, count + 1);

                if (count == 1) {
                    // If count is already 1, remove from the HashMap
                    stringCountMap.remove(artist);
                } else if (count == 0) {
                    // If count is 0, add the artist to the HashMap
                    stringCountMap.put(artist, musica);
                }
            }
        }


        // Iterate through the sorted songs
        List<String> artistasList = new ArrayList<>();
        for (Map.Entry<String, Songs> entry : stringCountMap.entrySet()) {
            String artist = entry.getKey();
            Songs musica = entry.getValue();
            String artista = artist.substring(1, artist.length() - 1);
            String result = artista + " | " + musica.name + " | " + musica.year;
            artistasList.add(result);
        }

// Sort the array alphabetically
        Collections.sort(artistasList);

// Create the result string with multiple lines
        StringBuilder artistas = new StringBuilder();
        for (String result : artistasList) {
            artistas.append(result).append("\n");
        }

// Remove the trailing "\n" at the end of the result
        if (artistas.length() > 0) {
            artistas.setLength(artistas.length() - 1);
        }

        String resultado = artistas.toString().trim();

        if (resultado.isEmpty()) {
            resultado = "No results";
        }

        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        return new QueryResult(resultado, durationMillis);
    }

    // \23/ query opcional
    public static QueryResult get_top_artists_with_songs_between(int num_resultados, int min, int max) {
        long startTime = System.currentTimeMillis();
        StringBuilder musicas = new StringBuilder();
        HashMap<String, Integer> topartistas = new HashMap<>();

        for (Songs song : matchedSongsArrayList) {
            List<String> artistas = song.getArtistas();
            for (String artista : artistas) {
                topartistas.put(artista, topartistas.getOrDefault(artista, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> sortedArtistas = new ArrayList<>(topartistas.entrySet());
        sortedArtistas.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedArtistas) {
            String artista = entry.getKey();
            int numTemas = entry.getValue();

            if (numTemas >= min && numTemas <= max) {
                artista = artista.substring(1, artista.length() - 1); // Remove the first and last character

                musicas.append(artista).append(" ").append(numTemas).append("\n");
                count++;

                if (count == num_resultados) {
                    break;
                }
            }
        }

        if (count == 0) {
            musicas.append("No results");
        }

        String resultado = musicas.toString();
        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        return new QueryResult(resultado, durationMillis);
    }

    // \24/ query opcional
    public static QueryResult get_most_frequent_words_in_artist_name(int N, int M, int L) {
        long startTime = currentTimeMillis();
        HashSet<String> popularArtists = new HashSet<>();
        ArrayList<String> frequentWordsToSort = new ArrayList<>();
        HashMap<String, Integer> frequentWords = new HashMap<>();
        StringBuilder result = new StringBuilder();
        HashMap<String, String> artistname = new HashMap<>();


        for(Songs song: matchedSongsArrayList){
            for(int i =0; i<song.artistas.size();i++){
                String str = song.artistas.get(i);//.replace("[", "").replace("]", "");
                if (!artistname.containsKey(str)) {
                    artistname.put(str,str);
                }
            }
        }

        for (String artist : artistname.values()) {
            if (musicas_colaborado(artist) >= M) {
                popularArtists.add(artist.replace("[", "").replace("]", ""));
            }
        }


        for (String name : popularArtists) {
            String[] parts = name.split(" ");

            for (String part : parts) {
                if (part.length() >= L) {
                    if (!frequentWords.containsKey(part)) {
                        frequentWords.put(part, 1);
                        frequentWordsToSort.add(part);
                    } else {
                        frequentWords.put(part, frequentWords.get(part) + 1);
                    }
                }
            }
        }

        Collections.sort(frequentWordsToSort, Comparator.comparingInt(frequentWords::get));

        if (frequentWordsToSort.size() < N) {
            for (String word : frequentWordsToSort) {
                result.append(word).append(" ").append(frequentWords.get(word)).append("\n");
            }
        } else {
            for (int i = frequentWordsToSort.size() - N; i < frequentWordsToSort.size(); i++) {
                result.append(frequentWordsToSort.get(i)).append(" ").append(frequentWords.get(frequentWordsToSort.get(i))).append("\n");
            }
        }
        result.deleteCharAt(result.length() - 1);
        long totalTime = currentTimeMillis() - startTime;
        return new QueryResult(result.toString(), totalTime);
    }


    // \25/ query opcional
    public static QueryResult getUniqueTags() {
        long startTime = System.currentTimeMillis();
        HashMap<String, Integer> tagOccurrences = new HashMap<>();

        // Contar as ocorrências de cada tag
        for (Artist artist : artistMap.values()) {
            List<String> artistTags = artist.tags;
            for (String tag : artistTags) {
                tagOccurrences.put(tag, tagOccurrences.getOrDefault(tag, 0) + 1);
            }
        }

        // Verificar se não há resultados
        if (tagOccurrences.isEmpty()) {
            long endTime = System.currentTimeMillis();
            long durationMillis = endTime - startTime;
            return new QueryResult("No results", durationMillis);
        }

        // Ordenar as tags por ordem crescente de ocorrências
        List<Map.Entry<String, Integer>> sortedTags = new ArrayList<>(tagOccurrences.entrySet());
        sortedTags.sort(Map.Entry.comparingByValue());

        // Construir a String de resultados
        StringBuilder resultBuilder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : sortedTags) {
            String tagName = entry.getKey();
            int occurrenceCount = entry.getValue();
            resultBuilder.append(tagName).append(" ").append(occurrenceCount).append("\n");
        }
        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        return new QueryResult(resultBuilder.toString(), durationMillis);

    }

    // \26/ query opcional
    public static QueryResult getUniqueTagsInBetweenYears(int startYear, int endYear) {
        long startTime = System.currentTimeMillis();
        HashMap<String, Integer> tagOccurrences = new HashMap<>();

        ArrayList<Songs> musicas_entre_anos = new ArrayList<>();

        for (int i = 0; i < matchedSongsArrayList.size(); i++) {
            Songs musica = matchedSongsArrayList.get(i);
            if (musica.year >= startYear && musica.year <= endYear) {
                musicas_entre_anos.add(musica);
            }
        }
        HashMap<String, Set<String>> tagArtists = new HashMap<>();
        ArrayList<String> sortedTags = new ArrayList<>();

        // Iterate over the songs and check the years
        for (Artist artist : artistMap.values()) {
            List<String> songTags = artist.tags;
            if (!songTags.isEmpty()) {
                for (Songs song : musicas_entre_anos) {
                    for (String artistName : song.artistas) {
                        String artistanome = artistName.substring(1, artistName.length() - 1);
                        if (Objects.equals(artistanome, artist.name)) {
                            for (String tag : songTags) {
                                String lowercaseTag = tag.toLowerCase();
                                // Check if the artist is already counted for this tag
                                if (!tagArtists.containsKey(lowercaseTag)) {
                                    tagArtists.put(lowercaseTag, new HashSet<>());
                                }
                                Set<String> artistsForTag = tagArtists.get(lowercaseTag);
                                // Add the artist to the set if not already present
                                if (!artistsForTag.contains(artist.name)) {
                                    artistsForTag.add(artist.name);
                                    if(!tagOccurrences.containsKey(tag)) {
                                        sortedTags.add(tag);
                                    }
                                    tagOccurrences.put(tag, tagOccurrences.getOrDefault(tag, 0) + 1);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Check if there are no results or no tags found
        if (tagOccurrences.isEmpty()) {
            long endTime = System.currentTimeMillis();
            long durationMillis = endTime - startTime;
            return new QueryResult("No results", durationMillis);
        }

        // Sort the tags by descending order of occurrences
        ArrayList<Map.Entry<String, Integer>> entryList = new ArrayList<>(tagOccurrences.entrySet());

        /*Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });*/
        //ArrayList<String> sortedList = new ArrayList<>();
        Collections.sort(sortedTags, Comparator.comparingInt((String tag) -> tagOccurrences.get(tag)).reversed());


        /*for (Map.Entry<String, Integer> entry : entryList) {
            sortedList.add(entry.getKey());
        }*/

        StringBuilder stringBuilder = new StringBuilder();

        for (String tag : sortedTags) {
            int occurrences = tagOccurrences.get(tag);
            stringBuilder.append(tag).append(" ").append(occurrences).append("\n");
        }


        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        return new QueryResult(stringBuilder.toString(), durationMillis);
    }



    // \27/ query opcional
    public static QueryResult getRisingStars(int startYear, int endYear, String order) {
        List<String> results = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        // Create a list of artists with songs in all years between startYear and endYear
        List<Artist> eligibleArtists = new ArrayList<>();
        for (Artist artist : artistMap.values()) {
            boolean hasSongsInAllYears = true;
            for (int year = startYear; year <= endYear; year++) {
                if (!hasSongsInYear(artist, year)) {
                    hasSongsInAllYears = false;
                    break;
                }
            }
            if (hasSongsInAllYears) {
                eligibleArtists.add(artist);
            }
        }

        // Sort the list of artists by ascending or descending average popularity
        eligibleArtists.sort((artist1, artist2) -> {
            double averagePopularity1 = calculateAveragePopularity(artist1, startYear, endYear);
            double averagePopularity2 = calculateAveragePopularity(artist2, startYear, endYear);

            if (order.equals("ASC")) {
                return Double.compare(averagePopularity1, averagePopularity2);
            } else {
                return Double.compare(averagePopularity2, averagePopularity1);
            }
        });

        // Add the artists to the results list
        int count = Math.min(eligibleArtists.size(), 15);
        for (int i = 0; i < count; i++) {
            Artist artist = eligibleArtists.get(i);
            double averagePopularity = calculateAveragePopularity(artist, startYear, endYear);
            results.add(artist.name + " <=> " + averagePopularity);
        }

        // Check if there are no results
        if (results.isEmpty()) {
            results.add("No results");
        }


        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        return new QueryResult(results.toString(), durationMillis);
    }

    // \28/ esta função verifica se o artista tem musicas nesse ano
    public static boolean hasSongsInYear(Artist artist, int year) {
        for (Songs song : matchedSongsArrayList) {
            if (song.year == year) {
                return true;
            }
        }
        return false;
    }

    // \29/ esta função faz a media da popularidade de um artista em especifico durante um periodo especifico de anos
    public static double calculateAveragePopularity(Artist artist, int startYear, int endYear) {
        double totalPopularity = 0.0;
        int songCount = 0;

        for (Songs song : matchedSongsArrayList) {
            int songYear = song.year;
            if (songYear >= startYear && songYear <= endYear) {
                totalPopularity += song.popularidade;
                songCount++;
            }
        }

        if (songCount > 0) {
            return totalPopularity / songCount;
        } else {
            return 0.0;
        }
    }




    /*
    LEGENDA

    \1/ parseMultipleArtists

    \2/ a \7/ loadFiles

    \8/ musicas_colaborado

    \9/ getObjects

    \10/ e \11/ parse e execute

    \12/ a \20/ queries obrigatórias

    \21/ a \29/ queries opcionais
     */





    public static void main(String[] args) {
        //loadFiles(new File("large-files"));
        //System.out.println(get_most_frequent_words_in_artist_name(8,10,10).result);
        //System.out.println(getUniqueTagsInBetweenYears(1920,2000).result);
        /*long start = currentTimeMillis();
        //System.out.println(get_artist_one_song(1,1900,2010).result);
        for (Map.Entry<String, Artist> entry : artistMap.entrySet()) {
            String artistId = entry.getKey();
            Artist artist = entry.getValue();
            System.out.println("Artist ID: " + artistId);
            System.out.println("Artist Object: " + artist.tags);
        }

        System.out.println(currentTimeMillis()-start);
        /*long startTime =System.currentTimeMillis();
        loadFiles(new File("large-files"));
        System.out.println(getObjects(TipoEntidade.ARTISTA));
        long durationMillis = System.currentTimeMillis() - startTime;
        System.out.println("it took  "+durationMillis);*/
        System.out.println("Welcome to Rock in DEISI!");
        if (!loadFiles(new File("."))) {
            System.out.println("Error reading files");
            return;
        }
        Scanner in = new Scanner(System.in);
        String line = in.nextLine();
        while (line != null && !line.equals("EXIT")) {
            QueryResult result = execute(line);
            if (result == null) {
                System.out.println("Illegal command. Try again");
            } else {
                System.out.println(result.result);
                System.out.println("(took " + result.time + " ms)");
            }
            line = in.nextLine();
        }

    }
}