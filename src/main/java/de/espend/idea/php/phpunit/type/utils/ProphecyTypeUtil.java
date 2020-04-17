package de.espend.idea.php.phpunit.type.utils;

import com.jetbrains.php.lang.psi.elements.MethodReference;
import de.espend.idea.php.phpunit.utils.processor.IndexLessMethodParameterChainProcessor;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ProphecyTypeUtil {

    /**
     * Find prophesize in chaining method references, does do access index
     * and its safe to use inside index processes
     *
     * $x->prophesize('foobar')->foo()
     * $x->prophesize(Foo::class)->foo()
     */
    public static String getLocalProphesizeType(@NotNull MethodReference methodReference) {
        return IndexLessMethodParameterChainProcessor.createParameter(methodReference, "prophesize");
    }
}
