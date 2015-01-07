package com.phpuaca.completion;

import com.intellij.codeInsight.completion.CompletionConfidence;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ThreeState;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.phpunit.PhpUnitUtil;
import org.jetbrains.annotations.NotNull;

public class StringLiteralConfidence extends CompletionConfidence {

    @NotNull
    @Override
    public ThreeState shouldSkipAutopopup(@NotNull PsiElement contextElement, @NotNull PsiFile psiFile, int offset) {
        if (!(psiFile instanceof PhpFile) || !PhpUnitUtil.isPhpUnitTestFile(psiFile)) {
            return ThreeState.UNSURE;
        }

        PsiElement context = contextElement.getContext();
        if (context instanceof StringLiteralExpression) {
            // $this->method('cursor');
            PsiElement stringLiteralExpressionContext = context.getContext();
            if (stringLiteralExpressionContext instanceof ParameterList) {
                return ThreeState.NO;
            }

            // $this->method(array('cursor'));
            if (stringLiteralExpressionContext instanceof PhpPsiElement && stringLiteralExpressionContext.toString().equals("Array value")) {
                PsiElement arrayValueContext = stringLiteralExpressionContext.getContext();
                if (arrayValueContext instanceof ArrayCreationExpression && arrayValueContext.getContext() instanceof ParameterList) {
                    return ThreeState.NO;
                }
            }
        }

        return ThreeState.UNSURE;
    }
}
