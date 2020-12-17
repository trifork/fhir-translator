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
public class Translator {

    private final Translate translate;
    private final List<Language> languages;

    public Translator() {
        translate = TranslateOptions.getDefaultInstance().getService();
        languages = translate.listSupportedLanguages(Translate.LanguageListOption.targetLanguage("en"));
    }

    private String translate(String text, String language) {

        boolean supportedLanguage = languages.stream().anyMatch(l -> l.getCode().equalsIgnoreCase(language));
        if (!supportedLanguage) {
            throw new RuntimeException("Unsupported language. Supported languages are: " + languages.stream().map(Language::toString).collect(Collectors.joining(", ")));
        }


        Translation translation = translate.translate(
                text,
                Translate.TranslateOption.sourceLanguage("en"),
                Translate.TranslateOption.targetLanguage(language));

        return translation.getTranslatedText();
    }

    public org.hl7.fhir.dstu3.model.CodeSystem addDesignation(org.hl7.fhir.dstu3.model.CodeSystem codeSystem, String language) {
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
