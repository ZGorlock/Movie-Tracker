/*
 * File:    StringUtility.java
 * Package: client.utility
 * Author:  Zachary Gill
 */

package client.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * A resource class with provides additional String functionality.
 */
public final class StringUtility
{
    
    //Logger
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(StringUtility.class);
    
    
    //Functions
    
    /**
     * Tokenizes a passed string into its words and returns a list of those words.
     *
     * @param str   The string to tokenize.
     * @param delim The delimiter to separate tokens by.
     * @return A list of all the tokens of the passed string.
     */
    public static List<String> tokenize(String str, String delim)
    {
        List<String> tokens = new ArrayList<>();
        
        StringTokenizer st = new StringTokenizer(str, delim);
        while (st.hasMoreTokens()) {
            tokens.add(st.nextToken());
        }
        
        return tokens;
    }
    
    /**
     * Tokenizes a passed string into its words and returns a list of those words.
     *
     * @param str The string to tokenize.
     * @return A list of all the tokens of the passed string.
     * @see #tokenize(String, String)
     */
    public static List<String> tokenize(String str)
    {
        return tokenize(str, " ");
    }
    
    /**
     * Splits a passed string by line separators and returns a list of lines.
     *
     * @param str The string to split.
     * @return A list of the lines in the passed string.
     */
    @SuppressWarnings("HardcodedLineSeparator")
    public static List<String> splitLines(String str)
    {
        String lines[] = str.split("\\r?\\n");
        return new ArrayList<>(Arrays.asList(lines));
    }
    
