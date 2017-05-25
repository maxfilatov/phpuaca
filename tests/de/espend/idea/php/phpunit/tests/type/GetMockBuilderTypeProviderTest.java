package de.espend.idea.php.phpunit.tests.type;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import de.espend.idea.php.phpunit.tests.PhpUnitLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.type.GetMockBuilderTypeProvider
 */
public class GetMockBuilderTypeProviderTest extends PhpUnitLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("GetMockBuilderTypeProvider.php");
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    public void testResolveForPhpunitMock() {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" +
            "/** @var $x \\PHPUnit\\Framework\\TestCase */\n" +
            "$t = $x->getMockBuilder(\\Foo::class)->getMock();\n" +
            "$t->b<caret>ar();\n"
        );

        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        MethodReference parent = (MethodReference) psiElement.getParent();

        assertTrue(parent.getType().getTypes().stream().anyMatch(s -> s.contains("\\Foo.class")));
    }
}
