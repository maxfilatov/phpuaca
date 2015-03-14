package com.phpuaca.completion.provider;

import com.intellij.codeInsight.lookup.LookupElement;
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
    public List<LookupElement> find(@NotNull Filter filter)
    {
        List<LookupElement> list = new ArrayList<LookupElement>();
        ClassConstantReference classConstantReference = filter.getClassConstantReference();

        if (classConstantReference != null) {
            PhpClassResolver resolver = new PhpClassResolver(classConstantReference);
            if (resolver.resolve()) {
                PhpClass resolvedClass = resolver.getResolvedClass();
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
        }

        return list;
    }
}
