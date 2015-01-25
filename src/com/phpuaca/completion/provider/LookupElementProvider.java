package com.phpuaca.completion.provider;

import com.intellij.codeInsight.lookup.LookupElement;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.ClassConstantReference;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.phpuaca.completion.filter.Filter;
import com.phpuaca.completion.util.PhpElementUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LookupElementProvider {

    @NotNull
    public List<LookupElement> find(@Nullable Filter filter)
    {
        List<LookupElement> list = new ArrayList<LookupElement>();

        if (filter != null) {
            ClassConstantReference classConstantReference = filter.getClassConstantReference();
            PhpClass phpClass = PhpElementUtil.resolvePhpClass(classConstantReference);
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
        }

        return list;
    }
}
