package de.espend.idea.php.phpunit.utils.processor;

import com.jetbrains.php.lang.psi.elements.MethodReference;
import de.espend.idea.php.phpunit.type.utils.PhpTypeProviderUtil;
import de.espend.idea.php.phpunit.utils.ChainVisitorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

public class IndexLessMethodParameterChainProcessor implements ChainVisitorUtil.ChainProcessorInterface {
    @NotNull
    private final Collection<String> methods;

    @Nullable
    private String parameter;

    public IndexLessMethodParameterChainProcessor(@NotNull String... methods) {
        this.methods = Arrays.asList(methods);
    }

    @Override
    public boolean process(@NotNull MethodReference methodReference) {
        if(this.methods.contains(methodReference.getName())) {
            this.parameter = PhpTypeProviderUtil.getReferenceSignatureByFirstParameter(methodReference);
            return false;
        }

        return true;
    }

    @Nullable
    public String getParameter() {
        return parameter;
    }

    @Nullable
    public static String createParameter(@NotNull MethodReference methodReference, @NotNull String... methods) {
        IndexLessMethodParameterChainProcessor processor = new IndexLessMethodParameterChainProcessor(methods);
        ChainVisitorUtil.visit(methodReference, processor);
        return processor.getParameter();
    }
}
