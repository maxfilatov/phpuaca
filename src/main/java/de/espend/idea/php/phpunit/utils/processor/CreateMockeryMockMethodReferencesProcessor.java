package de.espend.idea.php.phpunit.utils.processor;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import de.espend.idea.php.phpunit.utils.ChainVisitorUtil;
import de.espend.idea.php.phpunit.utils.MockeryReferencingUtil;
import de.espend.idea.php.phpunit.utils.PhpElementsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CreateMockeryMockMethodReferencesProcessor implements ChainVisitorUtil.ChainProcessorInterface {
    @Nullable
    private List<String> returnParameters;

    /**
     * Takes methodReference psiElement and checks if this is a mock declaration (e.g. Mockery::mock))
     * or if ChainVisitorUtil can keep searching. See {@link ChainVisitorUtil}
     */
    @Override
    public boolean process(@NotNull MethodReference methodReference) {
        /*
         check if methodReference looks like: $Mockery::mock(Dependency::class) etc
         In this case we want to set parameter to: Dependency::class
         */
        if (!PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "Mockery", "mock") &&
            !PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "Mockery", "spy") &&
            !PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "Mockery", "namedMock")
        ) {
            // allowed chain of classes types
            return PhpElementsUtil.isMethodReferenceInstanceOf(
                    methodReference,
                    MockeryReferencingUtil.allowedChainClasses
            );
        }

        PsiElement[] parameters = methodReference.getParameters();
        returnParameters = new ArrayList<>();

        for (PsiElement parameter : parameters) {
            String value = PhpElementsUtil.getStringValue(parameter);
            if (value == null) {
                continue;
            }

            // Used to alter the mock created, but has no effect on referencing
            value = value.replace(" ", "");
            value = value.replace("alias:", "");
            value = value.replace("overload:", "");

            // remove anything in square brackets (these are from generated partials
            value = value.replaceAll("\\[(.*?)\\]", "");

            // Can have situation:
            // = Mockery::mock('MockeryPlugin\DemoProject\Dependency, MockeryPlugin\DemoProject\AlternativeInterface');
            returnParameters.addAll(Arrays.asList(value.split(",")));
        }

        return false;

    }

    @Nullable
    public String[] getParameters() {
        return (returnParameters != null) ? returnParameters.toArray(new String[0]) : null;
    }

    /**
     * initiates the visit loop in ChainVisitorUtil.
     *
     * @param methodReference the psiElement we are finding a target for
     * @return the name of the class for the target of the reference
     */
    @Nullable
    public static String[] createParameters(@NotNull MethodReference methodReference) {
        CreateMockeryMockMethodReferencesProcessor processor = new CreateMockeryMockMethodReferencesProcessor();
        ChainVisitorUtil.visit(methodReference, processor);
        return processor.getParameters();
    }
}