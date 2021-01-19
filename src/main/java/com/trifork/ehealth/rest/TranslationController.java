package com.trifork.ehealth.rest;

import ca.uhn.fhir.context.FhirContext;
import com.trifork.ehealth.fhir.dstu3.CodeSystemResourceProvider;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranslationController {
    private final CodeSystemResourceProvider dstu3CodeSystemResourceProvider;

    public TranslationController(CodeSystemResourceProvider dstu3CodeSystemResourceProvider)
    {
        this.dstu3CodeSystemResourceProvider = dstu3CodeSystemResourceProvider;
    }

    @PostMapping("/translateDSTU3")
    public String translateDSTU3(@RequestBody String codeSystem, @RequestParam String language) {
        var context= FhirContext.forDstu3();
        var codesystem = context.newXmlParser().parseResource(CodeSystem.class, codeSystem);
        return context.newXmlParser().encodeResourceToString(this.dstu3CodeSystemResourceProvider.translate(codesystem, language));
    }

}
