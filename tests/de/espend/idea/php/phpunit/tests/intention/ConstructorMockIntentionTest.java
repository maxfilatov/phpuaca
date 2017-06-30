package de.espend.idea.php.phpunit.tests.intention;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.PhpFileType;
import de.espend.idea.php.phpunit.intention.ConstructorMockIntention;
import de.espend.idea.php.phpunit.tests.PhpUnitLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 *
 * @see de.espend.idea.php.phpunit.intention.ConstructorMockIntention
 */
public class ConstructorMockIntentionTest extends PhpUnitLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("ConstructorMockIntention.php");
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    public void testThatMockIsCreatedForEmptyConstructor() {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" +
            "new \\Foo\\<caret>Bar();"
        );

        String text = invokeAndGetText();

        assertEquals(
            "<?php\n" +
                "use Bar\\Car;\n" +
                "use Bar\\Foo;\n" +
                "\n" +
                "new \\Foo\\Bar($this->createMock(Foo::class), $this->createMock(Car::class));",
            text
        );
    }

    public void testThatMockIsCreatedForEmptyConstructorWithParameter() {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" +
            "new \\Foo\\<caret>BarNext($this->createMock(Foo::class));"
        );

        String text = invokeAndGetText();

        assertEquals(
            "<?php\n" +
                "use Bar\\Car;\n" +
                "\n" +
                "new \\Foo\\BarNext($this->createMock(Foo::class), $this->createMock(Car::class), $this->createMock(Car::class), $this->createMock(Car::class));",
            text
        );
    }

    public void testThatIntentionIsAvaibleForConstructorContext() {
        assertIntentionIsAvailable(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            $foo = new Fo<caret>obar()\n" +
                "        }\n" +
                "    }",
            "PHPUnit: Add constructor mocks"
        );
    }

    private String invokeAndGetText() {
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        new ConstructorMockIntention().invoke(getProject(), getEditor(), psiElement);

        return psiElement.getContainingFile().getText();
    }
}
