package com.phpuaca.completion.util;

import com.intellij.util.SmartList;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PhpElementUtil {

    @Nullable
    public static MethodReference findFirstMethodReferenceForVariableAssignment(@Nullable Variable variable)
    {
        if (variable == null) {
            return null;
        }
        
        String variableName = variable.getName();

        Method classMethod = PsiTreeUtil.getParentOfType(variable, Method.class);
        SmartList<Statement> statements = findMethodStatements(classMethod);
        if (statements == null) {
            return null;
        }

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

            return PsiTreeUtil.getChildOfType(assignmentExpression, MethodReference.class);
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

    @Nullable
    public static ClassConstantReference findClassConstantReference(@Nullable MethodReference methodReference)
    {
        MethodReference mockBuilderMethodReference = findMethodReferenceInChain(methodReference, "getMockBuilder");
        if (mockBuilderMethodReference == null) {
            return null;
        }

        ParameterList parameterList = mockBuilderMethodReference.getParameterList();
        return PsiTreeUtil.getChildOfType(parameterList, ClassConstantReference.class);
    }

    @Nullable
    public static ClassConstantReference findClassConstantReference(@Nullable Variable variable)
    {
        MethodReference methodReference = findFirstMethodReferenceForVariableAssignment(variable);
        return methodReference == null ? null : findClassConstantReference(methodReference);
    }

    @Nullable
    public static PhpClass resolvePhpClass(@Nullable ClassReference classReference)
    {
        if (classReference == null) {
            return null;
        }

        Collection<?extends PhpNamedElement> resolved = classReference.resolveGlobal(false);
        if (resolved.isEmpty()) {
            return null;
        }

        PhpNamedElement phpClass = resolved.iterator().next();
        return phpClass instanceof PhpClass ? (PhpClass) phpClass : null;
    }

    @Nullable
    public static PhpClass resolvePhpClass(@Nullable ClassConstantReference classConstantReference)
    {
        ClassReference classReference = PsiTreeUtil.getChildOfType(classConstantReference, ClassReference.class);
        return classReference == null ? null : resolvePhpClass(classReference);
    }

    @Nullable
    public static Method resolveMethod(@Nullable MethodReference methodReference)
    {
        if (methodReference == null) {
            return null;
        }

        Collection<?extends PhpNamedElement> resolved = methodReference.resolveGlobal(true);
        if (resolved.isEmpty()) {
            return null;
        }

        PhpNamedElement method = resolved.iterator().next();
        return method instanceof Method ? (Method) method : null;
    }

    @Nullable
    private static SmartList<Statement> findMethodStatements(@Nullable Method method)
    {
        // first opening "{" in specified method
        GroupStatement groupStatement = PsiTreeUtil.getChildOfType(method, GroupStatement.class);
        if (groupStatement == null) {
            return null;
        }

        SmartList<Statement> statements = new SmartList<Statement>();
        return findInnerStatements(groupStatement, statements);
    }

    @NotNull
    private static SmartList<Statement> findInnerStatements(@NotNull Statement statement, @NotNull SmartList<Statement> list)
    {
        list.add(statement);

        Statement[] children = PsiTreeUtil.getChildrenOfType(statement, Statement.class);
        if (children != null) {
            for (Statement child : children) {
                list = findInnerStatements(child, list);
            }
        }

        return list;
    }
}
