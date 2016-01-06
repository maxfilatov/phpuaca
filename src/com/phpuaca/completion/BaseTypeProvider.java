package com.phpuaca.completion;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import com.phpuaca.util.PhpClassAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

abstract public class BaseTypeProvider implements PhpTypeProvider2 {

    protected Collection<PhpClass> getPhpInterfaceCollection(@NotNull Project project, String FQN)
    {
        return PhpIndex.getInstance(project).getInterfacesByFQN(FQN);
    }

    @NotNull
    protected Collection<PhpClass> getPhpClassCollection(@NotNull Project project, String FQN)
    {
        return PhpIndex.getInstance(project).getClassesByFQN(FQN);
    }

    @Nullable
    protected PhpClassAdapter getPhpClassAdapterForMethod(@NotNull Method method)
    {
        PhpClass phpClass = method.getContainingClass();
        if (phpClass == null) {
            return null;
        }

        return new PhpClassAdapter(phpClass);
    }
}
