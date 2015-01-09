package com.phpuaca.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.intellij.util.ThreeState;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.*;
import com.phpuaca.completion.filter.Filter;
import com.phpuaca.completion.filter.FilterFactory;
import com.phpuaca.completion.util.PhpElementUtil;
import org.jetbrains.annotations.NotNull;

public class StringLiteralContributor extends CompletionContributor {

    public StringLiteralContributor()
    {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<CompletionParameters>() {

            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                PsiElement originalPosition = completionParameters.getOriginalPosition();
                ParameterList parameterList = PsiTreeUtil.getParentOfType(originalPosition, ParameterList.class);
                MethodReference entryPoint = PsiTreeUtil.getParentOfType(parameterList, MethodReference.class);
                if (entryPoint == null) {
                    return;
                }

                Filter filter = FilterFactory.create().getFilter(entryPoint);
                if (filter == null) {
                    return;
                }

                ClassConstantReference classConstantReference = filter.getClassConstantReference();
                ClassReference classReference = PsiTreeUtil.getChildOfType(classConstantReference, ClassReference.class);
                PhpClass phpClass = PhpElementUtil.resolvePhpClass(classReference);
                if (phpClass == null) {
                    return;
                }

                for (Method method : phpClass.getMethods()) {
                    if (filter.isMethodAllowed(method)) {
                        completionResultSet.addElement(new PhpLookupElement(method));
                    }
                }

                for (Field field : phpClass.getFields()) {
                    if (!field.isConstant() && filter.isFieldAllowed(field)) {
                        completionResultSet.addElement(new PhpLookupElement(field));
                    }
                }
            }
        });
    }

    @Override
    public boolean invokeAutoPopup(@NotNull PsiElement position, char typeChar)
    {
        return typeChar == '\'' || typeChar == '"';
    }
}
