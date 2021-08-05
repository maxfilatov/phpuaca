package de.espend.idea.php.phpunit.linemarker;

import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.linemarker.RelatedTestCaseLineMarkerProvider
 */
public class RelatedTestCaseLineMarkerProviderTest extends PhpUnitLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("RelatedTestCaseLineMarkerProvider.php");
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit/linemarker/fixtures";
    }

    public void testThatClassNameProvidesALineMarkerToItsTestCase() {
        PsiFile psiFileFromText = PhpPsiElementFactory.createPsiFileFromText(getProject(), "<?php\n" +
            "namespace Foo\\Bar\\Car;\n" +
            "class Foobar {}\n"
        );

        assertLineMarker(psiFileFromText, new LineMarker.ToolTipEqualsAssert("Navigate to Test Class"));
    }
}
