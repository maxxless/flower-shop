package com.team.flowershop

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchTest {

    @Test
    fun servicesAndRepositoriesShouldNotDependOnWebLayer() {

        val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.team.flowershop")

        noClasses()
            .that()
                .resideInAnyPackage("com.team.flowershop.service..")
            .or()
                .resideInAnyPackage("com.team.flowershop.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..com.team.flowershop.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses)
    }
}
