package de.craftery.craftinghomes.common.i18n;

import de.craftery.craftinghomes.common.Platform;
import de.craftery.craftinghomes.common.api.ConfigurationI;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseI18nProvider {
    protected final Map<String, String> defTranslations = new HashMap<>();
    private ConfigurationI loadedTranslations;

    protected abstract void addAllTranslations();

    public void register() {
        this.addAllTranslations();

        String language = Platform.getServer().getConfiguration().getString("language", "en");

        loadedTranslations = Platform.getServer().getConfiguration(language + ".lang.yml");

        for (Map.Entry<String, String> entry : defTranslations.entrySet()) {
            loadedTranslations.addDefault(entry.getKey(), entry.getValue());
        }

        loadedTranslations.applyDefaults();
    }

    protected String getTranslation(String key) {
        if (loadedTranslations.exists(key)) {
            return loadedTranslations.getString(key, "Not found: " + key);
        }
        return defTranslations.getOrDefault(key, "Not found: " + key);
    }

    protected String replaceString(String input, String needle, String replacement) {
        while (input.contains(needle)) {
            input = input.replace(needle, replacement);
        }
        return input;
    }

    protected String replaceInteger(String input, String needle, Integer replacement) {
        return this.replaceString(input, needle, replacement.toString());
    }
}
