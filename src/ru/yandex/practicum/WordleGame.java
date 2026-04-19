package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class WordleGame {

    private final String answer;

    private int stepsLeft;

    private final WordleDictionary dictionary;

    private final PrintWriter log;

    private final List<String> guesses = new ArrayList<>();

    private final List<String> feedbacks = new ArrayList<>();

    private final Set<String> shownHints = new HashSet<>();

    public WordleGame(WordleDictionary dictionary, int steps, Random random, PrintWriter log) {
        if (steps <= 0) {
            throw new IllegalArgumentException("Steps must be positive.");
        }
        this.dictionary = dictionary;
        this.stepsLeft = steps;
        this.answer = dictionary.getRandomWord(random);
        this.log = log;
    }

    public String makeMove(String rawWord) throws InvalidWordException, WordNotFoundInDictionaryException {
        String word = WordleDictionary.normalizeWord(rawWord);
        validateWord(word);
        stepsLeft--;
        String feedback = buildFeedback(word, answer);
        guesses.add(word);
        feedbacks.add(feedback);
        log.println("Move: " + word + ", feedback: " + feedback + ", stepsLeft: " + stepsLeft);
        log.flush();
        return feedback;
    }

    public String suggestHint() {
        List<String> words = dictionary.getWords();
        for (String candidate : words) {
            if (shownHints.contains(candidate)) {
                continue;
            }
            if (matchesHistory(candidate)) {
                shownHints.add(candidate);
                log.println("Hint: " + candidate);
                log.flush();
                return candidate;
            }
        }
        throw new IllegalStateException("No hints available for current game state.");
    }

    private boolean matchesHistory(String candidate) {
        for (int i = 0; i < guesses.size(); i++) {
            if (!buildFeedback(guesses.get(i), candidate).equals(feedbacks.get(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean isWon() {
        return !guesses.isEmpty() && guesses.get(guesses.size() - 1).equals(answer);
    }

    public boolean isFinished() {
        return isWon() || stepsLeft == 0;
    }

    public int getStepsLeft() {
        return stepsLeft;
    }

    public String getAnswer() {
        return answer;
    }

    public List<String> getGuesses() {
        return List.copyOf(guesses);
    }

    private void validateWord(String word) throws InvalidWordException, WordNotFoundInDictionaryException {
        if (!WordleDictionary.isRussianWord(word)) {
            throw new InvalidWordException("Введите слово из 5 русских букв.");
        }
        if (!dictionary.contains(word)) {
            throw new WordNotFoundInDictionaryException("Слова нет в словаре: " + word);
        }
    }

    public static String buildFeedback(String guess, String answer) {
        char[] marks = {'-', '-', '-', '-', '-'};
        Map<Character, Integer> rest = new HashMap<>();

        for (int i = 0; i < WordleDictionary.WORD_LENGTH; i++) {
            char g = guess.charAt(i);
            char a = answer.charAt(i);
            if (g == a) {
                marks[i] = '+';
            } else {
                rest.put(a, rest.getOrDefault(a, 0) + 1);
            }
        }

        for (int i = 0; i < WordleDictionary.WORD_LENGTH; i++) {
            if (marks[i] == '+') {
                continue;
            }
            char g = guess.charAt(i);
            int count = rest.getOrDefault(g, 0);
            if (count > 0) {
                marks[i] = '^';
                rest.put(g, count - 1);
            }
        }

        return new String(marks);
    }

}
