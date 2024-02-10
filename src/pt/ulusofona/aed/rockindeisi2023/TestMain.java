package pt.ulusofona.aed.rockindeisi2023;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TestMain {

    @Test
    public void toStringBefore1995() {
        Main.loadFiles(new File("test-files/Before1995"));
        String obtained = Main.getObjects(TipoEntidade.TEMA).toString();

        String expected = "[4Cna7QxOOTNnylVHLtShCi | Only For You | 1980, 2Sy1r4fGfq4Lslfc0gHkbk | Loops & Tings - Radio Edit | 1990]";
        assertEquals(expected,obtained,"erro nas musicas antes de 1995");
    }

    @Test
    public void toStringBefore2000() {
        Main.loadFiles(new File("test-files/Before2000"));
        String obtained = Main.getObjects(TipoEntidade.TEMA).toString();

        String expected = "[4Cna7QxOOTNnylVHLtShCi | Only For You | 1999 | 3:37 | 57, 2Sy1r4fGfq4Lslfc0gHkbk | Loops & Tings - Radio Edit | 1996 | 4:44 | 45]";
        assertEquals(expected,obtained,"erro nas musicas antes de 2000");
    }

    @Test
    public void toStringAfter2000() {
        Main.loadFiles(new File("test-files/After2000"));
        String obtained = Main.getObjects(TipoEntidade.TEMA).toString();

        String expected = "[4Cna7QxOOTNnylVHLtShCi | Only For You | 2000 | 3:37 | 57 | 1, 2Sy1r4fGfq4Lslfc0gHkbk | Loops & Tings - Radio Edit | 2003 | 4:44 | 45 | 2]";
        assertEquals(expected,obtained,"erro nas musicas depois de 2000");
    }

    @Test
    public void toStringInfoDeArtistas() {
        Main.loadFiles(new File("test-files/InfoDeArtistas"));
        String obtained = Main.getObjects(TipoEntidade.ARTISTA).toString();

        String expected = "[Artista: [Madonna] | 1, Artista: [La Sonora Matancera] | 1, Artista: [Carlos Argentino]]";
        assertEquals(expected,obtained,"erro nos Artistas");
    }

    @Test
    public void loadTest_fileCertos() {
        boolean received = Main.loadFiles(new File("test-files/certos"));
        if (!received) {
            Assertions.fail("Failed to load files from test-files/certos directory.");
        }
        Assertions.assertTrue(received);
    }

    @Test
    public void loadTest_fileErrado() {
        boolean received = Main.loadFiles(new File("test-files/erros"));
        if (!received) {
            Assertions.fail("Failed to load files from test-files/erros directory.");
        }
        Assertions.assertTrue(received);
    }

    @Test
    public void countSongsYear_OBG() {
        Main.loadFiles(new File("test-files/countSongsYear"));
        QueryResult queryResult = Main.execute("COUNT_SONGS_YEAR 1922");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        String expected = "3";
        assertEquals(expected, queryResult.result, "erro na query COUNT_SONGS_YEAR");
    }

    @Test
    public void countSongsYear_OBG2() {
        Main.loadFiles(new File("test-files/countSongsYear"));
        QueryResult queryResult = Main.execute("COUNT_SONGS_YEAR 2000");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        String expected = "1";
        assertEquals(expected, queryResult.result, "erro na query COUNT_SONGS_YEAR");
    }


    @Test
    public void countDuplicateSongsYear() {
        Main.loadFiles(new File("test-files/countDuplicateSongsYear"));
        QueryResult queryResult = Main.execute("COUNT_DUPLICATE_SONGS_YEAR 1920");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        String expected = "2";
        assertEquals(expected, queryResult.result, "erro na query COUNT_DUPLICATE_SONGS_YEAR");
    }

    @Test
    public void countDuplicateSongsYear2() {
        Main.loadFiles(new File("test-files/countDuplicateSongsYear"));
        QueryResult queryResult = Main.execute("COUNT_DUPLICATE_SONGS_YEAR 1919");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        String expected = "0";
        assertEquals(expected, queryResult.result, "erro na query COUNT_DUPLICATE_SONGS_YEAR");
    }

    @Test
    public void getSongsByArtist_OBG() {
        Main.loadFiles(new File("test-files/getSongsByArtist"));
        QueryResult queryResult = Main.execute("GET_SONGS_BY_ARTIST 3 Queen");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        String[] resultParts = queryResult.result.split("\n");
        assertEquals(3, resultParts.length);
        assertArrayEquals(new String[] {
                "\"Feelings, Feelings - Take 10 / July 1977\" : 1977",
                "Seven Seas Of Rhye - Remastered 2011 : 1974",
                "Princes Of The Universe : 1986",
        }, resultParts);
    }

    @Test
    public void getSongsByArtist_OBG2() {
        Main.loadFiles(new File("test-files/getSongsByArtist"));
        QueryResult queryResult = Main.execute("GET_SONGS_BY_ARTIST 1 Nirvana");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        String[] resultParts = queryResult.result.split("\n");
        assertEquals(1, resultParts.length);
        assertArrayEquals(new String[] {
                "About A Girl : 2002",
        }, resultParts);
    }

    @Test
    public void getMostDanceable_OBG() {
        Main.loadFiles(new File("test-files/getMostDanceable"));
        QueryResult queryResult = Main.execute("GET_MOST_DANCEABLE 1920 1922 3");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        String[] resultParts = queryResult.result.split("\n");
        assertEquals(3, resultParts.length);
        assertArrayEquals(new String[] {
                "Capítulo 2.16 - Banquero Anarquista : 1922 : 0.695",
                "Carve : 1922 : 0.645",
                "Aidiniko : 1920 : 0.58"
        }, resultParts);
        queryResult = Main.execute("GET_MOST_DANCEABLE 1923 2004 1");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        resultParts = queryResult.result.split("\n");
        assertEquals(1, resultParts.length);
        assertArrayEquals(new String[] {
                "Yellow : 2000 : 0.429"
        }, resultParts);
    }

    @Test
    public void getArtistsOneSong() {
        Main.loadFiles(new File("test-files/getArtistsOneSong"));
        QueryResult queryResult = Main.execute("GET_ARTISTS_ONE_SONG 1920 1922");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        String[] resultParts = queryResult.result.split("\n");
        assertEquals(5, resultParts.length);
        assertArrayEquals(new String[] {
                "Fernando Pessoa | Capítulo 2.16 - Banquero Anarquista | 1922",
                "Francisco Canaro | Los Indios - Remasterizado | 1920",
                "Ignacio Corsini | El Prisionero - Remasterizado | 1922",
                "Kostas Roukounas | \"Lefká peristerákia mou, tsámiko\" | 1920",
                "Uli | Carve | 1922",
        }, resultParts);
        queryResult = Main.execute("GET_ARTISTS_ONE_SONG 1990 2005");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        resultParts = queryResult.result.split("\n");
        assertEquals(1, resultParts.length);
        assertArrayEquals(new String[] {
                "Coldplay | Yellow | 2000"
        }, resultParts);
    }

    @Test
    public void getTopArtistsWithSongsBetween() {
        Main.loadFiles(new File("test-files/getTopArtistsWithSongsBetween"));
        QueryResult queryResult = Main.execute("GET_TOP_ARTISTS_WITH_SONGS_BETWEEN 3 2 3");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        String[] resultParts = queryResult.result.split("\n");
        assertEquals(2, resultParts.length);
        assertArrayEquals(new String[] {
                "Manolis Karapiperis 2",
                "Amalia Vaka 2",
        }, resultParts);
        queryResult = Main.execute("GET_TOP_ARTISTS_WITH_SONGS_BETWEEN 2 0 1");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        resultParts = queryResult.result.split("\n");
        assertEquals(2, resultParts.length);
        assertArrayEquals(new String[] {
                "Ignacio Corsini 1",
                "Coldplay 1"
        }, resultParts);
    }

    @Test
    public void getUniqueTags() {
        Main.loadFiles(new File("test-files/getUniqueTags"));
        Main.execute("ADD_TAGS Fernando Pessoa;Poeta");
        Main.execute("ADD_TAGS Nirvana;Rock");
        Main.execute("ADD_TAGS Coldplay;Rock");
        QueryResult queryResult = Main.execute("GET_UNIQUE_TAGS");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        String[] resultParts = queryResult.result.split("\n");
        assertEquals(2, resultParts.length);
        assertArrayEquals(new String[] {
                "POETA 1",
                "ROCK 2",
        }, resultParts);
        Main.execute("REMOVE_TAGS Fernando Pessoa;Poeta");
        queryResult = Main.execute("GET_UNIQUE_TAGS");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        resultParts = queryResult.result.split("\n");
        assertEquals(1, resultParts.length);
        assertArrayEquals(new String[] {
                "ROCK 2",
        }, resultParts);
    }

    @Test
    public void getArtistsForTag() {
        Main.loadFiles(new File("test-files/getArtistsForTag"));
        Main.execute("ADD_TAGS Coldplay;Rock");
        QueryResult queryResult = Main.execute("GET_ARTISTS_FOR_TAG Rock");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        String[] resultParts = queryResult.result.split("\n");
        assertEquals(1, resultParts.length);
        assertArrayEquals(new String[] {
                "Coldplay",
        }, resultParts);
        Main.execute("ADD_TAGS Nirvana;Rock");
        queryResult = Main.execute("GET_ARTISTS_FOR_TAG Rock");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        resultParts = queryResult.result.split("\n");
        assertEquals(1, resultParts.length);
        assertArrayEquals(new String[] {
                "Coldplay;Nirvana",
        }, resultParts);
    }

    @Test
    public void getArtistsWithMinDuration() {
        Main.loadFiles(new File("test-files/getArtistsWithMinDuration"));
        QueryResult queryResult = Main.execute("GET_ARTISTS_WITH_MIN_DURATION 1920 210");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        String[] resultParts = queryResult.result.split("\n");
        assertEquals(4, resultParts.length);
        assertArrayEquals(new String[] {
                "Amalia Vaka | Thelo na se lismoniso | 258160",
                "Amalia Vaka | Thelo na se lismoniso | 258160",
                "Manolis Karapiperis | Aidiniko | 267560",
                "Manolis Karapiperis | Aidiniko | 267560",
        }, resultParts);
        queryResult = Main.execute("GET_ARTISTS_WITH_MIN_DURATION 2000 300");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        resultParts = queryResult.result.split("\n");
        assertEquals(1, resultParts.length);
        assertArrayEquals(new String[] {
                "No artists"
        }, resultParts);
    }

    @Test
    public void getSongTitlesConsideringWords() {
        Main.loadFiles(new File("test-files/getSongTitlesConsideringWords"));
        QueryResult queryResult = Main.execute("GET_SONG_TITLES_CONSIDERING_WORDS 2 thelo go");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        String[] resultParts = queryResult.result.split("\n");
        assertEquals(2, resultParts.length);
        assertArrayEquals(new String[] {
                "Thelo na se lismoniso",
                "Thelo na se lismoniso",
        }, resultParts);
        queryResult = Main.execute("GET_SONG_TITLES_CONSIDERING_WORDS 3  thelo");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        resultParts = queryResult.result.split("\n");
        assertEquals(3, resultParts.length);
        assertArrayEquals(new String[] {
                "Aidiniko",
                "Aidiniko",
                "\"Lefká peristerákia mou, tsámiko\""
        }, resultParts);
    }

    @Test
    public void getSongTitlesConsideringWords2() {
        Main.loadFiles(new File("test-files/getSongTitlesConsideringWords"));
        QueryResult queryResult = Main.execute("GET_SONG_TITLES_CONSIDERING_WORDS 1 bloom in");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        String[] resultParts = queryResult.result.split("\n");
        assertEquals(1, resultParts.length);
        assertArrayEquals(new String[] {
                "When the Roses Bloom Again",
        }, resultParts);
        queryResult = Main.execute("GET_SONG_TITLES_CONSIDERING_WORDS 2 bloom yes");
        assertNotNull(queryResult);
        assertNotNull(queryResult.result);
        resultParts = queryResult.result.split("\n");
        assertEquals(2, resultParts.length);
        assertArrayEquals(new String[] {
                "When the Roses Bloom Again",
                "In Bloom - Nevermind Version"
        }, resultParts);
    }



    /*

    @Test-
    public void getArtistsForTags(){
        File folder = new File("test-files");
        Main.loadFiles(folder);
        Main.artistTags=new HashMap<>();
        Main.execute("ADD_TAGS Radiohead;Nirvana");
        QueryResult queryResult  = Main.execute("GET_ARTISTS_FOR_TAG Nirvana");
        String expectedResult = "Radiohead";
        assert queryResult != null;
        Assertions.assertEquals(expectedResult, queryResult.result);
    }

    @Test
    public void getArtistsForTags2(){
        File folder = new File("test-files");
        Main.loadFiles(folder);
        Main.artistTags=new HashMap<>();
        Main.execute("ADD_TAGS Nirvana;Radiohead");
        Main.execute("ADD_TAGS Queen;Breakupwithyourgirlfriend;Radiohead");
        QueryResult queryResult  = Main.execute("GET_ARTISTS_FOR_TAG Radiohead");
        String expectedResult = "Nirvana;Queen";
        assert queryResult != null;
        Assertions.assertEquals(expectedResult, queryResult.result);
    }
*/

}