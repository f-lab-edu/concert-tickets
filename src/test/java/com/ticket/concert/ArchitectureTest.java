package com.ticket.concert;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleNameEndingWith;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleNameStartingWith;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(
        packages = "com.ticket.concert",
        importOptions = ImportOption.DoNotIncludeTests.class
)
public class ArchitectureTest {

    /**
     * 계층 의존성 규칙
     * presentation → application → domain ← infrastructure
     */
    @ArchTest
    static final ArchRule layered_architecture_is_respected = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Presentation").definedBy("..presentation..")
            .layer("Application").definedBy("..application..")
            .layer("Domain").definedBy("..domain..")
            .layer("Infrastructure").definedBy("..infrastructure..")

            .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Presentation")
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
            .whereLayer("Domain").mayOnlyBeAccessedByLayers(
                    "Presentation", "Application", "Infrastructure"
            );

    /**
     * Domain은 외부 계층(presentation, application, infrastructure)에 절대 의존하지 않아야 함
     */
    @ArchTest
    static final ArchRule domain_should_not_depend_on_outer_layers = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..presentation..", "..application..", "..infrastructure..");

    /**
     * Controller는 presentation 패키지에만 존재해야 함
     */
    @ArchTest
    static final ArchRule controllers_should_be_in_presentation = classes()
            .that().haveSimpleNameEndingWith("Controller")
            .should().resideInAPackage("..presentation..");

    /**
     * Service는 application 패키지에만 존재해야 함
     */
    @ArchTest
    static final ArchRule services_should_be_in_application = classes()
            .that().haveSimpleNameEndingWith("Service")
            .should().resideInAPackage("..application..");

    /**
     * Repository 구현체는 infrastructure 패키지에만 존재해야 함
     */
    @ArchTest
    static final ArchRule repository_impls_should_be_in_infrastructure = classes()
            .that(
                    simpleNameEndingWith("RepositoryImpl")
                            .or(simpleNameStartingWith("Jpa").and(simpleNameEndingWith("Repository")))
                            .or(simpleNameStartingWith("Jdbc").and(simpleNameEndingWith("Repository")))
            )
            .should().resideInAPackage("..infrastructure..");

    /**
     * 패키지 간 순환 의존성 금지
     */
    @ArchTest
    static final ArchRule no_cyclic_dependencies = slices()
            .matching("com.ticket.concert.(*)..")
            .should().beFreeOfCycles();

}
