package com.nikit.bobin.wordstranslate.translating;

import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.translating.exceptions.NotSuccessfulResponseException;
import com.nikit.bobin.wordstranslate.translating.exceptions.ResponseHasNotTargetDataException;
import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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

            if (responseBody.has("dirs"))
                fillDirections(result, responseBody.getJSONArray("dirs"));

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

    private void fillDirections(ArrayList<Language> languages, JSONArray dirs) throws JSONException {
        HashMap<String, Language> langsTable = new HashMap<>();
        for (Language l : languages)
            langsTable.put(l.getKey(), l);

        for (int i = 0; i < dirs.length(); ++i) {
            String dir = dirs.getString(i);
            Direction direction = Direction.parse(dir);
            String from = direction.getFrom().toString();
            if (langsTable.containsKey(from))
                langsTable.get(from).addDirection(direction.getTo());
        }
    }
}
