# FHIR Translator
Executable jar that translates input FHIR resources using Google Cloud Translation and outputs them in serialized form (xml files) 

## Currently Supports
- DSTU3
- CodeSystem (Concepts)
- English (`en`) source language

## CodeSystem Concept Translation
CodeSystem Concepts are translated by their `display` text by adding a `designation` property to the Concept containing a `language` property containing the translation `target_language` and `value` containing the output of the translation. 

## To Run
- `mvn package`
- `java -DGOOGLE_API_KEY=<your_key> -jar fhir-translator.jar --spring.config.location=your-application.yaml`
  - Use `--spring.config.location` to provide your own values for application.yaml if needed
