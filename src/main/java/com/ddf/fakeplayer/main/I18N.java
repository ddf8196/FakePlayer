package com.ddf.fakeplayer.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class I18N {
    private static final Locale DEFAULT_LOCALE = Locale.CHINA;
    private static final List<Locale> LOCALES = Arrays.asList(Locale.CHINA, Locale.US);
    private static final Map<Locale, ResourceBundle> LOCALE_MAP = new LinkedHashMap<>();
    private static final ResourceBundle FALLBACK_BUNDLE;

    private static Locale currentLocale = DEFAULT_LOCALE;
    private static ResourceBundle currentBundle;

    static {
        ResourceBundle.Control control = new ResourceBundle.Control() {
            @Override
            public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
                String bundleName = toBundleName(baseName, locale);
                String resourceName = toResourceName(bundleName, "properties");
                InputStream is = loader.getResourceAsStream(resourceName);
                return new PropertyResourceBundle(new InputStreamReader(is, StandardCharsets.UTF_8));
            }
        };

        for (Locale locale : LOCALES) {
            try {
                ResourceBundle bundle = ResourceBundle.getBundle("i18n/Messages", locale, control);
                LOCALE_MAP.put(locale, bundle);
            } catch (Throwable t) {
                if (locale.equals(DEFAULT_LOCALE)) {
                    throw new RuntimeException(t);
                }
            }
        }

        FALLBACK_BUNDLE = LOCALE_MAP.get(DEFAULT_LOCALE);
        currentBundle = LOCALE_MAP.get(currentLocale);
    }

    public static String get(String key) {
        try {
            return currentBundle.getString(key);
        } catch (Throwable t) {
            return FALLBACK_BUNDLE.getString(key);
        }
    }

    public static String get(String key, Locale locale) {
        ResourceBundle bundle = LOCALE_MAP.get(locale);
        if (bundle != null) {
            try {
                return bundle.getString(key);
            } catch (Throwable ignored) {}
        }
        return FALLBACK_BUNDLE.getString(key);
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static void setCurrentLocale(Locale locale) {
        currentLocale = locale;
        ResourceBundle bundle = LOCALE_MAP.get(locale);
        if (bundle == null)
            bundle = LOCALE_MAP.get(DEFAULT_LOCALE);
        currentBundle = bundle;
    }
}
