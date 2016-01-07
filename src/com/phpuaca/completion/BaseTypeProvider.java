package com.phpuaca.completion;

import com.intellij.openapi.project.DumbService;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import com.phpuaca.util.PhpClassAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class BaseTypeProvider implements PhpTypeProvider2 {

    protected boolean isAvailable(@NotNull PsiElement psiElement) {
        return (psiElement instanceof MethodReference) && !DumbService.getInstance(psiElement.getProject()).isDumb();
    }

    @Nullable
    protected PhpClassAdapter getPhpClassAdapterForMethod(@NotNull Method method) {
        PhpClass phpClass = method.getContainingClass();
        if (phpClass == null) {
            return null;
        }

        return new PhpClassAdapter(phpClass);
    }
}
