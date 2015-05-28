package com.phpuaca.completion.provider;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.ClassConstantReference;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.phpuaca.completion.filter.Filter;
import com.phpuaca.completion.util.PhpClassResolver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LookupElementProvider {

    @NotNull
    public List<LookupElement> find(@NotNull Project project, @NotNull Filter filter)
    {
        List<LookupElement> list = new ArrayList<LookupElement>();
        ClassConstantReference classConstantReference = filter.getClassConstantReference();
        PhpClass resolvedClass = null;

        if (classConstantReference != null) {
            PhpClassResolver resolver = new PhpClassResolver(classConstantReference);
            if (resolver.resolve()) {
                resolvedClass = resolver.getResolvedClass();
            }
        }

        String className = filter.getClassName();
        if (className != null) {
            resolvedClass = PhpClassResolver.getClass(project, className);
        }

        if (resolvedClass != null) {
            for (Method method : resolvedClass.getMethods()) {
                if (filter.isMethodAllowed(method)) {
                    list.add(new PhpLookupElement(method));
                }
            }
            for (Field field : resolvedClass.getFields()) {
                if (!field.isConstant() && filter.isFieldAllowed(field)) {
                    list.add(new PhpLookupElement(field));
                }
            }
        }

        return list;
    }
}
