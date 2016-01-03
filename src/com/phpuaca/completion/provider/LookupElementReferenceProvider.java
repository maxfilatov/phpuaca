package com.phpuaca.completion.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.phpuaca.completion.StringLiteralReference;
import org.jetbrains.annotations.NotNull;

public class LookupElementReferenceProvider extends PsiReferenceProvider {

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        StringLiteralReference reference = new StringLiteralReference(psiElement);
        return new PsiReference[] {reference};
    }
}
