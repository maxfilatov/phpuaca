package com.phpuaca.completion.provider;

import com.intellij.codeInsight.lookup.LookupElement;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.phpuaca.completion.filter.Filter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LookupElementProvider {

    @NotNull
    public List<LookupElement> find(@NotNull Filter filter)
    {
        List<LookupElement> list = new ArrayList<LookupElement>();
        PhpClass phpClass = filter.getPhpClass();

        if (phpClass != null) {
            for (Method method : phpClass.getMethods()) {
                if (filter.isMethodAllowed(method)) {
                    list.add(new PhpLookupElement(method));
                }
            }
            for (Field field : phpClass.getFields()) {
                if (!field.isConstant() && filter.isFieldAllowed(field)) {
                    list.add(new PhpLookupElement(field));
                }
            }
        }

        return list;
    }
}
