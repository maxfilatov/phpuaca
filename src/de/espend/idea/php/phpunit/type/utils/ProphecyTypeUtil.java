package de.espend.idea.php.phpunit.type.utils;

import com.jetbrains.php.lang.psi.elements.MethodReference;
import de.espend.idea.php.phpunit.utils.ChainVisitorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        MyIndexLessProphesizeChainProcessor processor = new MyIndexLessProphesizeChainProcessor();
        ChainVisitorUtil.visit(methodReference, processor);
        return processor.getParameter();
    }

    private static class MyIndexLessProphesizeChainProcessor implements ChainVisitorUtil.ChainProcessorInterface {
        @Nullable
        private String parameter;

        @Override
        public boolean process(@NotNull MethodReference methodReference) {
            if("prophesize".equals(methodReference.getName())) {
                this.parameter = PhpTypeProviderUtil.getReferenceSignatureByFirstParameter(methodReference);
                return false;
            }

            return true;
        }

        @Nullable
        private String getParameter() {
            return parameter;
        }
    }
}
