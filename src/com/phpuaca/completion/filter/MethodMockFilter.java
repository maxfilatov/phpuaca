package com.phpuaca.completion.filter;

import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.ClassConstantReference;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.Variable;
import com.phpuaca.completion.util.PhpElementUtil;

public class MethodMockFilter extends Filter {

    public MethodMockFilter(MethodReference entryPoint) {
        super(entryPoint);

        ParameterList parameterList = entryPoint.getParameterList();
        ClassConstantReference classConstantReference = PsiTreeUtil.getChildOfType(parameterList, ClassConstantReference.class);
        if (classConstantReference == null) {
            Variable variable = PsiTreeUtil.getChildOfType(parameterList, Variable.class);
            classConstantReference = PhpElementUtil.findClassConstantReference(variable);
        }

        setClassConstantReference(classConstantReference);
    }
}
