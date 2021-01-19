package com.trifork.ehealth.google;

import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.Coding;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TranslatorStu3 {
    private final Translate translate;
    private final List<Language> supportedLanguages;

    public TranslatorStu3() {
        translate = TranslateOptions.getDefaultInstance().getService();
        supportedLanguages = translate.listSupportedLanguages(Translate.LanguageListOption.targetLanguage("en"));
    }

    private String translate(String englishText, String targetLanguage) {
        if (supportedLanguages.stream().noneMatch(lang -> lang.getCode().equalsIgnoreCase(targetLanguage))) {
            throw new RuntimeException("Unsupported targetLanguage. Supported languages are: " + supportedLanguages.stream().map(Language::toString).collect(Collectors.joining(", ")));
        }

        Translation translation = translate.translate(
                englishText,
                Translate.TranslateOption.sourceLanguage("en"),
                Translate.TranslateOption.targetLanguage(targetLanguage));

        return translation.getTranslatedText();
    }

    public CodeSystem addDesignation(CodeSystem codeSystem, String language) {
        for (CodeSystem.ConceptDefinitionComponent concept : codeSystem.getConcept()) {
            if (concept.hasDisplay()) {
                concept.addDesignation(
                        new CodeSystem.ConceptDefinitionDesignationComponent()
                                .setLanguage(language)
                                .setValue(translate(concept.getDisplay(), language))
                                .setUse(new Coding()
                                        .setSystem("http://snomed.info/sct")
                                        //See http://hl7.org/fhir/STU3/valueset-designation-use.html - eg the following:
                                        .setCode("900000000000013009")));
            }
        }
        return codeSystem;
    }
}
