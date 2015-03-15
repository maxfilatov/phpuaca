package com.phpuaca.completion.filter;

import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.*;
import com.phpuaca.completion.filter.util.ClassFinder;

public class MethodMockFilter extends Filter {

    public MethodMockFilter(FilterContext context) {
        super(context);

        ParameterList parameterList = context.getMethodReference().getParameterList();
        ClassConstantReference classConstantReference = PsiTreeUtil.getChildOfType(parameterList, ClassConstantReference.class);
        if (classConstantReference == null) {
            Variable variable = PsiTreeUtil.getChildOfType(parameterList, Variable.class);
            if (variable != null) {
                ClassFinder.Result classFinderResult = (new ClassFinder()).find(variable);
                if (classFinderResult != null) {
                    classConstantReference = classFinderResult.getClassConstantReference();
                }
            }
        }

        setClassConstantReference(classConstantReference);

        String methodName = context.getFilterConfigItem().getMethodName();
        if (methodName.equals("callProtectedMethod")) {
            allowMethods();
            allowModifier(PhpModifier.PUBLIC_FINAL_DYNAMIC);
            allowModifier(PhpModifier.PUBLIC_FINAL_STATIC);
            allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC);
            allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_STATIC);
            allowModifier(PhpModifier.PROTECTED_FINAL_DYNAMIC);
            allowModifier(PhpModifier.PROTECTED_FINAL_STATIC);
            allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC);
            allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_STATIC);
        } else if (methodName.endsWith("ProtectedPropertyValue")) {
            allowFields();
            allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC);
            allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_STATIC);
            allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC);
            allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_STATIC);
        } else {
            allowMethods();
        }
    }
}
