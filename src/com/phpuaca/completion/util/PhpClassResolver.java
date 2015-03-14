package com.phpuaca.completion.util;

import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.ClassConstantReference;
import com.jetbrains.php.lang.psi.elements.ClassReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

final public class PhpClassResolver implements IResolver {

    private ClassReference classReference;
    private PhpClass resolvedClass;

    public PhpClassResolver(@NotNull ClassReference classReference)
    {
        this.classReference = classReference;
    }

    public PhpClassResolver(@NotNull ClassConstantReference classConstantReference)
    {
        this.classReference = PsiTreeUtil.getChildOfType(classConstantReference, ClassReference.class);
    }

    public boolean resolve()
    {
        if (classReference != null) {
            Collection<?extends PhpNamedElement> resolvedCollection = classReference.resolveGlobal(false);
            if (!resolvedCollection.isEmpty()) {
                PhpNamedElement resolvedElement = resolvedCollection.iterator().next();
                if (resolvedElement instanceof PhpClass) {
                    resolvedClass = (PhpClass) resolvedElement;
                    return true;
                }
            }
        }

        return false;
    }

    public PhpClass getResolvedClass()
    {
        return resolvedClass;
    }
}
