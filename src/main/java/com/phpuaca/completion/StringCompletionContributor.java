package com.phpuaca.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.jetbrains.php.lang.patterns.PhpPatterns;

public class StringCompletionContributor extends CompletionContributor {
    public StringCompletionContributor() {
        extend(
                CompletionType.BASIC,
                PhpPatterns.psiElement(),
                new StringCompletionProvider()
        );
    }
}
