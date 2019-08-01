package com.bopems.module.bridge.consult.web.rest.util;

import com.google.gson.Gson;
import org.springframework.web.util.HtmlUtils;

import java.util.Map;

public class ValidateUtil {

    private ValidateUtil(){
        //Hidden constructor for class with static methods
    }

    public static boolean PayloadValidate(String match, String body)
    {
        try {
            if (match.equals("") || body.equals("")) {
                return false;
            }
            Gson gson = new Gson();

            Map<String, Object> mapMatch = gson.fromJson(match, Map.class);
            Map<String, Object> mapBody = gson.fromJson(body, Map.class);

            for (Map.Entry<String, Object> propMatch : mapMatch.entrySet()) {
                Object propBody = mapBody.get(propMatch.getKey());
                if (propBody == null)
                    return false;
            }

            return true;
        }
        catch(com.google.gson.JsonSyntaxException ex) {
            return false;
        }
        catch (Exception ex) {
            return false;
        }
    }

    /**
     * Verify if a string contains any HTML characters by comparing its
     * HTML-escaped version with the original.
     * @param String input  the input String
     * @return boolean  True if the String contains HTML characters
     */
    public static boolean isHtml(String input) throws SecurityException {
        if (input != null && !input.equals(HtmlUtils.htmlEscape(input))) {
            throw new SecurityException("XSS Parse Invalid Data.");
        }
        return false;
    }
}