    /**
     * Detokenizes a passed list of tokens back into a string.
     *
     * @param tokens The list of tokens to detokenize.
     * @param delim  The delimiter to insert between tokens.
     * @return A string composed of the tokens in the passed list.
     */
    public static String detokenize(List<String> tokens, String delim)
    {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < tokens.size(); i++) {
            str.append(tokens.get(i));
            if (i != (tokens.size() - 1)) {
                str.append(delim);
            }
        }
        return str.toString();
    }
    
    /**
     * Detokenizes a passed list of tokens back into a string.
     *
     * @param tokens The list of tokens to detokenize.
     * @return A string composed of the tokens in the passed list.
     * @see #detokenize(List, String)
     */
    public static String detokenize(List<String> tokens)
    {
        return detokenize(tokens, " ");
    }
    
    /**
     * Converts a string to camel case.
     *
     * @param string The string to convert.
     * @return The string converted to camel case.
     */
    public static String toCamelCase(String string)
    {
        StringBuilder camelCase = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            char c1 = (string.length() > (i + 1)) ? string.charAt(i + 1) : '\0';
            char c2 = (string.length() > (i + 2)) ? string.charAt(i + 2) : '\0';
            if (Character.isUpperCase(c)) {
                camelCase.append(Character.toLowerCase(c));
                if (((c1 != '\0') && Character.isUpperCase(c1)) &&
                        ((c2 != '\0') && Character.isLowerCase(c2))) {
                    camelCase.append(string.substring(i + 1));
                    break;
                }
            } else {
                camelCase.append(string.substring(i));
                break;
            }
        }
        return camelCase.toString();
    }
    
    /**
     * Converts a string to title case.<br>
     * Cannot handle lower case acronyms at the beginning of the string.
     *
     * @param string The string to convert.
     * @return The string converted to title case.
     */
    public static String toTitleCase(String string)
    {
        if (!Character.isUpperCase(string.charAt(0))) {
            return Character.toUpperCase(string.charAt(0)) + string.substring(1);
        }
        return string;
    }
    
    /**
     * Converts a string to constant case.
     *
     * @param string The string to convert.
     * @return The string converted to constant case.
     */
    public static String toConstantCase(String string)
    {
        String titleCase = toTitleCase(string);
        List<String> words = new ArrayList<>();
        
        int l = 0;
        for (int i = 0; i < titleCase.length(); i++) {
            char c = titleCase.charAt(i);
            char c1 = (titleCase.length() > (i + 1)) ? titleCase.charAt(i + 1) : '\0';
            
            if (Character.isUpperCase(c) && ((c1 != '\0') && !Character.isUpperCase(c1))) {
                words.add(titleCase.substring(l, i - l));
                l = i;
            } else if (i == (titleCase.length() - 1)) {
                words.add(titleCase.substring(l));
            }
        }
        
        StringBuilder constantCase = new StringBuilder();
        for (String word : words) {
            if (constantCase.length() > 0) {
                constantCase.append('_');
            }
            constantCase.append(word.toUpperCase());
        }
        return constantCase.toString();
    }
    
    /**
     * Determines if a character is a vowel or not.
     *
     * @param c The character.
     * @return Whether the character is a vowel or not.
     */
    public static boolean isVowel(char c)
    {
        return "AEIOUaeiou".indexOf(c) != -1;
    }
    
    /**
     * Determines if a character is a consonant or not.
     *
     * @param c The character.
     * @return Whether the character is a consonant or not.
     */
    public static boolean isConsonant(char c)
    {
        return "BCDFGHJKLMNPQRSTVWXYZbcdfghjklmnpqrstvwxyz".indexOf(c) != -1;
    }
    
    /**
     * Determines if a string is alphanumeric or not.
     *
     * @param str The string.
     * @return Whether the string is alphanumeric or not.
     */
    public static boolean isAlphanumeric(String str)
    {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isAlphabetic(c) && !Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Removes the punctuation from a string.
     *
     * @param string The string to operate on.
     * @return The string with punctuation removed.
     */
    public static String removePunctuation(String string)
    {
        StringBuilder sb = new StringBuilder(string);
        for (int i = 0; i < string.length(); i++) {
            char current = string.charAt(i);
            if (!(Character.isLetterOrDigit(current) || Character.isWhitespace(current))) {
                sb.deleteCharAt(i);
            }
        }
        return sb.toString();
    }
    
    /**
     * Gently removes the punctuation from a tokenized string.<br>
     * Does not remove decimals from numbers or operators from expressions.
     *
     * @param tokens The list of tokens to operate on.
     * @param save   A list of punctuation characters to ignore.
     * @return The tokens with punctuation gently removed.
     */
    private static List<String> removePunctuationSoft(List<String> tokens, List<Character> save)
    {
        List<String> depuncted = new ArrayList<>();
        
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            
            if (((i != 0) && (i != (tokens.size() - 1))) &&
                    (tokenIsOperator(token) && (tokenIsNum(tokens.get(i - 1)) && tokenIsNum(tokens.get(i + 1))))) { //keep operators for expressions
                depuncted.add(token);
            } else {
                for (int j = 0; j < token.length(); j++) {
                    char current = token.charAt(j);
                    
                    if (!Character.isLetterOrDigit(current)) { //found a symbol character
                        boolean remove = true;
                        
                        if ((j < (token.length() - 1)) && Character.isDigit(token.charAt(j + 1))) { //if the next digit is a number
                            if (tokenIsOperator(Character.toString(current)) || (current == '.')) { //operators and decimals are ok near numbers
                                remove = false;
                            }
                        }
                        
                        if (remove) {
                            boolean saveChar = false;
                            for (Character c : save) {
                                if (c == current) {
                                    saveChar = true;
                                }
                            }
                            if (!saveChar) {
                                StringBuilder sb = new StringBuilder(token);
                                sb.deleteCharAt(j);
                                token = sb.toString();
                                j--;
                            }
                        }
                    }
                }
                depuncted.add(token);
            }
        }
        
        return depuncted;
    }
    
    /**
     * Gently removes the punctuation from a string.<br>
     * Does not remove decimals from numbers or operators from expressions.
     *
     * @param string The string to operate on.
     * @param save   A list of punctuation characters to ignore.
     * @return The string with punctuation gently removed.
     * @see #removePunctuationSoft(List, List)
     */
    public static String removePunctuationSoft(String string, List<Character> save)
    {
        List<String> tokens = tokenize(string);
        tokens = removePunctuationSoft(tokens, save);
        
        return detokenize(tokens);
    }
    
    /**
     * Gently removes the punctuation from a string.<br>
     * Does not remove decimals from numbers or operators from expressions.
     *
     * @param string The string to operate on.
     * @return The string with punctuation gently removed.
     * @see #removePunctuationSoft(String, List)
     */
    public static String removePunctuationSoft(String string)
    {
        return removePunctuationSoft(string, new ArrayList<Character>());
    }
    
    /**
     * Removes the whitespace from a string.
     *
     * @param string The string to operate on.
     * @return The string with all whitespace characters removed.
     */
    public static String removeWhiteSpace(String string)
    {
        return string.replaceAll("\\s", "");
    }
    
    /**
     * Trims the whitespace off of the front and back ends of a string.
     *
     * @param str The string to trim.
     * @return The trimmed string.
     */
    public static String trim(String str)
    {
        return lTrim(rTrim(str));
    }
    
    /**
     * Trims the whitespace off the left end of a string.
     *
     * @param str The string to trim.
     * @return The trimmed string.
     */
    public static String lTrim(String str)
    {
        String trimmed = "";
        
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                trimmed = str.substring(i);
                break;
            }
        }
        
        return trimmed;
    }
    
    /**
     * Trims the whitespace off the right end of a string.
     *
     * @param str The string to trim.
     * @return The trimmed string.
     */
    public static String rTrim(String str)
    {
        String trimmed = "";
        
        for (int i = str.length() - 1; i >= 0; i--) {
            if (!Character.isWhitespace(str.charAt(i))) {
                trimmed = str.substring(0, i + 1);
                break;
            }
        }
        
        return trimmed;
    }
    
    /**
     * Determines if a string token represents a number of not.
     *
     * @param token The token to examine.
     * @return Whether the token represents a number of not.
     */
    public static boolean tokenIsNum(String token)
    {
        try {
            double d = Double.parseDouble(token);
        } catch (NumberFormatException ignored) {
            return false;
        }
        return true;
    }
    
    /**
     * Determines if a string token represents an operator or not.
     *
     * @param token The token to examine.
     * @return Whether the token represents an operator or not.
     */
    @SuppressWarnings("HardcodedFileSeparator")
    public static boolean tokenIsOperator(String token)
    {
        switch (token.length()) {
            case 1:
                return (Objects.equals("+", token) || Objects.equals("-", token) || Objects.equals("*", token) || Objects.equals("/", token) || Objects.equals("\\", token) || Objects.equals("%", token) || Objects.equals(">", token) || Objects.equals("<", token));
            case 2:
                return (Objects.equals("==", token) || Objects.equals("!=", token) || Objects.equals("<>", token) || Objects.equals(">=", token) || Objects.equals("<=", token));
            default:
                return false;
        }
    }
    
    /**
     * Pads a string on the left to a specified length.
     *
     * @param str     The string to pad.
     * @param size    The target size of the string.
     * @param padding The character to pad with.
     * @return The padded string.
     */
    public static String padLeft(String str, int size, char padding)
    {
        if (str.length() >= size) {
            return str;
        }
        
        int numPad = size - str.length();
        char[] chars = new char[numPad];
        Arrays.fill(chars, padding);
        String pad = new String(chars);
        return pad + str;
    }
    
    /**
     * Pads a string on the left to a specified length.
     *
     * @param str  The string to pad.
     * @param size The target size of the string.
     * @return The padded string.
     */
    public static String padLeft(String str, int size)
    {
        return padLeft(str, size, ' ');
    }
    
    /**
     * Pads a string on the right to a specified length.
     *
     * @param str     The string to pad.
     * @param size    The target size of the string.
     * @param padding The character to pad with.
     * @return The padded string.
     */
    public static String padRight(String str, int size, char padding)
    {
        if (str.length() >= size) {
            return str;
        }
        
        int numPad = size - str.length();
        char[] chars = new char[numPad];
        Arrays.fill(chars, padding);
        String pad = new String(chars);
        return str + pad;
    }
    
    /**
     * Pads a string on the right to a specified length.
     *
     * @param str  The string to pad.
     * @param size The target size of the string.
     * @return The padded string.
     */
    public static String padRight(String str, int size)
    {
        return padRight(str, size, ' ');
    }
    
    /**
     * Pads a number string with leading zeros to fit a particular size.
     *
     * @param str  The number string to pad.
     * @param size The specified size of the final string.
     * @return The padded number string.
     */
    public static String padZero(String str, int size)
    {
        if (str.length() >= size) {
            return str;
        }
        
        return padLeft(str, size, '0');
    }
    
    /**
     * Pads a number string with leading zeros to fit a particular size.
     *
     * @param num  The number to pad.
     * @param size The specified size of the final string.
     * @return The padded number string.
     */
    public static String padZero(int num, int size)
    {
        return padZero(Integer.toString(num), size);
    }
    
    /**
     * Creates a string of the length specified filled with the character specified.
     *
     * @param fill The character to fill the string with.
     * @param size The length to make the string.
     * @return A new string filled with the specified character to the length specified.
     */
    public static String fillStringOfLength(char fill, int size)
    {
        return padRight("", size, fill);
    }
    
}
