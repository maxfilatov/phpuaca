package com.phpuaca.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import org.jetbrains.annotations.NotNull;

public class StringCompletionContributor extends CompletionContributor {

    public StringCompletionContributor() {
        extend(
                CompletionType.BASIC,
                PhpPatterns.psiElement(),
                new StringCompletionProvider()
        );
    }

    @Override
    public boolean invokeAutoPopup(@NotNull PsiElement position, char typeChar) {
        if (typeChar != '\'' && typeChar != '"') {
            return false;
        }

        if (position instanceof PsiWhiteSpace) {
            return true;
        }

        String text = position.getText();
        return text.equals("(") || text.equals(",");
    }
}
