package com.phpuaca.completion.filter;

import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocMethod;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

abstract public class Filter {

    private boolean isMethodsAllowed = false;
    private boolean isFieldsAllowed = false;

    private List<String> allowedMethods;
    private List<String> allowedFields;
    private List<String> allowedModifiers;

    private ClassConstantReference classConstantReference;

    public Filter(FilterContext context)
    {
        allowedMethods = new ArrayList<String>();
        allowedFields = new ArrayList<String>();
        allowedModifiers = new ArrayList<String>();
    }

    public void allowMethod(String methodName)
    {
        allowMethods();
        allowedMethods.add(methodName);
    }

    public void allowField(String fieldName)
    {
        allowFields();
        allowedFields.add(fieldName);
    }

    public void allowModifier(String modifierName)
    {
        allowedModifiers.add(modifierName);
    }

    public void allowModifier(PhpModifier modifier)
    {
        allowModifier(modifier.toString());
    }

    public void allowMethods()
    {
        isMethodsAllowed = true;
    }

    public void allowFields()
    {
        isFieldsAllowed = true;
    }

    protected boolean isMethodAllowed(String methodName)
    {
        return isMethodsAllowed && (allowedMethods.isEmpty() || allowedMethods.contains(methodName));
    }

    public boolean isMethodAllowed(Method method)
    {
        return !(method instanceof PhpDocMethod) && isMethodAllowed(method.getName()) && isModifierAllowed(method.getModifier());
    }

    protected boolean isFieldAllowed(String fieldName)
    {
        return isFieldsAllowed && (allowedFields.isEmpty() || allowedFields.contains(fieldName));
    }

    public boolean isFieldAllowed(Field field)
    {
        return !(field instanceof PhpDocProperty) && isFieldAllowed(field.getName()) && isModifierAllowed(field.getModifier());
    }

    protected boolean isModifierAllowed(String modifierName)
    {
        return allowedModifiers.isEmpty() || allowedModifiers.contains(modifierName);
    }

    protected boolean isModifierAllowed(PhpModifier modifier)
    {
        return isModifierAllowed(modifier.toString());
    }

    public void setClassConstantReference(@Nullable ClassConstantReference value)
    {
        classConstantReference = value;
    }

    @Nullable
    public ClassConstantReference getClassConstantReference()
    {
        return classConstantReference;
    }
}
