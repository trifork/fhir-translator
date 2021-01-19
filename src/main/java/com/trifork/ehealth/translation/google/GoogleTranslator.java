package com.trifork.ehealth.translation.google;

import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.trifork.ehealth.translation.exceptions.TranslatorException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GoogleTranslator {
    private final Translate translate;
    private final List<Language> supportedLanguages;

    public GoogleTranslator() {
        translate = TranslateOptions.getDefaultInstance().getService();
        supportedLanguages = translate.listSupportedLanguages(Translate.LanguageListOption.targetLanguage("en"));
    }

    public String translate(String englishText, String targetLanguage) {
        if (supportedLanguages.stream().noneMatch(lang -> lang.getCode().equalsIgnoreCase(targetLanguage))) {
            throw new TranslatorException("Unsupported targetLanguage. Supported languages are: " + supportedLanguages.stream().map(Language::toString).collect(Collectors.joining(", ")));
        }

        Translation translation = translate.translate(
                englishText,
                Translate.TranslateOption.sourceLanguage("en"),
                Translate.TranslateOption.targetLanguage(targetLanguage));

        return translation.getTranslatedText();
    }

}
