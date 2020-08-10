package com.phpuaca.tests.reference;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.phpunit.tests.PhpUnitLightCodeInsightFixtureTestCase;

public class StringReferenceContributorTest extends PhpUnitLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("StringReferenceContributor.php");
    }

    public String getTestDataPath() {
        return "src/test/java/com/phpuaca/tests/reference/fixtures";
    }

    public void testThatReferencesForClassMethodAreProvided() {
        assertReferencesMatch(PhpFileType.INSTANCE, "<?php\n" +
                        "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                        "{\n" +
                        "" +
                        "   public function foobar()\n" +
                        "   {\n" +
                        "       $this->getMockBuilder(\\Foo\\Bar::class)\n" +
                        "           ->setMethods(['getFoo<caret>bar'])\n" +
                        "           ->getMock();\n" +
                        "   }\n" +
                        "}",
                PlatformPatterns.psiElement(Method.class).withName("getFoobar")
        );

        assertReferencesMatch(PhpFileType.INSTANCE, "<?php\n" +
                        "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                        "{\n" +
                        "" +
                        "   public function foobar()\n" +
                        "   {\n" +
                        "       $this->getMockBuilder('\\Foo\\Bar')\n" +
                        "           ->onlyMethods(['getFoo<caret>bar'])\n" +
                        "           ->getMock();\n" +
                        "   }\n" +
                        "}",
                PlatformPatterns.psiElement(Method.class).withName("getFoobar")
        );

        assertReferencesMatch(PhpFileType.INSTANCE, "<?php\n" +
                        "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                        "{\n" +
                        "" +
                        "   public function foobar()\n" +
                        "   {\n" +
                        "       $this->getMockBuilder('\\Foo\\Bar')\n" +
                        "           ->addMethods(['getFoo<caret>bar'])\n" +
                        "           ->getMock();\n" +
                        "   }\n" +
                        "}",
                PlatformPatterns.psiElement(Method.class).withName("getFoobar")
        );
    }
}
