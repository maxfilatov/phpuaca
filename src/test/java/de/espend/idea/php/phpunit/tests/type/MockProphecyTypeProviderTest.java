package de.espend.idea.php.phpunit.tests.type;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.phpunit.tests.PhpUnitLightCodeInsightFixtureTestCase;
import de.espend.idea.php.phpunit.type.MockProphecyTypeProvider;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see MockProphecyTypeProvider
 */
public class MockProphecyTypeProviderTest extends PhpUnitLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit/tests/type/fixtures";
    }

    public void testResolveForPhpunitMock() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
                "$t->createMock(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
                "$t->createMock('Foo')->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );
    }

    public void testResolveForProphecyMock() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
                "$t->prophesize(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );
    }

    public void testResolveForProphecyMockWithStringClass() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
                "$t->prophesize('Foo')->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );
    }

    public void testResolveForProphecyMockWithStringClassWithTrait() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\Prophecy\\PhpUnit\\ProphecyTrait */\n" +
                "$t->prophesize('Foo')->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );
    }
}
