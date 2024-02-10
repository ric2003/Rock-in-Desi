package pt.ulusofona.aed.rockindeisi2023;

import java.util.ArrayList;
import java.util.List;

public class Artist {
    public String name;
    public List<String> tags;

    // Constructor
    public Artist(String name) {
        this.name = name;
        this.tags = new ArrayList<>();
    }

    public Artist(String name, List<String> tags) {
        this.name = name;
        this.tags = tags;
    }
}
