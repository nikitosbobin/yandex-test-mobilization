package com.nikit.bobin.wordstranslate.translating;

import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.translating.exceptions.NotSuccessfulResponseException;
import com.nikit.bobin.wordstranslate.translating.exceptions.ResponseHasNotTargetDataException;
import com.nikit.bobin.wordstranslate.translating.models.Direction;
import com.nikit.bobin.wordstranslate.translating.models.Language;
import com.nikit.bobin.wordstranslate.translating.models.Translation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Response;

public class YandexResponseExtractor implements IYandexResponseExtractor {

    @Override
    public Language[] extractSupportedLanguages(Response response)
            throws ResponseHasNotTargetDataException, NotSuccessfulResponseException {
        Ensure.notNull(response, "response");
        Ensure.okHttpResponseIsSuccess(response, "response");

        Pattern pattern = Pattern.compile("^.*ui=([a-z]+).*$");
        try {
            JSONObject responseBody = new JSONObject(response.body().string());
            ensureYandexApiResponseIsSuccess(responseBody);

            JSONObject langs = responseBody.getJSONObject("langs");
            ArrayList<Language> result = new ArrayList<>(16);
            Iterator<String> keys = langs.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                Language currentLang = new Language(key);
                Matcher matcher = pattern.matcher(response.request().url().toString());
                if (matcher.matches()) {
                    String title = langs.getString(key);
                    currentLang.addTitle(new Language(matcher.group(1)), title);
                }
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
    public Direction[] extractSupportedDirections(Response response)
            throws ResponseHasNotTargetDataException, NotSuccessfulResponseException {
        Ensure.notNull(response, "response");
        Ensure.okHttpResponseIsSuccess(response, "response");

        try {
            JSONObject responseBody = new JSONObject(response.body().string());
            ensureYandexApiResponseIsSuccess(responseBody);

            JSONArray dirs = responseBody.getJSONArray("dirs");
            Direction[] result = new Direction[dirs.length()];
            for (int i = 0; i < result.length; ++i)
                result[i] = Direction.parse(dirs.getString(i));
            return result;
        } catch (JSONException e) {
            throw new ResponseHasNotTargetDataException("Response body could not convert to JSONObject", e);
        } catch (IOException e) {
            throw new ResponseHasNotTargetDataException("Response body has IOException", e);
        }
    }

    @Override
    public Translation extractTranslation(Response response)
            throws ResponseHasNotTargetDataException, NotSuccessfulResponseException {
        Ensure.notNull(response, "response");
        Ensure.okHttpResponseIsSuccess(response, "response");

        try {
            JSONObject responseBody = new JSONObject(response.body().string());
            ensureYandexApiResponseIsSuccess(responseBody);

            JSONArray text = responseBody.getJSONArray("text");
            if (text.length() == 0)
                return null;
            String lang = responseBody.getString("lang");
            return new Translation(text.getString(0), Direction.parse(lang));
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
}
