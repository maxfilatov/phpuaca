package com.phpuaca.completion.filter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.refactoring.PhpNameUtil;
import com.phpuaca.completion.util.PhpElementUtil;
import org.jetbrains.annotations.Nullable;

public class InvocationMockerFilter extends Filter {

    public InvocationMockerFilter(MethodReference entryPoint) {
        super(entryPoint);

        Variable variable = (Variable) PsiTreeUtil.getDeepestFirst(entryPoint).getParent();
        MethodReference methodReference = PhpElementUtil.findFirstMethodReferenceForVariableAssignment(variable);
        ClassConstantReference classConstantReference = PhpElementUtil.findClassConstantReference(methodReference);

        allowMethods(methodReference);
        allowModifier(PhpModifier.PUBLIC_ABSTRACT_DYNAMIC);
        allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC);
        allowModifier(PhpModifier.PROTECTED_ABSTRACT_DYNAMIC);
        allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC);

        setClassConstantReference(classConstantReference);
    }

    protected void allowMethods(@Nullable MethodReference methodReference)
    {
        MethodReference setMethodsMethodReference = PhpElementUtil.findMethodReferenceInChain(methodReference, "setMethods");
        if (setMethodsMethodReference != null) {
            ParameterList parameterList = setMethodsMethodReference.getParameterList();
            ArrayCreationExpression arrayCreationExpression = PsiTreeUtil.getChildOfType(parameterList, ArrayCreationExpression.class);
            if (arrayCreationExpression != null) {
                for (PsiElement child : arrayCreationExpression.getChildren()) {
                    LeafPsiElement leaf = (LeafPsiElement) PsiTreeUtil.getDeepestLast(child);
                    allowMethod(PhpNameUtil.unquote(leaf.getText()));
                }
            }
        }
    }
}
