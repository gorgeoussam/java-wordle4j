package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    private static final String DICTIONARY_FILE = "words_ru.txt";
    private static final String LOG_FILE = "wordle.log";
    private static final int MAX_STEPS = 6;

    public static void main(String[] args) {
        try (PrintWriter log = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
            WordleDictionary dictionary = loader.load(DICTIONARY_FILE);
            WordleGame game = new WordleGame(dictionary, MAX_STEPS, new Random(), log);

            runGameLoop(game, log);
        } catch (InvalidWordException | WordNotFoundInDictionaryException e) {
            System.out.println(e.getMessage());
        } catch (DictionaryLoadException | EmptyDictionaryException | IOException e) {
            try (PrintWriter fallbackLog = new PrintWriter(System.out)) {
                fallbackLog.println("System error: " + e.getMessage());
                e.printStackTrace(fallbackLog);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    private static void runGameLoop(WordleGame game, PrintWriter log)
            throws InvalidWordException, WordNotFoundInDictionaryException {
        try (Scanner scanner = new Scanner(System.in)) {
            while (!game.isFinished()) {
                System.out.println("Введите слово из 5 букв (или Enter для подсказки):");
                String input = scanner.nextLine();

                if (input.trim().isEmpty()) {
                    String hint = game.suggestHint();
                    System.out.println(hint);
                    continue;
                }

                String feedback = game.makeMove(input);
                System.out.println(feedback);
                System.out.println("Осталось попыток: " + game.getStepsLeft());
            }

            if (game.isWon()) {
                System.out.println("Победа!");
            } else {
                System.out.println("Поражение.");
            }
            System.out.println("Загаданное слово: " + game.getAnswer());
            log.println("Game finished. Won: " + game.isWon());
            log.flush();
        }

    }

}
