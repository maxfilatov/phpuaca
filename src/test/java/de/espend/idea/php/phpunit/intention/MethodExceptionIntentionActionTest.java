package de.espend.idea.php.phpunit.intention;

import com.jetbrains.php.lang.PhpFileType;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 *
 * @see de.espend.idea.php.phpunit.intention.MethodExceptionIntentionAction
 */
public class MethodExceptionIntentionActionTest extends PhpUnitLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("MethodExceptionIntentionAction.php");
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit/intention/fixtures";
    }

    public void testThatIntentionIsAvailableForConstructorContext() {
        assertIntentionIsAvailable(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            <caret>\n" +
                "        }\n" +
                "    }",
            "PHPUnit: Expected exception"
        );
    }
}
