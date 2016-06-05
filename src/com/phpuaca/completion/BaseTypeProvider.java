package com.phpuaca.completion;

import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import com.phpuaca.util.PhpClassAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class BaseTypeProvider implements PhpTypeProvider2 {

    @Nullable
    protected PhpClassAdapter getPhpClassAdapterForMethod(@NotNull Method method) {
        PhpClass phpClass = method.getContainingClass();
        if (phpClass == null) {
            return null;
        }

        return new PhpClassAdapter(phpClass);
    }
}
