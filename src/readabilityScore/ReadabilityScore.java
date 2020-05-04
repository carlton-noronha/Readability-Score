package readabilityScore;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class ReadabilityScore {

    private double averageAge;

    private static final Set<Character> vowels = Set.of('a', 'e', 'i', 'o', 'u', 'y');

    private static final Map<Integer, String> automatedReadabilityIndex1 = Map.of(1, "6", 2, "7",
            3, "9", 4, "10", 5, "11", 6, "12", 7, "13", 8, "14", 9, "15",
            10, "16");

    private static final Map<Integer, String> automatedReadabilityIndex2 = Map.of(11, "17",
            12, "18", 13, "24", 14, "24+");

    public static void main(String[] args) {

        ReadabilityScore readabilityScore = new ReadabilityScore();

        String fileName = args[0];

        File file = new File(fileName);

        StringBuilder fileText = new StringBuilder();

        int noOfCharacters = 0;
        int noOfWords;
        int noOfSyllables = 0;
        int noOfPolySyllables = 0;
        int noOfSentences;
        int noOfSyllablesInOneWord;

        try (final Scanner fileReader = new Scanner(file)) {

            final Scanner scanner = new Scanner(System.in);

            System.out.println("The text is:");

            //Read from file
            while (fileReader.hasNext()) {
                String line = fileReader.nextLine();
                fileText.append(line);
            }

            //Displaying file contents
            System.out.println(fileText);
            System.out.println();

            //Sentences ends with either . or ? or !
            noOfSentences = fileText.toString().split("\\.\\s*|\\?\\s*|!\\s*").length;

            //Array of words, words are separated by spaces
            String[] words = fileText.toString().split("\\s+|\\.\\s*|\\?\\s*|!\\s*");

            noOfWords = words.length;

            //For each word we find the # of characters, # of syllables in a word and # of polysyllables
            for (String word : words) {
                noOfCharacters += word.length();
                noOfSyllablesInOneWord = readabilityScore.countSyllables(word.toLowerCase());

                //If number of syllables in a word is 0 the word is one syllable
                noOfSyllablesInOneWord = noOfSyllablesInOneWord > 0 ? noOfSyllablesInOneWord : 1;

                noOfSyllables += noOfSyllablesInOneWord;

                //If a word has more than 2 syllables it is a polysyllable
                if (noOfSyllablesInOneWord > 2) {
                    noOfPolySyllables += 1;
                }
            }

            //The last sentence may not end with a . or ? or !
            //In that case the # of characters will be increased by (noOfSentences - 1)
            //Or else the # of characters will be increased by noOfSentences
            if (fileText.toString().endsWith(".") || fileText.toString().endsWith("?") || fileText.toString().endsWith("!")) {
                noOfCharacters += noOfSentences;
            }
            else{
                noOfCharacters += (noOfSentences - 1);
            }


            System.out.println("Words: " + noOfWords);
            System.out.println("Sentences: " + noOfSentences);
            System.out.println("Characters: " + noOfCharacters);
            System.out.println("Number of syllables: " + noOfSyllables);
            System.out.println("Number of Polysyllables: " + noOfPolySyllables);


            //Readability Tests
            /*  ARI - Automated Readability Index
                FK - Flesch–Kincaid
                SMOG - Simple Measure of Gobbledygook
                CL - Coleman–Liau
             */

            System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
            String choice = scanner.nextLine();

            System.out.println();

            switch (choice) {
                case "ARI":
                    readabilityScore.ari(noOfCharacters, noOfWords, noOfSentences);
                    break;

                case "FK":
                    readabilityScore.fk(noOfWords, noOfSentences, noOfSyllables);
                    break;

                case "SMOG":
                    readabilityScore.smog(noOfPolySyllables, noOfSentences);
                    break;

                case "CL":
                    readabilityScore.cl(noOfCharacters, noOfWords, noOfSentences);
                    break;

                case "all":
                    readabilityScore.ari(noOfCharacters, noOfWords, noOfSentences);
                    readabilityScore.fk(noOfWords, noOfSentences, noOfSyllables);
                    readabilityScore.smog(noOfPolySyllables, noOfSentences);
                    readabilityScore.cl(noOfCharacters, noOfWords, noOfSentences);
                    System.out.println();
                    System.out.printf("This text should be understood in average by %.2f year olds.", (readabilityScore.averageAge / 4 ));
                    break;

                default:
                    System.out.println("Invalid option!");
            }
        } catch (

                FileNotFoundException FNF) {

            System.out.println(FNF.getMessage());

        }

    }

    private int countSyllables(String word) {
        int noOfSyllables = 0;
        boolean doubleVowel = false;
        for (int i = 0; i < word.length(); ++i) {
            if ((i == word.length() - 1) && (word.charAt(i) == 'e')) {
                break;
            }
            if (vowels.contains(word.charAt(i))) {
                if (!doubleVowel) {
                    noOfSyllables += 1;
                }
                doubleVowel = true;
            } else {
                doubleVowel = false;
            }
        }
        return noOfSyllables;
    }

    private void ari(int noOfCharacters, int noOfWords, int noOfSentences) {
        String age;

        //Formula
        double score = (4.71 * ((double) noOfCharacters / noOfWords)) + (0.5 * ((double) noOfWords / noOfSentences)) - 21.43;

        age = determineAge(score);
        System.out.printf("Automated Readability Index: %.2f (about %s year olds).\n", score, age);
    }

    private void fk(int noOfWords, int noOfSentences, int noOfSyllables) {
        String age;

        //Formula
        double score = (0.39 * (double) noOfWords / noOfSentences) + (11.8 * (double) noOfSyllables / noOfWords) - 15.59;

        age = determineAge(score);
        System.out.printf("Flesch–Kincaid readability tests: %.2f (about %s year olds).\n", score, age);
    }

    private void smog(int noOfPolySyllables, int noOfSentences) {
        String age;

        //Formula
        double score = (1.043 * Math.sqrt(noOfPolySyllables * (30 / (double)noOfSentences))) + 3.1291;

        age = determineAge(score);
        System.out.printf("Simple Measure of Gobbledygook: %.2f (about %s year olds).\n", score, age);
    }

    private void cl(int noOfCharacters, int noOfWords, int noOfSentences) {

        String age;

        double averageL = averageCharactersPer100Words(noOfCharacters, noOfWords);
        double averageS = averageSentencesPer100Words(noOfSentences, noOfWords);

        //Formula
        double score = (0.0588 * averageL) - (0.296 * averageS) - 15.8;

        age = determineAge(score);
        System.out.printf("Coleman–Liau index: %.2f (about %s year olds).\n", score, age);
    }

    private String determineAge(double score) {
        int roundedScore = (int)Math.round(score);
        String age;

        if(automatedReadabilityIndex1.containsKey(roundedScore)){
            age =  automatedReadabilityIndex1.get(roundedScore);
        }
        else{
            age = automatedReadabilityIndex2.get(roundedScore);
        }

        if(age.equals("24+")){
            averageAge += 24;
        }
        else{
            averageAge += Double.parseDouble(age);
        }
        return age;
    }

    private double averageSentencesPer100Words(int noOfSentences, int noOfWords) {
        return ((double)noOfSentences / noOfWords) * 100.0;
    }

    private double averageCharactersPer100Words(int noOfCharacters, int noOfWords) {
        return ((double)noOfCharacters / noOfWords) * 100.0;
    }

}
