package de.espend.idea.php.phpunit.utils.processor;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import de.espend.idea.php.phpunit.utils.ChainVisitorUtil;
import de.espend.idea.php.phpunit.utils.PhpElementsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class MethodReferenceNameProcessor implements ChainVisitorUtil.ChainProcessorInterface {
    @NotNull
    private final String methodName;

    @Nullable
    private String parameter;

    private MethodReferenceNameProcessor(@NotNull String methodName) {
        this.methodName = methodName;
    }

    @Override
    public boolean process(@NotNull MethodReference methodReference) {
        if(this.methodName.equals(methodReference.getName())) {
            PsiElement[] parameters = methodReference.getParameters();

            if(parameters.length > 0) {
                this.parameter = PhpElementsUtil.getStringValue(parameters[0]);
            }

            return false;
        }

        return true;
    }

    @Nullable
    public String getParameter() {
        return parameter;
    }

    @Nullable
    public static String createParameterWithCurrent(@NotNull MethodReference methodReference, @NotNull String... methodNames) {
        for (String methodName : methodNames) {
            MethodReferenceNameProcessor processor = new MethodReferenceNameProcessor(methodName);
            ChainVisitorUtil.visit(methodReference, processor, false);

            String parameter = processor.getParameter();
            if(parameter != null) {
                return parameter;
            }
        }

        return null;
    }
}
