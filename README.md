# FHIR Translator
Executable jar that translates input FHIR resources using Google Cloud Translation and outputs them in serialized form (CodeSystems in xml files) 

## Currently Supports
- DSTU3
- CodeSystem (Concepts)
- English (`en`) source language

## CodeSystem Concept Translation
CodeSystem Concepts are translated by their `display` text by adding a `designation` property to the Concept containing a `language` property containing the translation `target_language` and `value` containing the output of the translation.  
### Specifying CodeSystems to Translate
- The input CodeSystems to translate is determined from the ValueSets specified in [application.yaml](src/main/resources/application.yaml).  
- Each ValueSet is looked up on http://tx.fhir.org and all CodeSystems that has concepts included in the ValueSet are selected for translation.

## To Run
Requires Java 11 JRE
- Run `mvn package` to build the jar, or download the jar from [releases](releases)
- Run `java -DGOOGLE_API_KEY=<your_key> -jar fhir-translator.jar --spring.config.location=your-application.yaml`
  - Use `--spring.config.location` to provide your own values for application.yaml if needed
