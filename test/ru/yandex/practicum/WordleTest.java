package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordleTest {

    private WordleDictionary dictionary;

    @BeforeEach
    void setUp() throws EmptyDictionaryException {
        dictionary = new WordleDictionary(List.of("герой", "гонец", "кукла", "ветер"));
    }

    @Test
    void shouldNormalizeWord() {
        assertEquals("ежик", WordleDictionary.normalizeWord("  ЁЖИК "));
    }

    @Test
    void shouldBuildFeedback() {
        assertEquals("+^-^-", WordleGame.buildFeedback("гонец", "герой"));
    }

    @Test
    void shouldRejectNonDictionaryWord() {
        WordleGame game = new WordleGame(dictionary, 6, new Random(1), new PrintWriter(System.out));
        assertThrows(WordNotFoundInDictionaryException.class, () -> game.makeMove("лампа"));
    }

    @Test
    void shouldSpendStepOnlyForValidWord() throws Exception {
        WordleGame game = new WordleGame(dictionary, 6, new Random(1), new PrintWriter(System.out));
        int before = game.getStepsLeft();

        assertThrows(InvalidWordException.class, () -> game.makeMove("abc"));
        assertEquals(before, game.getStepsLeft());

        game.makeMove("герой");
        assertEquals(before - 1, game.getStepsLeft());
    }

    @Test
    void shouldFinishWhenWordIsGuessed() throws Exception {
        WordleDictionary singleWordDictionary = new WordleDictionary(List.of("герой"));
        WordleGame game = new WordleGame(singleWordDictionary, 6, new Random(1), new PrintWriter(System.out));
        game.makeMove("герой");

        assertTrue(game.isWon());
        assertTrue(game.isFinished());
    }

    @Test
    void shouldGiveDifferentHints() {
        WordleDictionary hintDictionary;
        try {
            hintDictionary = new WordleDictionary(List.of("герой", "гонец"));
        } catch (EmptyDictionaryException e) {
            throw new RuntimeException(e);
        }

        WordleGame game = new WordleGame(hintDictionary, 6, new Random(1), new PrintWriter(System.out));
        String first = game.suggestHint();
        String second = game.suggestHint();

        assertFalse(first.equals(second));
    }

}
