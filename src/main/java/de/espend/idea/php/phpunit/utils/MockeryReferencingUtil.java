package de.espend.idea.php.phpunit.utils;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.*;
import de.espend.idea.php.phpunit.utils.processor.CreateMockeryMockMethodReferencesProcessor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MockeryReferencingUtil {

    public static final String[][] expectedClassNamesAndMethods = new String[][]{
            {"Mockery_MockInterface", "expects"},
            {"Mockery_LegacyMockInterface", "shouldReceive"},
            {"Mockery_LegacyMockInterface", "shouldHaveReceived"},
            {"Mockery_LegacyMockInterface", "shouldNotReceive"},
            {"Mockery_LegacyMockInterface", "shouldNotHaveReceived"},
            {"Mockery_MockInterface", "allows"},
            {"Mockery\\MockInterface", "expects"},
            {"Mockery\\LegacyMockInterface", "shouldReceive"},
            {"Mockery\\LegacyMockInterface", "shouldHaveReceived"},
            {"Mockery\\LegacyMockInterface", "shouldNotReceive"},
            {"Mockery\\LegacyMockInterface", "shouldNotHaveReceived"},
            {"Mockery\\MockInterface", "allows"},
    };
    public static final String[] allowedChainClasses = new String[]{
            "Mockery_MockInterface",
            "Mockery_LegacyMockInterface",
            "Mockery\\MockInterface",
            "Mockery\\LegacyMockInterface",
    };

    /**
     * $foo = Mockery::mock('Foobar')
     * $foo->expects('<caret>')
     */
    @Nullable
    public static String[] findMockeryMockParametersOnParameterScope(@NotNull StringLiteralExpression psiElement) {
        return _findMockeryMockParametersOnParameterScope(psiElement);
    }

    @Nullable
    private static String[] _findMockeryMockParametersOnParameterScope(@NotNull PsiElement psiElement) {
        PsiElement parameterList = psiElement.getParent();
        if (!(parameterList instanceof ParameterList)) {
            return new String[0];
        }

        PsiElement methodReference = parameterList.getParent();

        if (!(methodReference instanceof MethodReference)) {
            return new String[0];
        }

        for (String[] classAndMethod : expectedClassNamesAndMethods) {
            if (PhpElementsUtil.isMethodReferenceInstanceOf(
                    (MethodReference) methodReference, classAndMethod[0], classAndMethod[1])) {
                return CreateMockeryMockMethodReferencesProcessor.createParameters((MethodReference) methodReference);
            }
        }
        return new String[0];
    }

    /**
     * Consider situation <code>$this->dependency->shouldReceive(['calledMethod' => 'mocked result',
     * 'secondCalledMethod' => 'mocked result']);</code>
     * If psi element is 'calledMethod'
     */
    @Nullable
    public static String[] findMockeryMockParametersOnArrayHashScope(@NotNull StringLiteralExpression psiElement) {
        PsiElement phpPsiElement = psiElement.getParent();
        if (phpPsiElement instanceof PhpPsiElement) {
            PsiElement arrayHashElement = phpPsiElement.getParent();

            // we need to also check that this is the first child of the hash element (the method name)
            if (arrayHashElement instanceof ArrayHashElement && arrayHashElement.getFirstChild() == phpPsiElement) {
                PsiElement arrayCreationExpression = arrayHashElement.getParent();

                if (arrayCreationExpression instanceof ArrayCreationExpression) {
                    //We are now at the parameter list level so we can use findCreateMockParameterOnParameterScope

                    return _findMockeryMockParametersOnParameterScope(arrayCreationExpression);
                }
            }
        }

        return new String[0];
    }

    @Nullable
    public static String[] findMockeryMockParametersOnArrayElementScope(@NotNull StringLiteralExpression psiElement) {
        PsiElement phpPsiElement = psiElement.getParent();
        if (phpPsiElement instanceof PhpPsiElement) {
            PsiElement arrayCreationExpression = phpPsiElement.getParent();

            if (arrayCreationExpression instanceof ArrayCreationExpression) {
                //We are now at the parameter list level so we can use findCreateMockParameterOnParameterScope
                return _findMockeryMockParametersOnParameterScope(arrayCreationExpression);
            }
        }

        return new String[0];
    }

    @Nullable
    public static String[] findMockeryMockParametersOnPartialMockStringDeclarationScope(@NotNull PsiElement psiElement) {
        PsiElement parameterList = psiElement.getParent();
        if (!(parameterList instanceof ParameterList)) {
            return new String[0];
        }

        PsiElement methodReference = parameterList.getParent();
        if (!(methodReference instanceof MethodReference)) {
            return new String[0];
        }

        if (PhpElementsUtil.isMethodReferenceInstanceOf((MethodReference) methodReference, "Mockery", "mock")) {
            // Generated Partials are identified by methods in square brackets i.e. 'Dependency[calledMethod]'

            String elemText = StringUtils.deleteWhitespace(psiElement.getText().replace("'", "").replace("\"", ""));

            if (elemText.contains("[")) {
                String className = StringUtils.substringBefore(elemText, "[");
                String[] methodNames = StringUtils.substringBetween(elemText, "[", "]").split(",");

                return ArrayUtils.insert(0, methodNames, className);
            }
        }
        return new String[0];
    }

    @Nullable
    public static String[] findMockeryMockParametersOnPartialMockConcatenationDeclarationScope(@NotNull PsiElement psiElement) {
        PsiElement concatenationExpression = psiElement.getParent();
        if (!(concatenationExpression instanceof ConcatenationExpression)) {
            return new String[0];
        }

        PsiElement parameterList = concatenationExpression.getParent();
        if (!(parameterList instanceof ParameterList)) {
            return new String[0];
        }

        PsiElement methodReference = parameterList.getParent();
        if (!(methodReference instanceof MethodReference)) {
            return new String[0];
        }

        if (PhpElementsUtil.isMethodReferenceInstanceOf((MethodReference) methodReference, "Mockery", "mock")) {
            // Generated Partials are identified by methods in square brackets i.e. 'Dependency::class . [calledMethod]'

            String concatenationValue = StringUtils.deleteWhitespace(PhpElementsUtil.getStringValue(concatenationExpression));

            if (concatenationValue != null && concatenationValue.contains("[")) {
                String className = StringUtils.substringBefore(concatenationValue, "[");
                String[] methodNames = StringUtils.substringBetween(concatenationValue, "[", "]").split(",");

                return ArrayUtils.insert(0, methodNames, className);
            }
        }
        return new String[0];
    }
}