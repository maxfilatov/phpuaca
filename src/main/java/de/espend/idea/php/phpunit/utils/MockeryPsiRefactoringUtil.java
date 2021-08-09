package de.espend.idea.php.phpunit.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.elements.impl.PhpPsiElementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MockeryPsiRefactoringUtil {

    private static MethodReference[] getMethodSequence(MethodReference startingMethod) {
        List<MethodReference> methodSequence = new ArrayList<>();
        methodSequence.add(startingMethod);
        MethodReference current = startingMethod;

        while (current.getParent() instanceof MethodReference) {
            current = (MethodReference) current.getParent();
            methodSequence.add(current);
        }

        return methodSequence.toArray(new MethodReference[0]);
    }

    private static MethodReference getBaseMethodInSequence(@NotNull MethodReference startingMethod) {
        while (startingMethod.getFirstChild() != null && startingMethod.getFirstChild() instanceof MethodReference) {
            startingMethod = (MethodReference) startingMethod.getFirstChild();
        }
        return startingMethod;
    }

    /**
     * @return True if once() is found
     */
    public static Boolean checkForOnceInMethodSequence(MethodReference startingMethod) {
        return checkForOnceInMethodSequence(getMethodSequence(startingMethod));
    }

    /**
     * @return True if once() is found
     */
    private static Boolean checkForOnceInMethodSequence(MethodReference[] methods) {

        for (MethodReference method : methods) {
            if (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "once") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "once")) {
                return true;
            }
        }
        return false;
    }

    public static Boolean checkForCountInMethodSequence(MethodReference startingMethod) {
        return checkForCountInMethodSequence(getMethodSequence(startingMethod));
    }

    private static Boolean checkForCountInMethodSequence(MethodReference[] methods) {

        for (MethodReference method : methods) {
            if (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "twice") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "twice") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "times") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "times") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "between") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "between")) {
                return true;
            }
        }
        return false;
    }

    private static void ReplaceIdentifier(@NotNull Project project, @NotNull PsiElement element, String newIdentifier) {
        element.replace(
                PhpPsiElementFactory.createStatement(project, newIdentifier).getFirstChild().getFirstChild()
        );
    }

    @Nullable
    private static PsiElement getMethodIdentifier(@NotNull MethodReference methodReference) {
        return methodReference.getNameNode() != null ? methodReference.getNameNode().getPsi() : null;
    }

    @Nullable
    public static MethodReference convertMultipleParametersToArrayParameter(@NotNull Project project, MethodReference baseMethod) {
        if (baseMethod.getParameterList() == null) {
            return null;
        }

        // Check if there is a return
        String args = null;

        for (MethodReference method : getMethodSequence(baseMethod)) {
            if (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "andReturn") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "andReturn") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "andReturns") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "andReturns")) {
                args = method.getParameterList() != null ? method.getParameterList().getText() : null;
                // Now remove the andReturn(s). This requires finding the baseMethod again.
                baseMethod = getBaseMethodInSequence((MethodReference) method.replace(method.getFirstChild()));
                break;
            }
        }

        String parametersAsString;
        if (args != null) {
            String finalArgs = args;
            parametersAsString = Arrays.stream(baseMethod.getParameters())
                    .map(PsiElement::getText)
                    .map(s -> s + " => " + finalArgs)
                    .collect(Collectors.joining(", "));
        } else {
            parametersAsString = Arrays.stream(baseMethod.getParameters())
                    .map(PsiElement::getText)
                    .collect(Collectors.joining(", "));
        }

        Statement statement = PhpPsiElementFactory.createStatement(
                project,
                String.format("[%s]", parametersAsString)
        );

        if (baseMethod.getParameterList() != null) {
            ParameterList parameterList = baseMethod.getParameterList();
            parameterList.deleteChildRange(
                    parameterList.getFirstChild(), parameterList.getLastChild()
            );
            parameterList.add(statement.getFirstChild());
        }

        return baseMethod;

    }


    public static void replaceShouldReceiveFromMethodReference(@NotNull Project project, MethodReference method, String type, boolean useFunctionNotation, boolean useMultipleStatements) {
        if (useFunctionNotation) {
            // Then must use multiple statements
            // For simplicity we treat all function notation conversions as if they were multiple methods
            replaceShouldReceiveFromMethodReferenceMultipleStatements(project, method, type, true);
        } else {
            boolean hasMultipleParameters = method.getParameterList() != null && method.getParameters().length > 0 &&
                    (method.getParameters().length > 1 || method.getParameter(0) instanceof ArrayCreationExpression);

            if (useMultipleStatements && hasMultipleParameters) {
                // Will convert to multiple standard statements
                replaceShouldReceiveFromMethodReferenceMultipleStatements(project, method, type, false);
            } else {
                // Will convert to Array form if there are multiple method parameters,
                // but if not has the advantage of leaving method reference order closer to original
                StandardReplacement(project, method, type);
            }
        }
    }

    private static void replaceShouldReceiveFromMethodReferenceMultipleStatements(Project project, MethodReference methodReference, String type, Boolean functionNotation) {
        if (!(methodReference.getParameter(0) instanceof ArrayCreationExpression)) {
            methodReference = convertMultipleParametersToArrayParameter(project, methodReference);
        }

        MethodReference[] methodReferenceSequence = getMethodSequence(methodReference);
        String argument = "";

        for (MethodReference method : methodReferenceSequence) {
            PsiElement methodIdentifier = getMethodIdentifier(method);
            if (methodIdentifier == null) {
                continue;
            }

            // We only remove with statement if targeting the function form
            if (functionNotation && (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "with") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "with"))) {
                if (method.getParameterList() != null) {
                    argument = method.getParameterList().getText();
                }
                PsiElement newElement = method.replace(method.getFirstChild());
                methodReference = getBaseMethodInSequence((MethodReference) newElement);
            }
            if (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "once") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "once")) {
                PsiElement newElement = method.replace(method.getFirstChild());
                // need to recover parent from newElement
                methodReference = getBaseMethodInSequence((MethodReference) newElement);
            }
            if (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "andReturn") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "andReturn")) {
                ReplaceIdentifier(project, methodIdentifier, "andReturns");
            }
            if (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_LegacyMockInterface", "shouldReceive") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\LegacyMockInterface", "shouldReceive")) {

                ReplaceIdentifier(project, methodIdentifier, type);
            }
        }

        PsiElement statementElement = PsiTreeUtil.getParentOfType(methodReference, Statement.class);

        if (statementElement == null) {
            return;
        }

        PsiElement statementElementRootParent = statementElement.getParent();
        String methodPrefix = methodReference.getFirstChild().getText() + "->";
        String statementText = statementElement.getText();
        String shouldReceiveMethodText = methodReference.getText();
        String methodSuffix = statementText.replace(shouldReceiveMethodText, "");

        if (methodReference.getParameterList() == null) {
            return;
        }

        PsiElement[] arrayElements = methodReference.getParameterList().getFirstChild().getChildren();
        for (PsiElement arrayElement : arrayElements) {
            PsiElement newStatement;
            if (arrayElement instanceof ArrayHashElement) {
                ArrayHashElement arrayHashElement = (ArrayHashElement) arrayElement;
                String key = arrayHashElement.getKey() != null ? arrayHashElement.getKey().getText() : "";
                String value = arrayHashElement.getValue() != null ? arrayHashElement.getValue().getText() : "";

                if (functionNotation) {
                    key = key.replaceAll("['\"]","");
                    newStatement = PhpPsiElementFactory.createStatement(project, methodPrefix +
                            type + "()->" + key + "(" + argument + ")->andReturns(" + value + ")" + methodSuffix);
                } else {
                    newStatement = PhpPsiElementFactory.createStatement(project, methodPrefix +
                            type + "(" + key + ")->andReturns(" + value + ")" + methodSuffix);
                }

                statementElementRootParent.addBefore(newStatement, statementElement);
            } else if (arrayElement instanceof PhpPsiElementImpl) {
                if (functionNotation) {
                    newStatement = PhpPsiElementFactory.createStatement(project, methodPrefix +
                            type + "()->" + arrayElement.getText().replaceAll("['\"]","") + "(" + argument + ")" + methodSuffix);
                } else {
                    newStatement = PhpPsiElementFactory.createStatement(project, methodPrefix +
                            type + "(" + arrayElement.getText() + ")" + methodSuffix);
                }

                statementElementRootParent.addBefore(newStatement, statementElement);
            }
        }
        statementElement.delete();
    }

    public static void replaceShouldNotReceive(@NotNull Project project, MethodReference method,
                                               String type, boolean useFunctionNotation, boolean useMultipleStatements) {

        PsiElement statementElement = PsiTreeUtil.getParentOfType(method, Statement.class);

        if (statementElement == null) {
            return;
        }

        PsiElement statementElementRootParent = statementElement.getParent();

        String statementText = statementElement.getText();
        String newStatementText = statementText.replace("shouldNotReceive", "shouldReceive")
                .replace(";", "->never();");

        PsiElement newStatement = PhpPsiElementFactory.createStatement(project, newStatementText);
        newStatement = statementElementRootParent.addBefore(newStatement, statementElement);
        statementElement.delete();

        // Now can do normal conversion on the shouldReceive
        MethodReference baseMethod = getBaseMethodInSequence((MethodReference) newStatement.getFirstChild());

        replaceShouldReceiveFromMethodReference(project, baseMethod, type, useFunctionNotation, useMultipleStatements);
    }

    private static void StandardReplacement(@NotNull Project project, MethodReference methodReference, String type) {

        if (methodReference.getParameterList() != null && methodReference.getParameters().length > 1) {
            methodReference = convertMultipleParametersToArrayParameter(project, methodReference);
        }
        MethodReference[] methodReferenceSequence = getMethodSequence(methodReference);

        for (MethodReference method : methodReferenceSequence) {
            PsiElement methodIdentifier = getMethodIdentifier(method);
            if (methodIdentifier == null) {
                continue;
            }

            if (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "once") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "once")) {
                method.replace(method.getFirstChild());
            }

            if (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "andReturn") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "andReturn")) {

                ReplaceIdentifier(project, methodIdentifier, "andReturns");
            }

            if (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_LegacyMockInterface", "shouldReceive") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\LegacyMockInterface", "shouldReceive")) {

                ReplaceIdentifier(project, methodIdentifier, type);
            }
        }
    }
}