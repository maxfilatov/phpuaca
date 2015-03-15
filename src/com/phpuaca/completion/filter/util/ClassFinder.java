package com.phpuaca.completion.filter.util;

import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.ClassConstantReference;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.Variable;
import com.phpuaca.completion.filter.FilterConfigItem;
import com.phpuaca.completion.filter.FilterFactory;
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

        ParameterList parameterList = mockBuilderMethodReference.getParameterList();
        ClassConstantReference classConstantReference = PsiTreeUtil.getChildOfType(parameterList, ClassConstantReference.class);
        if (classConstantReference == null) {
            return null;
        }

        return new Result(classConstantReference, filterConfigItem.getParameterNumber());
    }

    @Nullable
    public Result find(@NotNull Variable variable)
    {
        MethodReference methodReference = (new PhpVariable(variable)).findClosestAssignment();
        return methodReference == null ? null : find(methodReference);
    }

    public class Result {
        private ClassConstantReference classConstantReference;
        private int parameterNumber;

        public Result(@NotNull ClassConstantReference classConstantReference, int parameterNumber)
        {
            this.classConstantReference = classConstantReference;
            this.parameterNumber = parameterNumber;
        }

        public ClassConstantReference getClassConstantReference()
        {
            return classConstantReference;
        }

        public int getParameterNumber()
        {
            return parameterNumber;
        }
    }
}
