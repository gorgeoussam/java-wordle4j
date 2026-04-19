package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class WordleDictionaryLoader {

    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    public WordleDictionary load(String fileName) throws DictionaryLoadException, EmptyDictionaryException {
        List<String> rawWords = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(fileName), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                rawWords.add(line);
            }
        } catch (IOException e) {
            throw new DictionaryLoadException("Cannot load dictionary from: " + fileName, e);
        }

        List<String> preparedWords = WordleDictionary.prepareWords(rawWords);
        if (preparedWords.isEmpty()) {
            throw new EmptyDictionaryException("No valid 5-letter Russian words in dictionary: " + fileName);
        }
        log.println("Loaded dictionary words: " + preparedWords.size());
        log.flush();
        return new WordleDictionary(preparedWords);
    }
}
