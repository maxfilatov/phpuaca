package com.phpuaca.completion.util;

import com.intellij.util.SmartList;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpElementUtil {

    @Nullable
    public static MethodReference findClosestMethodReferenceForVariableAssignment(@Nullable Variable variable)
    {
        if (variable == null) {
            return null;
        }

        String variableName = variable.getName();
        PsiElement parent = variable;

        while (true) {
            parent = parent.getParent();
            if (parent == null || parent instanceof Method) {
                break;
            }

            if (!(parent instanceof Statement)) {
                continue;
            }

            SmartList<Statement> statements = new SmartList<Statement>();
            statements.add((Statement) parent);
            statements.addAll(PsiTreeUtil.getChildrenOfTypeAsList(parent, Statement.class));

            for (Statement statement : statements) {
                AssignmentExpression assignmentExpression = PsiTreeUtil.getChildOfType(statement, AssignmentExpression.class);
                if (assignmentExpression == null) {
                    continue;
                }

                Variable statementVariable = PsiTreeUtil.getChildOfType(assignmentExpression, Variable.class);
                if (statementVariable == null) {
                    continue;
                }

                String statementVariableName = statementVariable.getName();
                if (statementVariableName == null || !statementVariableName.equals(variableName)) {
                    continue;
                }

                MethodReference methodReference = PsiTreeUtil.getChildOfType(assignmentExpression, MethodReference.class);
                if (methodReference != null) {
                    return methodReference;
                }
            }
        }

        return null;
    }

    @Nullable
    public static MethodReference findMethodReferenceInChain(@Nullable MethodReference entryPoint, @NotNull String methodName)
    {
        if (entryPoint == null) {
            return null;
        }

        PsiElement psiElement = entryPoint;

        while (true) {
            PsiElement firstChild = psiElement.getFirstChild();
            if (firstChild == null) {
                break;
            }

            psiElement = firstChild;
            if ((psiElement instanceof MethodReference) && methodName.equals(((MethodReference) psiElement).getName())) {
                return (MethodReference) psiElement;
            }
        }

        return null;
    }
}
