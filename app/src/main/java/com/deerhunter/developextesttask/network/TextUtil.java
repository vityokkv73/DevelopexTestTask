package com.deerhunter.developextesttask.network;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
    public static List<String> findLinks(String content) {
        List<String> foundLinks = new ArrayList<>();
        String urlRegex = "(http:((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(content);

        while (urlMatcher.find()) {
            foundLinks.add(content.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return foundLinks;
    }

    public static boolean isHtmlUrl(String str) {
        String urlRegex = "(http:((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(str);
        return urlMatcher.find();
    }
}
