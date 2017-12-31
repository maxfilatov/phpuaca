package de.espend.idea.php.phpunit.tests.linemarker;

import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.phpunit.tests.PhpUnitLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 *
 * @see de.espend.idea.php.phpunit.linemarker.TestRunLineMarkerProvider
 * @see com.intellij.testIntegration.TestRunLineMarkerProvider
 */
public class TestRunLineMarkerProviderTest extends PhpUnitLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    public void testTestRunLineMarkerForClassIsProvides() {
        PsiFile psiFile = myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase {}"
        );

        assertLineMarker(psiFile, new LineMarker.ToolTipEqualsAssert("Run Test"));
    }

    public void testTestRunLineMarkerForMethodIsProvides() {
        Method method = PhpPsiElementFactory.createFromText(getProject(), Method.class, "<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "  public function testFoo() {}" +
            "}"
        );

        assertNotNull(method);

        assertLineMarker(method.getParent(), markerInfo ->
            markerInfo.getElement().getParent() instanceof Method && "Run Test".equals(markerInfo.getLineMarkerTooltip())
        );
    }
}
