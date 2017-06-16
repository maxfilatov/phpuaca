package de.espend.idea.php.phpunit.tests.utils;

import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.phpunit.tests.PhpUnitLightCodeInsightFixtureTestCase;
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil;

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
}
