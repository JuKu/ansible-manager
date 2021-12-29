package de.jukusoft.anman.tests;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.data.repository.CrudRepository;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * @author Justin Kuenzel
 */
@AnalyzeClasses(packages = "com.jukusoft.anman")
public class ArchitectureTest {

	@ArchTest
	public static final ArchRule serviceDependencyRule = classes()
			.that().resideInAPackage("..service..")
			.should().onlyBeAccessed().byAnyPackage("..controller..", "..service..");

	@ArchTest
	public static final ArchRule utilsClassesShouldNotAccessConfigClassesRule = noClasses().that().resideInAPackage("..utils..")
			.should().dependOnClassesThat().resideInAPackage("..config..");

	@ArchTest
	public static final ArchRule controllerShouldNotAccessDAOsRule = noClasses().that().haveNameMatching(".*Controller")
			.should().dependOnClassesThat().haveNameMatching(".*DAO");

	/*@ArchTest
	public static final ArchRule servicesShouldOnlyBeAccessedByOtherServicesOrController = classes()
			.that().haveNameMatching(".*Service")
			.should().onlyBeAccessed().byClassesThat().haveNameMatching(".*Service");
	//.orShould().onlyBeAccessed().byClassesThat().haveNameMatching(".*Controller");

	@ArchTest
	public static final ArchRule daosShouldOnlyBeAccessedByServices = classes()
			.that().haveNameMatching(".*DAO")
			.should().onlyBeAccessed().byClassesThat().haveNameMatching(".*DAO")
			.orShould().onlyBeAccessed().byClassesThat().haveNameMatching(".*Service")
			.orShould().onlyBeAccessed().byClassesThat().haveNameMatching(".*Impl")
			.orShould().onlyBeAccessed().byClassesThat().haveNameMatching(".*Provider")
			.orShould().onlyBeAccessed().byClassesThat().haveNameMatching(".*Importer");*/

	@ArchTest
	public static final ArchRule daosShouldEndWithDAOSuffix = classes().that().implement(CrudRepository.class)
			.should().haveSimpleNameContaining("DAO");

}
