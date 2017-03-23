package com.nikit.bobin.wordstranslate.translating;

import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.translating.exceptions.NotSuccessfulResponseException;
import com.nikit.bobin.wordstranslate.translating.exceptions.ResponseHasNotTargetDataException;
import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.SynonymGroup;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;
import com.nikit.bobin.wordstranslate.translating.models.WordLookup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import okhttp3.Response;

public class YandexResponseExtractor implements IYandexResponseExtractor {

    @Override
    public Language[] extractLanguages(Response response) {
        Ensure.notNull(response, "response");
        Ensure.okHttpResponseIsSuccess(response, "response");

        try {
            JSONObject responseBody = new JSONObject(response.body().string());
            JSONObject langs = responseBody.getJSONObject("langs");

            ArrayList<Language> result = new ArrayList<>(16);
            Iterator<String> keys = langs.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                Language currentLang = new Language(key, langs.getString(key));
                result.add(currentLang);
            }

            return result.toArray(new Language[result.size()]);
        } catch (JSONException e) {
            throw new ResponseHasNotTargetDataException("Response body could not convert to JSONObject", e);
        } catch (IOException e) {
            throw new ResponseHasNotTargetDataException("Response body has IOException", e);
        }
    }

    @Override
    public TranslatedText extractTranslation(Response response, Translation translation) {
        Ensure.notNull(response, "response");
        Ensure.notNull(translation, "translation");

        try {
            JSONObject responseBody = new JSONObject(response.body().string());
            ensureYandexApiResponseIsSuccess(responseBody);

            JSONArray text = responseBody.getJSONArray("text");
            if (text.length() == 0)
                return TranslatedText.fail(translation);
            return TranslatedText.success(text.getString(0), translation);
        } catch (JSONException e) {
            throw new ResponseHasNotTargetDataException("Response body could not convert to JSONObject", e);
        } catch (IOException e) {
            throw new ResponseHasNotTargetDataException("Response body has IOException", e);
        }
    }

    @Override
    public Language extractDetectedLanguage(Response response) {
        try {
            JSONObject responseBody = new JSONObject(response.body().string());
            ensureYandexApiResponseIsSuccess(responseBody);
            String lang = responseBody.getString("lang");
            return new Language(lang);
        } catch (JSONException e) {
            throw new ResponseHasNotTargetDataException("Response body could not convert to JSONObject", e);
        } catch (IOException e) {
            throw new ResponseHasNotTargetDataException("Response body has IOException", e);
        }
    }

    @Override
    public WordLookup extractWordLookup(Response response, Translation translation) {
        try {
            JSONObject responseBody = new JSONObject(response.body().string());
            JSONArray defs = responseBody.getJSONArray("def");
            if (defs.length() == 0)
                return null;
            JSONObject def = defs.getJSONObject(0);
            JSONArray tr = def.getJSONArray("tr");
            SynonymGroup[] synonymGroups = new SynonymGroup[tr.length()];
            for (int i = 0; i < synonymGroups.length; ++i)
                synonymGroups[i] = extractSynonymGroup(tr.getJSONObject(i));
            return new WordLookup(translation, synonymGroups);
        } catch (JSONException e) {
            throw new ResponseHasNotTargetDataException("Response body could not convert to JSONObject", e);
        } catch (IOException e) {
            throw new ResponseHasNotTargetDataException("Response body has IOException", e);
        }
    }

    private static SynonymGroup extractSynonymGroup(JSONObject group) throws JSONException {
        ArrayList<String> meanArray = new ArrayList<>();
        if (group.has("mean")) {
            JSONArray mean = group.getJSONArray("mean");
            for (int i = 0; i < mean.length(); ++i) {
                JSONObject currentMean = mean.getJSONObject(i);
                meanArray.add(currentMean.getString("text"));
            }
        }
        ArrayList<String> synArray = new ArrayList<>();
        synArray.add(group.getString("text"));
        if (group.has("syn")) {
            JSONArray syn = group.getJSONArray("syn");
            for (int i = 0; i < syn.length(); ++i) {
                JSONObject synonym = syn.getJSONObject(i);
                synArray.add(synonym.getString("text"));
            }
        }
        return new SynonymGroup(
                meanArray.toArray(new String[meanArray.size()]),
                synArray.toArray(new String[synArray.size()]));
    }

    private static void ensureYandexApiResponseIsSuccess(JSONObject response)
        throws NotSuccessfulResponseException{
        try {
            String code = response.getInt("code") + "";
            if (code.charAt(0) != '2') //code from class 2xx is success
                throw new NotSuccessfulResponseException(
                        String.format("Response not success, actual code is: %s", code));
        } catch (JSONException e) {
            throw new NotSuccessfulResponseException("Response has not Yandex api code", e);
        }
    }
}
