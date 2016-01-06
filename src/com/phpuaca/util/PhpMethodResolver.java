package com.phpuaca.util;

import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

final public class PhpMethodResolver {

    @Nullable
    public Method resolveByMethodReference(@NotNull MethodReference methodReference) {
        Collection<? extends PhpNamedElement> resolvedCollection = methodReference.resolveGlobal(true);
        if (!resolvedCollection.isEmpty()) {
            PhpNamedElement resolvedElement = resolvedCollection.iterator().next();
            if (resolvedElement instanceof Method) {
                return (Method) resolvedElement;
            }
        }

        return null;
    }
}
