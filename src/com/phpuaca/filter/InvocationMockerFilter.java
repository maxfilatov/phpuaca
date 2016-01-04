package com.phpuaca.filter;

import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import com.jetbrains.php.lang.psi.elements.Variable;
import com.phpuaca.filter.util.ClassFinder;
import com.phpuaca.util.PhpArrayParameter;
import com.phpuaca.util.PhpMethodChain;
import com.phpuaca.util.PhpVariable;

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

                setPhpClass(classFinderResult.getPhpClass());

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
