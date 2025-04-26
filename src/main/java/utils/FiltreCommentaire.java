package utils;

import java.util.Arrays;
import java.util.List;

public class FiltreCommentaire {

    // List of words to filter out and replace
    private static final List<String> FILTER_WORDS = Arrays.asList("test", "test1");

    // Function to filter the comment text
    public static String filtreCommentaire(String inputText) {
        if (inputText == null) {
            return null;
        }

        // Iterate through the list of words to filter and replace them with ***
        for (String word : FILTER_WORDS) {
            // Use word boundaries to match whole words only, case-insensitive
            inputText = inputText.replaceAll("(?i)\\b" + word + "\\b", "***");
        }

        return inputText;
    }
}