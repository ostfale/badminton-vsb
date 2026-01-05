package de.ostfale.va.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Hexagonal Architecture Tests")
public class HexagonalArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("de.ostfale.va");

    @Test
    @DisplayName("Hexagonal Architecture should be respected")
    void hexagonal_architecture_should_be_respected() {
        Architectures.layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Domain Model").definedBy("..application.domain.model..")
                .layer("Domain Service").definedBy("..application.domain.service..")
                .layer("Ports").definedBy("..application.port..")
                .layer("Framework In").definedBy("..framework.in..")
                .layer("Framework Out").definedBy("..framework.out..")

                .whereLayer("Domain Model").mayNotAccessAnyLayer()
                .whereLayer("Domain Service").mayOnlyAccessLayers("Domain Model", "Ports")
                .whereLayer("Ports").mayOnlyAccessLayers("Domain Model")
                .whereLayer("Framework In").mayOnlyAccessLayers("Ports", "Domain Model")
                .whereLayer("Framework Out").mayOnlyAccessLayers("Ports", "Domain Model")
                .check(classes);
    }

    @Test
    @DisplayName("Domain should not depend on framework")
    void domain_should_not_depend_on_framework() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..application.domain..")
                .should().dependOnClassesThat().resideInAnyPackage("..framework..", "com.vaadin..", "org.springframework.web..", "org.springframework.data..")
                .because("Domain should not depend on framework or UI libraries")
                .check(classes);
    }

    @Test
    @DisplayName("Ports should be interfaces")
    void ports_should_be_interfaces() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..application.port..")
                .should().beInterfaces()
                .check(classes);
    }

    @Test
    @DisplayName("Domain should only use spring stereotypes")
    void domain_should_only_use_spring_stereotypes_not_framework() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..application.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework.web..",
                        "org.springframework.data..",
                        "org.springframework.boot..",
                        "org.springframework.context..")
                .because("Domain may use @Service but should not depend on Spring framework specifics")
                .check(classes);
    }

    @Test
    @DisplayName("Adapters should not call each other")
    void adapters_should_not_call_each_other() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..framework.in..")
                .should().dependOnClassesThat().resideInAPackage("..framework.out..")
                .because("Adapters should only communicate through the domain and ports, and specifically 'In' adapters should not depend on 'Out' adapters")
                .check(classes);
    }
}
