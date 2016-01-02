package com.phpuaca.completion.filter;

import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.*;
import com.phpuaca.completion.filter.util.ClassFinder;
import com.phpuaca.completion.util.PhpArrayParameter;
import com.phpuaca.completion.util.PhpMethodChain;
import com.phpuaca.completion.util.PhpVariable;

public class InvocationMockerFilter extends Filter {

    public InvocationMockerFilter(FilterContext context) {
        super(context);

        Variable variable = (Variable) PsiTreeUtil.getDeepestFirst(context.getMethodReference()).getParent();
        MethodReference methodReference = (new PhpVariable(variable)).findClosestAssignment();

        if (methodReference != null) {
            ClassFinder.Result classFinderResult = (new ClassFinder()).find(methodReference);
            if (classFinderResult != null) {
                allowModifier(PhpModifier.PUBLIC_ABSTRACT_DYNAMIC);
                allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC);
                allowModifier(PhpModifier.PROTECTED_ABSTRACT_DYNAMIC);
                allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC);

                if (classFinderResult.getClassConstantReference() != null) {
                    setClassConstantReference(classFinderResult.getClassConstantReference());
                }
                if (classFinderResult.getClassName() != null) {
                    setClassName(classFinderResult.getClassName());
                }

                MethodReference definitionMethodReference = (new PhpMethodChain(methodReference)).findMethodReference("setMethods");
                if (definitionMethodReference == null) {
                    definitionMethodReference = methodReference;
                    allowMethods();
                }

                ParameterList parameterList = definitionMethodReference.getParameterList();
                if (parameterList != null) {
                    PhpArrayParameter phpArrayParameter = PhpArrayParameter.create(parameterList, classFinderResult.getParameterNumber());
                    if (phpArrayParameter != null) {
                        allowMethods(phpArrayParameter.getValues());
                    }
                }
            }
        }
    }
}
