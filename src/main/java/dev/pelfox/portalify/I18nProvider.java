package dev.pelfox.portalify;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationStore;
import net.kyori.adventure.util.UTF8ResourceBundleControl;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18nProvider {
    public static final String LOCALIZATION_BASE_NAME = "lang.Bundle";
    public static final Key LOCALIZATION_STORE_KEY = Key.key("portalify:translations");
    public static final List<Locale> LOCALES = List.of(Locale.US, Locale.of("ru"));

    public static void initializeI18n() {
        TranslationStore.StringBased<MessageFormat> store = TranslationStore.messageFormat(LOCALIZATION_STORE_KEY);
        for (Locale locale : LOCALES) {
            ResourceBundle bundle = ResourceBundle.getBundle(LOCALIZATION_BASE_NAME, locale, UTF8ResourceBundleControl.get());
            store.registerAll(locale, bundle, true);
        }
        GlobalTranslator.translator().addSource(store);
    }
}
