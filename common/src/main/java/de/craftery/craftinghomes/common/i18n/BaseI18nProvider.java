package de.craftery.craftinghomes.common.i18n;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseI18nProvider {
    protected final Map<String, String> defTranslations = new HashMap<>();

    protected abstract void addAllTranslations();

    public void register() {
        this.addAllTranslations();

        // TODO: save default translations to configured file
    }

    protected String getTranslation(String key) {
        // TODO: get translation from config
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
