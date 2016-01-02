package com.phpuaca.completion.filter;

import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocMethod;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty;
import com.jetbrains.php.lang.psi.elements.*;

import java.util.ArrayList;
import java.util.List;

abstract public class Filter {

    private boolean isMethodsAllowed = false;
    private boolean isFieldsAllowed = false;

    private List<String> allowedMethods;
    private List<String> allowedFields;
    private List<String> allowedModifiers;
    private List<String> disallowedMethods;

    private PhpClass phpClass;

    public Filter(FilterContext context)
    {
        allowedMethods = new ArrayList<String>();
        allowedFields = new ArrayList<String>();
        allowedModifiers = new ArrayList<String>();
        disallowedMethods = new ArrayList<String>();
    }

    public void allowMethod(String methodName)
    {
        allowMethods();
        disallowedMethods.remove(methodName);
        allowedMethods.add(methodName);
    }

    public void disallowMethod(String methodName)
    {
        allowMethods();
        allowedMethods.remove(methodName);
        disallowedMethods.add(methodName);
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

    public void allowMethods(List<String> methodNames)
    {
        for (String methodName : methodNames) {
            allowMethod(methodName);
        }
    }

    public void disallowMethods(List<String> methodNames)
    {
        for (String methodName : methodNames) {
            disallowMethod(methodName);
        }
    }

    public void allowFields()
    {
        isFieldsAllowed = true;
    }

    protected boolean isMethodAllowed(String methodName)
    {
        return isMethodsAllowed && !disallowedMethods.contains(methodName) && (allowedMethods.isEmpty() || allowedMethods.contains(methodName));
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

    public void setPhpClass(PhpClass phpClass)
    {
        this.phpClass = phpClass;
    }

    public PhpClass getPhpClass()
    {
        return phpClass;
    }
}
