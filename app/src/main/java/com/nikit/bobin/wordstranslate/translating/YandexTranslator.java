package com.nikit.bobin.wordstranslate.translating;

import com.nikit.bobin.wordstranslate.core.Ensure;
import com.nikit.bobin.wordstranslate.functional.Function;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.net.HttpMethod;
import com.nikit.bobin.wordstranslate.net.IHttpSender;

import org.jdeferred.DeferredManager;
import org.jdeferred.DoneFilter;
import org.jdeferred.Promise;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;

import okhttp3.Response;

public class YandexTranslator implements ITranslator {
    private IHttpSender httpSender;

    private String appKey;
    private final String yandexApiPrefix = "https://translate.yandex.net/api/v1.5/tr.json/";


    private DeferredManager deferredManager;
    private ILog log;

    //todo: extract to separated class
    private boolean enableCaching;
    private HashMap<String, HashMap<String, String>> cachedSupportedLanguages;
    private String[] cachedDirections;
    private HashMap<String, String> cachedTranslations;

    public YandexTranslator(
            DeferredManager deferredManager,
            IHttpSender httpSender,
            boolean enableCaching,
            ILog log) {
        this(
                deferredManager,
                httpSender,
                enableCaching,
                log,
                "trnsl.1.1.20170315T155530Z.982270abc72ef811.6b65d3680beb5b85a2f7ee473c7033c589c743a2"
        );
    }

    public YandexTranslator(
            DeferredManager deferredManager,
            IHttpSender httpSender,
            boolean enableCaching,
            ILog log,
            String appKey) {
        Ensure.notNull(deferredManager, "deferredManager");
        Ensure.notNull(httpSender, "httpSender");
        Ensure.notNull(log, "log");
        Ensure.notNullOrEmpty(appKey, "appKey");

        this.deferredManager = deferredManager;
        this.httpSender = httpSender;
        this.enableCaching = enableCaching;
        this.appKey = appKey;
        this.log = log;

        cachedSupportedLanguages = new HashMap<>();
        cachedTranslations = new HashMap<>();
    }

    public Promise<String, Throwable, Void> translateAsync(String word, String direction) {
        Ensure.notNullOrEmpty(word, "word");
        Ensure.notNullOrEmpty(direction, "direction");

        final String translationKey = word + direction;
        if (enableCaching && cachedTranslations.containsKey(translationKey))
            return createPromiseFromResult(cachedTranslations.get(translationKey));

        //todo: ensure success
        //todo: screen special symbols

        return
                executeCallToYandexApi("translate", "lang=" + direction, "text=" + word, new Function<Response, String>() {
                    @Override
                    public String invoke(Response item) {
                        return extractTranslationFromResponse(item);
                    }
                })
                .then(new DoneFilter<String, String>() {
                    @Override
                    public String filterDone(String result) {
                        if (enableCaching)
                            cachedTranslations.put(translationKey, result);
                        return result;
                    }
                });
    }

    //todo: Pair<String, String>[] to separated class
    public Promise<HashMap<String, String>, Throwable, Void> getSupportedLangsAsync(final String uiLang) {
        Ensure.notNullOrEmpty(uiLang, "uiLang");

        if (enableCaching && cachedSupportedLanguages.containsKey(uiLang))
            return createPromiseFromResult(cachedSupportedLanguages.get(uiLang));

        //todo: ensure success
        return
                executeCallToYandexApi("getLangs", "ui=" + uiLang, "", new Function<Response, HashMap<String, String>>() {
                    @Override
                    public HashMap<String, String> invoke(Response item) {
                        return extractSupportedLanguagesFromResponse(item);
                    }
                })
                .then(new DoneFilter<HashMap<String, String>, HashMap<String, String>>() {
                    @Override
                    public HashMap<String, String> filterDone(HashMap<String, String> result) {
                        if (enableCaching)
                            cachedSupportedLanguages.put(uiLang, result);
                        return result;
                    }
                });
    }

    public Promise<String[], Throwable, Void> getSupportedDirectionsAsync() {
        if (enableCaching && cachedDirections != null)
            return createPromiseFromResult(cachedDirections);

        //todo: ensure success
        return
                executeCallToYandexApi("getLangs", null, "", new Function<Response, String[]>() {
                    @Override
                    public String[] invoke(Response item) {
                        return extractDirectionsFromResponse(item);
                    }
                })
                .then(new DoneFilter<String[], String[]>() {
                    @Override
                    public String[] filterDone(String[] result) {
                        if (enableCaching)
                            cachedDirections = result;
                        return result;
                    }
                });
    }

    private <TResult> Promise<TResult, Throwable, Void> executeCallToYandexApi(
            String method,
            String query,
            String body,
            final Function<Response, TResult> responseTransformation) {
        String fullPath = String.format("%s%s?key=%s", yandexApiPrefix, method, appKey);

        if (query != null && !query.equals(""))
            fullPath = String.format("%s&%s", fullPath, query);

        return httpSender
                .sendRequest(fullPath, HttpMethod.POST, body)
                .then(new DoneFilter<Response, TResult>() {
                    @Override
                    public TResult filterDone(Response result) {
                        return responseTransformation.invoke(result);
                    }
                });
    }

    //todo: for this create promise factory
    private <TResult> Promise<TResult, Throwable, Void> createPromiseFromResult(final TResult result) {
        return deferredManager.when(new Callable<TResult>() {
            @Override
            public TResult call() throws Exception {
                return result;
            }
        });
    }

    //todo: extract to separated class
    private String[] extractDirectionsFromResponse(Response response) {
        try {
            JSONObject responseJson = new JSONObject(response.body().string());
            JSONArray dirs = responseJson.getJSONArray("dirs");
            String[] result = new String[dirs.length()];
            for (int i = 0; i < result.length; ++i)
                result[i] = dirs.getString(i);
            return result;
        } catch (Exception e) {
            log.error("Extracting directions from response fails with error: %s", e.getMessage());
            return new String[0];
        }
    }

    //todo: extract to separated class
    private HashMap<String, String> extractSupportedLanguagesFromResponse(Response response) {
        try {
            JSONObject responseJson = new JSONObject(response.body().string());
            JSONObject langs = responseJson.getJSONObject("langs");
            HashMap<String, String> result = new HashMap<>();
            Iterator<String> keys = langs.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                result.put(key, langs.getString(key));
            }

            return result;
        } catch (Exception e) {
            log.error("Extracting supported languages from response fails with error: %s", e.getMessage());
            return new HashMap<>();
        }
    }

    //todo: extract to separated class
    private String extractTranslationFromResponse(Response response) {
        try {
            JSONObject responseJson = new JSONObject(response.body().string());
            JSONArray text = responseJson.getJSONArray("text");
            if (text.length() == 0)
                return "";
            return text.getString(0);
        } catch (Exception e) {
            log.error("Extracting translations from response fails with error: %s", e.getMessage());
            return "";
        }
    }
}
