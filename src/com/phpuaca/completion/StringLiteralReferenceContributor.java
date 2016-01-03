package com.phpuaca.completion;

import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import com.phpuaca.completion.provider.LookupElementReferenceProvider;
import org.jetbrains.annotations.NotNull;

public class StringLiteralReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(
                PhpPatterns.psiElement().withElementType(PhpElementTypes.STRING),
                new LookupElementReferenceProvider()
        );
    }
}
