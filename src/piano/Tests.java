package piano;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import piano.music.Chord;
import piano.music.Composition;
import piano.music.MusicSymbol;
import piano.music.Note;
import piano.music.Pause;

public class Tests {

    private User testedUser;

    @BeforeEach
    void Setup() {
        testedUser = User.getInstance();
    }

    @Test
    void uniqueUserObject() {
        User user = User.getInstance();
        assertSame(testedUser, user);
    }

    @Test
    void setUserDataTest() {
        testedUser.setData("Marko", "Divjak", "mdivjak");

        assertEquals("Marko", testedUser.getFirstName());
        assertEquals("Divjak", testedUser.getLastName());
        assertEquals("mdivjak", testedUser.getUsername());
    }

    @ParameterizedTest
    @CsvSource({
        "markodivjak, true",
        "marko divjak, false"
    })
    void validateUsername(String username, boolean expected) {
        boolean isValid = testedUser.setData("Marko", "Divjak", username);

        assertEquals(expected, isValid);
    }
    
    @Test
    void printCompositionTest() {
        Composition composition = new Composition();
        composition.add(new Note("A2", MusicSymbol.EIGHT));
        composition.add(new Note("C#4", MusicSymbol.EIGHT));
        composition.add(new Pause(MusicSymbol.FOURTH));
        Chord chord = new Chord();
        chord.add("G3");
        chord.add("H3");
        composition.add(chord);

        Assert.assertEquals("A2C#4 [G3_H3]", composition.toString());
    }

    @Test
    void exportCompositionToTextFormat() {
        Composition composition = new Composition();
        composition.add(new Note("A2", MusicSymbol.EIGHT));
        composition.add(new Note("C#4", MusicSymbol.EIGHT));
        composition.add(new Pause(MusicSymbol.FOURTH));
        Chord chord = new Chord();
        chord.add("G3");
        chord.add("H3");
        composition.add(chord);

        TextFormatter tf = new TextFormatter(composition, "test_export.txt");
        try {
            tf.export("test_export.txt", composition);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        File file = new File("test_export.txt");
		BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            Stream<String> stringStream = reader.lines();
            stringStream.forEach(line -> {
                Assert.assertEquals("[6 T ] [wnull]", line);
            });
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Path path = Paths.get("test_export.txt");
        try {
            Files.delete(path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
    }
}
