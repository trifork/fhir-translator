package com.trifork.ehealth.translation.fhir.dstu3;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.util.BundleUtil;
import com.trifork.ehealth.translation.TranslationRunner;
import com.trifork.ehealth.translation.TranslatorProperties;
import com.trifork.ehealth.translation.fhir.annotations.OnDSTU3Condition;
import com.trifork.ehealth.translation.google.GoogleTranslator;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@Conditional(OnDSTU3Condition.class)
public class TranslationRunnerDstu3 implements TranslationRunner {
    private static final Logger logger = LoggerFactory.getLogger(TranslationRunnerDstu3.class);
    private static final String OUT_DIR = "out";
    private static final FhirContext fhirContext = FhirContext.forDstu3();
    private static final IParser parser = fhirContext.newXmlParser().setPrettyPrint(true);
    private final TranslatorProperties translatorProperties;
    private final GoogleTranslator googleTranslator;

    @Autowired
    public TranslationRunnerDstu3(GoogleTranslator googleTranslator, TranslatorProperties translatorProperties) {
        this.googleTranslator = googleTranslator;
        this.translatorProperties = translatorProperties;
    }

    @Override
    public void execute() throws IOException {
        var client = fhirContext.newRestfulGenericClient("http://tx.fhir.org/r3");
        var uniqueCodeSystems = new HashSet<String>();
        logger.info("Processing ValueSets {}", translatorProperties.getValuesets());
        for (String valueSetUrl : translatorProperties.getValuesets()) {
            var bundle = client.search().byUrl("ValueSet?url=" + valueSetUrl).returnBundle(Bundle.class).execute();
            var valueSet = BundleUtil.toListOfResourcesOfType(fhirContext, bundle, ValueSet.class).get(0);
            logger.debug("Looking up CodeSystems used by ValueSet {}", valueSet.getUrl());
            Set<String> usedCodeSystems = valueSet.getCompose().getInclude().stream().map(ValueSet.ConceptSetComponent::getSystem).collect(Collectors.toSet());
            logger.debug("Found {}", usedCodeSystems);
            uniqueCodeSystems.addAll(usedCodeSystems);
        }
        logger.info("Total CodeSystems found: {}", uniqueCodeSystems);

        prepareOutDirectory();
        for (String codeSystemUrl : uniqueCodeSystems) {
            logger.info("Fetching CodeSystem: {}", codeSystemUrl);
            var bundle = client.search().byUrl("CodeSystem?url=" + codeSystemUrl).returnBundle(Bundle.class).execute();
            var codeSystems = BundleUtil.toListOfResourcesOfType(fhirContext, bundle, CodeSystem.class);
            if (codeSystems.size() > 1) {
                throw new IllegalStateException("More than one CodeSystem found with url '" + codeSystemUrl + "': " + codeSystems);
            }
            if (codeSystems.isEmpty()) {
                logger.warn("CodeSystem not found: {} - SKIPPING!", codeSystemUrl);
                continue;
            }
            var codeSystem = codeSystems.get(0);
            if (codeSystem.getConcept().isEmpty()) {
                logger.warn("CodeSystem {} has no concepts - SKIPPING!", codeSystemUrl);
            } else {
                logger.debug("Nr of CodeSystem concepts: {}", codeSystem.getConcept().size());
                addGoogleTranslation(codeSystem, translatorProperties.getTargetLanguage());
                writeCodeSystemToFile(codeSystem);
            }
        }
    }

    public void addGoogleTranslation(CodeSystem codeSystem, String language) {
        for (CodeSystem.ConceptDefinitionComponent concept : codeSystem.getConcept()) {
            if (concept.hasDisplay()) {
                String translation = googleTranslator.translate(concept.getDisplay(), language);
                logger.debug("Translated concept '{}' with Display text '{}' into '{}'", concept.getCode(), concept.getDisplay(), translation);
                concept.addDesignation(
                        new CodeSystem.ConceptDefinitionDesignationComponent()
                                .setLanguage(language)
                                .setValue(translation)
                                .setUse(new Coding()
                                        .setSystem("http://snomed.info/sct")
                                        //See http://hl7.org/fhir/STU3/valueset-designation-use.html - eg the following:
                                        .setCode("900000000000013009")));
            } else
                logger.debug("SKIPPING Concept with no Display text: {}", concept.getCode());
        }
    }

    public static void prepareOutDirectory() throws IOException {
        Files.createDirectories(Path.of(OUT_DIR));
        try (Stream<Path> walk = Files.walk(Path.of(OUT_DIR))) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .peek(file -> logger.debug("Deleting file {}", file.getPath()))
                    .forEach(File::delete);
        }
    }

    public static void writeCodeSystemToFile(CodeSystem codeSystem) throws IOException {
        String codeSystemString = parser.encodeResourceToString(codeSystem);
        logger.debug("Translated CodeSystem: {}", codeSystemString);
        String fileName = codeSystem.getName() + ".xml";
        logger.info("Writing '{}'", fileName);
        Files.writeString(Path.of(OUT_DIR + "/" + fileName), codeSystemString, UTF_8);
    }

}
