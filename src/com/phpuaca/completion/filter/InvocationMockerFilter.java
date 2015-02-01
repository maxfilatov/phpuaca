package com.phpuaca.completion.filter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.refactoring.PhpNameUtil;
import com.phpuaca.completion.util.PhpElementUtil;

public class InvocationMockerFilter extends Filter {

    public InvocationMockerFilter(FilterContext context) {
        super(context);

        FilterConfigItemHolder definitionFilterConfigItemHolder = new FilterConfigItemHolder();
        Variable variable = (Variable) PsiTreeUtil.getDeepestFirst(context.getMethodReference()).getParent();
        MethodReference methodReference = PhpElementUtil.findClosestMethodReferenceForVariableAssignment(variable);
        ClassConstantReference classConstantReference = FilterUtil.findClassConstantReference(methodReference, definitionFilterConfigItemHolder);

        allowModifier(PhpModifier.PUBLIC_ABSTRACT_DYNAMIC);
        allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC);
        allowModifier(PhpModifier.PROTECTED_ABSTRACT_DYNAMIC);
        allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC);

        setClassConstantReference(classConstantReference);

        FilterConfigItem definitionFilterConfigItem = definitionFilterConfigItemHolder.getItem();
        if (definitionFilterConfigItem != null) {
            MethodReference definitionMethodReference = PhpElementUtil.findMethodReferenceInChain(methodReference, "setMethods");
            if (definitionMethodReference == null) {
                definitionMethodReference = methodReference;
            }

            if (definitionMethodReference != null) {
                ParameterList parameterList = definitionMethodReference.getParameterList();
                if (parameterList != null) {
                    PsiElement[] parameters = parameterList.getParameters();
                    int position = definitionFilterConfigItem.getParameterNumber() - 1;
                    if (position < parameters.length && parameters[position] instanceof ArrayCreationExpression) {
                        ArrayCreationExpression arrayCreationExpression = (ArrayCreationExpression) parameters[position];
                        for (PsiElement child : arrayCreationExpression.getChildren()) {
                            LeafPsiElement leaf = (LeafPsiElement) PsiTreeUtil.getDeepestLast(child);
                            allowMethod(PhpNameUtil.unquote(leaf.getText()));
                        }
                    }
                }
            }
        }
    }
}
