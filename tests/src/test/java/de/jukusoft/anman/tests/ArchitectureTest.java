package de.jukusoft.anman.tests;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.slf4j.Logger;
import org.springframework.data.repository.CrudRepository;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

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
			.should().haveSimpleNameEndingWith("DAO")
			.orShould().haveSimpleNameEndingWith("DAOImpl");

	// verify that logger fields are private, static and final, see also: https://www.javacodegeeks.com/2020/02/validating-code-and-architecture-constraints-with-archunit.html
	@ArchTest
	private final ArchRule loggers_should_be_private_static_final = fields()
			.that().haveRawType(Logger.class)
			.should().bePrivate()
			.orShould().beProtected()
			.andShould().beStatic()
			.andShould().beFinal();

	//see also: https://medium.com/free-code-camp/java-archunit-testing-the-architecture-a09f089585be
	@ArchTest
	public static final ArchRule rule = slices().matching("..utils.(*)..")
			.should().beFreeOfCycles();

}
