package com.phpuaca.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.SmartList;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpVariable {

    private Variable variable;

    public PhpVariable(@NotNull Variable variable) {
        this.variable = variable;
    }

    @Nullable
    public MethodReference findClosestAssignment() {
        String variableName = variable.getName();
        PsiElement cursor = variable;

        while (true) {
            cursor = cursor.getParent();
            if (cursor == null || cursor instanceof Method) {
                break;
            }

            if (!(cursor instanceof Statement)) {
                continue;
            }

            SmartList<Statement> statements = new SmartList<Statement>();
            statements.add((Statement) cursor);
            statements.addAll(PsiTreeUtil.getChildrenOfTypeAsList(cursor, Statement.class));

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
}
