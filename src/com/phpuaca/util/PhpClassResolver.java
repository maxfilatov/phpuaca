package com.phpuaca.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
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
        String className = stringLiteralExpression.getContents();
        if (!className.isEmpty()) {
            className = className.replace("\\\\", "\\");
            Project project = stringLiteralExpression.getProject();
            Collection<PhpClass> phpClasses = PhpIndex.getInstance(project).getClassesByFQN(className);
            if (!phpClasses.isEmpty()) {
                return phpClasses.iterator().next();
            }
        }

        return null;
    }

    @Nullable
    public PhpClass resolveByParameterListContainingClassReference(@Nullable ParameterList parameterList) {
        ClassConstantReference classConstantReference = PsiTreeUtil.getChildOfType(parameterList, ClassConstantReference.class);
        if (classConstantReference != null) {
            return resolveByClassConstantReference(classConstantReference);
        } else {
            StringLiteralExpression stringLiteralExpression = PsiTreeUtil.getChildOfType(parameterList, StringLiteralExpression.class);
            if (stringLiteralExpression != null) {
                return resolveByClassStringLiteralExpression(stringLiteralExpression);
            }
        }

        return null;
    }
}
