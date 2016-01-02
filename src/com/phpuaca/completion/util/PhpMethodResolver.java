package com.phpuaca.completion.util;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

final public class PhpMethodResolver {

    private MethodReference methodReference;
    private Method resolvedMethod;
    private PhpClass resolvedClass;

    public PhpMethodResolver(@NotNull MethodReference methodReference)
    {
        this.methodReference = methodReference;
    }

    public boolean resolve()
    {
        Collection<?extends PhpNamedElement> resolvedCollection = methodReference.resolveGlobal(true);
        if (!resolvedCollection.isEmpty()) {
            PhpNamedElement resolvedElement = resolvedCollection.iterator().next();
            if (resolvedElement instanceof Method) {
                resolvedMethod = (Method) resolvedElement;
                PsiElement parent = resolvedMethod.getParent();
                if (parent instanceof PhpClass) {
                    resolvedClass = (PhpClass) parent;
                    return true;
                }
            }
        }

        return false;
    }

    public Method getResolvedMethod()
    {
        return resolvedMethod;
    }

    public PhpClass getResolvedClass()
    {
        return resolvedClass;
    }
}
