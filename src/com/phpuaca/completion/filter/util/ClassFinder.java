package com.phpuaca.completion.filter.util;

import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.*;
import com.phpuaca.completion.filter.FilterConfigItem;
import com.phpuaca.completion.filter.FilterFactory;
import com.phpuaca.completion.util.PhpClassResolver;
import com.phpuaca.completion.util.PhpMethodChain;
import com.phpuaca.completion.util.PhpVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final public class ClassFinder {

    @Nullable
    public Result find(@NotNull MethodReference methodReference)
    {
        String methodNameToFind = "setMethods";
        MethodReference mockBuilderMethodReference = (new PhpMethodChain(methodReference)).findMethodReference("getMockBuilder");
        if (mockBuilderMethodReference == null) {
            String methodName = methodReference.getName();
            if (methodName != null && methodName.startsWith("getMock")) {
                mockBuilderMethodReference = methodReference;
                methodNameToFind = methodName;
            }
        }

        if (mockBuilderMethodReference == null) {
            return null;
        }

        FilterConfigItem filterConfigItem = FilterFactory.getInstance().getConfig().getItem(methodNameToFind);
        if (filterConfigItem == null) {
            return null;
        }

        PhpClass phpClass = null;
        ParameterList parameterList = mockBuilderMethodReference.getParameterList();
        ClassConstantReference classConstantReference = PsiTreeUtil.getChildOfType(parameterList, ClassConstantReference.class);
        if (classConstantReference != null) {
            phpClass = (new PhpClassResolver()).resolveByClassConstantReference(classConstantReference);
        } else {
            StringLiteralExpression stringLiteralExpression = PsiTreeUtil.getChildOfType(parameterList, StringLiteralExpression.class);
            if (stringLiteralExpression != null) {
                phpClass = (new PhpClassResolver()).resolveByClassStringLiteralExpression(stringLiteralExpression);
            }
        }

        return phpClass == null ? null : new Result(phpClass, filterConfigItem.getParameterNumber());
    }

    @Nullable
    public Result find(@NotNull Variable variable)
    {
        MethodReference methodReference = (new PhpVariable(variable)).findClosestAssignment();
        return methodReference == null ? null : find(methodReference);
    }

    public class Result {
        private PhpClass phpClass;
        private int parameterNumber;

        public Result(@NotNull PhpClass phpClass, int parameterNumber)
        {
            this.phpClass = phpClass;
            this.parameterNumber = parameterNumber;
        }

        public PhpClass getPhpClass()
        {
            return phpClass;
        }

        public int getParameterNumber()
        {
            return parameterNumber;
        }
    }
}
