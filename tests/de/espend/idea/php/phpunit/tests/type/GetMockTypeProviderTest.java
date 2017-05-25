package de.espend.idea.php.phpunit.tests.type;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.phpunit.tests.PhpUnitLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.type.GetMockTypeProvider
 */
public class GetMockTypeProviderTest extends PhpUnitLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("GetMockBuilderTypeProvider.php");
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    public void testGetMockGeneratorProvidesNavigation() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "/** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
                "$x = $t->getMockBuilder('Foo')" +
                "->getMock()" +
                "$x->b<caret>ar();\n",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );
    }

    public void testChainMockGeneratorProvidesNavigation() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "/** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
            "$x = $t->getMockBuilder('Foo')" +
            "->disableOriginalConstructor()" +
            "->disableOriginalConstructor()" +
            "->getMock()" +
            "$x->b<caret>ar();\n",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );
    }
}
