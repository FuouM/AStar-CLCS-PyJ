package parsingUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class StringsParsing {
    public static List<Character> charsFromString(String a) {
        List<Character> chars = new ArrayList<>();
        a = a.trim();
        for (char c : a.toCharArray()) {
            chars.add(c);
        }
        return chars;
    }

    public static String stringFromChars(List<Character> charList) {
        StringBuilder str = new StringBuilder();
        for (Character c : charList) {
            str.append(c);
        }
        return str.toString();
    }

    public static String stringFromInt(List<Integer> result, Map<Integer, Character> indexToChar) {
        List<Character> charList = GenericsParsing.mapItems(result, indexToChar);
        return stringFromChars(charList);
    }
}
