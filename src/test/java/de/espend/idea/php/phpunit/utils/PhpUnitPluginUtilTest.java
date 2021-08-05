package de.espend.idea.php.phpunit.utils;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.Function;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpUnitPluginUtilTest extends PhpUnitLightCodeInsightFixtureTestCase {

    public void testIsTestClassWithoutIndexAccess() {
        assertTrue(PhpUnitPluginUtil.isTestClassWithoutIndexAccess(
            PhpPsiElementFactory.createFromText(getProject(), PhpClass.class, "<?php class FooTest {}")
        ));

        assertTrue(PhpUnitPluginUtil.isTestClassWithoutIndexAccess(
            PhpPsiElementFactory.createFromText(getProject(), PhpClass.class, "<?php class FooTest extends \\PHPUnit\\Framework\\TestCase {}")
        ));

        assertTrue(PhpUnitPluginUtil.isTestClassWithoutIndexAccess(
            PhpPsiElementFactory.createFromText(getProject(), PhpClass.class, "<?php class FooTest extends PHPUnit_Framework_TestCase {}")
        ));

        assertTrue(PhpUnitPluginUtil.isTestClassWithoutIndexAccess(
            PhpPsiElementFactory.createFromText(getProject(), PhpClass.class, "<?php class FooTest extends \\Symfony\\Bundle\\FrameworkBundle\\Test\\WebTestCase {}")
        ));
    }

    public void testThatInsertExpectedExceptionForNonExistingDocBlock() {
        PsiFile psiFile = myFixture.configureByText("test.php", "<?php\n" +
            " function test()" +
            " {" +
            " }"
        );

        Document document = PsiDocumentManager.getInstance(getProject()).getDocument(psiFile);
        Function function = PsiTreeUtil.findChildOfType(psiFile, Function.class);

        new WriteCommandAction.Simple(getProject(), "PHPUnit: expectedException Insert") {
            @Override
            protected void run() {
                PhpUnitPluginUtil.insertExpectedException(document, function, "Foobar\\Foobar");
            }
        }.execute();

        assertTrue(psiFile.getText().contains("@expectedException \\Foobar\\Foobar"));
    }

    public void testInsertExpectedExceptionForDocBlockUpdate() {
        PsiFile psiFile = myFixture.configureByText("test.php", "<?php\n" +
            "/**\n" +
            " * @Foo\n" +
            " * @return Foo\n" +
            " */\n" +
            "function test()\n" +
            "{\n" +
            "}\n"
        );

        Document document = PsiDocumentManager.getInstance(getProject()).getDocument(psiFile);
        Function function = PsiTreeUtil.findChildOfType(psiFile, Function.class);

        new WriteCommandAction.Simple(getProject(), "PHPUnit: expectedException Insert") {
            @Override
            protected void run() {
                PhpUnitPluginUtil.insertExpectedException(document, function, "Foobar\\Foobar");
            }
        }.execute();

        assertTrue(psiFile.getText().contains("@expectedException \\Foobar\\Foobar"));
    }
}
