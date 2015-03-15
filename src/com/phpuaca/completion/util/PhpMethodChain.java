package com.phpuaca.completion.util;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpMethodChain {

    private MethodReference entryPoint;

    public PhpMethodChain(@NotNull MethodReference entryPoint)
    {
        this.entryPoint = entryPoint;
    }

    @Nullable
    public MethodReference findMethodReference(@NotNull String methodName)
    {
        PsiElement firstChild;
        PsiElement cursor = entryPoint;

        while ((firstChild = cursor.getFirstChild()) != null) {
            cursor = firstChild;
            if ((cursor instanceof MethodReference) && methodName.equals(((MethodReference) cursor).getName())) {
                return (MethodReference) cursor;
            }
        }

        return null;
    }
}
