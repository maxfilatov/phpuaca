package com.phpuaca.completion.filter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.phpuaca.completion.util.PhpElementUtil;

public class FilterContext {
    private MethodReference methodReference;
    private String className;
    private String methodName;
    private int parameterNumber;

    public FilterContext(PsiElement parameter)
    {
        PsiElement parentParameter = PsiTreeUtil.getParentOfType(parameter, ArrayCreationExpression.class);
        if (parentParameter != null) {
            parameter = parentParameter;
        }

        methodReference = PsiTreeUtil.getParentOfType(parameter, MethodReference.class);
        Method method = PhpElementUtil.resolveMethod(methodReference);
        if (method != null) {
            PhpClass phpClass = (PhpClass) method.getParent();

            className = phpClass.getName();
            methodName = method.getName();
            parameterNumber = PhpElementUtil.getParameterNumber(parameter);
        }
    }

    public MethodReference getMethodReference()
    {
        return methodReference;
    }

    public String getClassName()
    {
        return className;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public int getParameterNumber()
    {
        return parameterNumber;
    }
}
