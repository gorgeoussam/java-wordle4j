package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;


public class WordleDictionary {

    public static final int WORD_LENGTH = 5;

    private final List<String> words;

    public WordleDictionary(List<String> words) throws EmptyDictionaryException {
        if (words == null || words.isEmpty()) {
            throw new EmptyDictionaryException("Dictionary is empty.");
        }
        this.words = List.copyOf(words);
    }

    public boolean contains(String rawWord) {
        String word = normalizeWord(rawWord);
        return words.contains(word);
    }

    public String getRandomWord(Random random) {
        return words.get(random.nextInt(words.size()));
    }

    public List<String> getWords() {
        return words;
    }

    public static String normalizeWord(String rawWord) {
        if (rawWord == null) {
            return "";
        }
        return rawWord.trim()
                .toLowerCase(Locale.ROOT)
                .replace('ё', 'е');
    }

    public static boolean isRussianWord(String word) {
        if (word == null || word.length() != WORD_LENGTH) {
            return false;
        }
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (ch < 'а' || ch > 'я') {
                return false;
            }
        }
        return true;
    }

    public static List<String> prepareWords(List<String> rawWords) {
        Set<String> prepared = new LinkedHashSet<>();
        for (String rawWord : rawWords) {
            String normalized = normalizeWord(rawWord);
            if (isRussianWord(normalized)) {
                prepared.add(normalized);
            }
        }
        return Collections.unmodifiableList(new ArrayList<>(prepared));
    }

}
