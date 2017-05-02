package de.espend.idea.php.phpunit.tests.type;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.phpunit.tests.PhpUnitLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.type.ProphecyTypeProvider
 */
public class ProphecyTypeProviderTest extends PhpUnitLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("ProphecyTypeProvider.php");
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    public void testThatProphesizeForVariableIsResolved() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
            "    {\n" +
            "        public function testFoobar()\n" +
            "        {\n" +
            "            $foo = $this->prophesize(Foo::class);\n" +
            "            $foo->find()->will<caret>Return();\n" +
            "        }\n" +
            "    }",
            PlatformPatterns.psiElement(Method.class).withName("willReturn")
        );
    }

    public void testThatProphesizeForMethodReferenceIsResolved() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            $foo = $this->prophesize(Foo::class)->find()->will<caret>Return();\n" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement(Method.class).withName("willReturn")
        );
    }
}
