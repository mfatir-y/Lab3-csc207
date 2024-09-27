package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private final Map<String, Map<String, String>> countryMap = new HashMap<>();

    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        try {
            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                Map<String, String> countryTranslations = new HashMap<>();
                for (String keyCode : jsonArray.getJSONObject(i).keySet()) {
                    if (!keyCode.equals("id") && !keyCode.equals("alpha2") && !keyCode.equals("alpha3")) {
                        countryTranslations.put(keyCode, jsonArray.getJSONObject(i).getString(keyCode));
                    }
                }
                countryMap.put(jsonArray.getJSONObject(i).getString("alpha3"), countryTranslations);
            }
        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        Map<String, String> translations = countryMap.get(country);
        return new ArrayList<>(translations.keySet());
    }

    @Override
    public List<String> getCountries() {
        return new ArrayList<>(countryMap.keySet());
    }

    @Override
    public String translate(String country, String language) {
        Map<String, String> translations = countryMap.get(country);
        if (translations.containsKey(language)) {
            return translations.get(language);
        }
        return null;
    }
}
