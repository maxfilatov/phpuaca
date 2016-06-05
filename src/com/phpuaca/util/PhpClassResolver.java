package com.phpuaca.util;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.phpuaca.filter.util.ClassFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

final public class PhpClassResolver {

    @Nullable
    public PhpClass resolveByClassConstantReference(@NotNull ClassConstantReference classConstantReference) {
        ClassReference classReference = PsiTreeUtil.getChildOfType(classConstantReference, ClassReference.class);
        if (classReference != null) {
            Collection<? extends PhpNamedElement> resolvedCollection = classReference.resolveGlobal(false);
            if (!resolvedCollection.isEmpty()) {
                PhpNamedElement resolvedElement = resolvedCollection.iterator().next();
                if (resolvedElement instanceof PhpClass) {
                    return (PhpClass) resolvedElement;
                }
            }
        }

        return null;
    }

    @Nullable
    public PhpClass resolveByClassStringLiteralExpression(@NotNull StringLiteralExpression stringLiteralExpression) {
        Project project = stringLiteralExpression.getProject();
        if (!DumbService.getInstance(project).isDumb()) {
            String className = stringLiteralExpression.getContents();
            if (!className.isEmpty()) {
                className = className.replace("\\\\", "\\");
                Collection<PhpClass> phpClasses = PhpIndex.getInstance(project).getAnyByFQN(className);
                if (!phpClasses.isEmpty()) {
                    return phpClasses.iterator().next();
                }
            }
        }

        return null;
    }

    @Nullable
    public PhpClass resolveByParameterListContainingClassReference(@NotNull ParameterList parameterList) {
        PsiElement[] parameters = parameterList.getParameters();
        if (parameters.length == 0) {
            return null;
        }

        if (parameters[0] instanceof ClassConstantReference) {
            return resolveByClassConstantReference((ClassConstantReference) parameters[0]);
        }

        if (parameters[0] instanceof StringLiteralExpression) {
            return resolveByClassStringLiteralExpression((StringLiteralExpression) parameters[0]);
        }

        if (parameters[0] instanceof Variable) {
            return resolveByVariable((Variable) parameters[0]);
        }

        return null;
    }

    @Nullable
    public PhpClass resolveByVariable(@NotNull Variable variable) {
        ClassFinder.Result classFinderResult = (new ClassFinder()).find(variable);
        if (classFinderResult == null) {
            return null;
        }

        return classFinderResult.getPhpClass();
    }

    @Nullable
    public PhpClass resolveByMethodReferenceContainingParameterListWithClassReference(@NotNull MethodReference methodReference) {
        ParameterList parameterList = methodReference.getParameterList();
        if (parameterList == null) {
            return null;
        }

        return resolveByParameterListContainingClassReference(parameterList);
    }
}
