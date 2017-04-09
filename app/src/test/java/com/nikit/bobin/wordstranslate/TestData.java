package com.nikit.bobin.wordstranslate;

import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.SynonymGroup;
import com.nikit.bobin.wordstranslate.translating.models.Translation;
import com.nikit.bobin.wordstranslate.translating.models.WordLookup;

import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TestData {
    public static Response createFakeResponse(int code, String body, String requestUrl) {
        Request request = new Request.Builder()
                .method("POST", RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), ""))
                .url(requestUrl)
                .build();
        return new Response.Builder()
                .code(code)
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .body(ResponseBody.create(MediaType.parse("application/json"), body))
                .build();
    }

    public static Response createEmptyResponse() {
        return createFakeResponse(200, "", "http://dummy");
    }

    public static String createTranslationJson(String direction, String text) {
        if (text == null)
            return String.format("{ \"code\": 200, \"lang\": \"%s\", \"text\": [] }", direction);
        return String.format("{ \"code\": 200, \"lang\": \"%s\", \"text\": [\"%s\"] }", direction, text);
    }

    public static String createEmptyJsonWithCode(int code) {
        return String.format("{ \"code\": %d}", code);
    }

    public static String createFailJson(int code) {
        return String.format(Locale.getDefault(), "{\"code\":%d,\"message\":\"Bad message\"}", code);
    }

    public static String createLangsJson() {
        return "{\"dirs\":[\"ru-en\",\"ru-fr\",\"en-fr\",\"en-ru\",\"fr-en\",\"fr-ru\"]," +
                "\"langs\":{\"ru\":\"Russian\",\"en\":\"English\",\"fr\":\"French\"}}";
    }

    public static Language[] getExtractedLangs() {
        Language languageRu = new Language("ru", "Russian");
        Language languageEn = new Language("en", "English");
        Language languageFr = new Language("fr", "French");

        return new Language[]{languageRu, languageEn, languageFr};
    }

    public static String createDirectionsJson() {
        return "{\"dirs\":[\"ru-en\",\"ru-fr\",\"en-fr\",\"en-ru\",\"fr-en\",\"fr-ru\"]}";
    }

    public static Direction[] getExtractedDirections() {
        return new Direction[]{
                Direction.parseKeySerialized("ru-en"),
                Direction.parseKeySerialized("ru-fr"),
                Direction.parseKeySerialized("en-fr"),
                Direction.parseKeySerialized("en-ru"),
                Direction.parseKeySerialized("fr-en"),
                Direction.parseKeySerialized("fr-ru")
        };
    }

    public static String getCorrectDetectionJson() {
        return "{\"code\":200,\"lang\":\"en\"}";
    }

    public static String getCorrectLookupJson() {
        return "{\n" +
                "    \"head\": {},\n" +
                "    \"def\": [\n" +
                "        {\n" +
                "            \"text\": \"привет\",\n" +
                "            \"pos\": \"noun\",\n" +
                "            \"anm\": \"неодуш\",\n" +
                "            \"tr\": [\n" +
                "                {\n" +
                "                    \"text\": \"hi\",\n" +
                "                    \"pos\": \"noun\",\n" +
                "                    \"syn\": [\n" +
                "                        {\n" +
                "                            \"text\": \"hello\",\n" +
                "                            \"pos\": \"noun\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"text\": \"hallo\",\n" +
                "                            \"pos\": \"noun\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"text\": \"salutation\",\n" +
                "                            \"pos\": \"noun\"\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"mean\": [\n" +
                "                        {\n" +
                "                            \"text\": \"приветствие\"\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"text\": \"hey\",\n" +
                "                    \"pos\": \"noun\",\n" +
                "                    \"mean\": [\n" +
                "                        {\n" +
                "                            \"text\": \"ну\"\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"text\": \"regards\",\n" +
                "                    \"pos\": \"noun\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"text\": \"hail\",\n" +
                "                    \"pos\": \"noun\",\n" +
                "                    \"mean\": [\n" +
                "                        {\n" +
                "                            \"text\": \"оклик\"\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"text\": \"howdy\",\n" +
                "                    \"pos\": \"noun\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"text\": \"remembrance\",\n" +
                "                    \"pos\": \"noun\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
    }

    public static WordLookup getExtractedWordLookup() {
        SynonymGroup[] synonymGroups = new SynonymGroup[6];
        synonymGroups[0] = new SynonymGroup(
                new String[]{"приветствие"},
                new String[]{"hi", "hello", "hallo", "salutation"}
        );
        synonymGroups[1] = new SynonymGroup(
                new String[]{"ну"},
                new String[]{"hey"}
        );
        synonymGroups[2] = new SynonymGroup(
                new String[0],
                new String[]{"regards"}
        );
        synonymGroups[3] = new SynonymGroup(
                new String[]{"оклик"},
                new String[]{"hail"}
        );
        synonymGroups[4] = new SynonymGroup(
                new String[0],
                new String[]{"howdy"}
        );
        synonymGroups[5] = new SynonymGroup(
                new String[0],
                new String[]{"remembrance"}
        );
        return new WordLookup(new Translation("привет", "ru-en"), synonymGroups);
    }
}
