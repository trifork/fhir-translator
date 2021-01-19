package com.trifork.ehealth.translation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "translator")
public class TranslatorProperties {
    private List<String> valuesets;
    private String fhirVersion;
    private String targetLanguage;

    public List<String> getValuesets() {
        return valuesets;
    }

    public void setValuesets(List<String> valuesets) {
        this.valuesets = valuesets;
    }

    public String getFhirVersion() {
        return fhirVersion;
    }

    public void setFhirVersion(String fhirVersion) {
        this.fhirVersion = fhirVersion;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }
}
