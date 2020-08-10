package com.phpuaca.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.phpuaca.helper.AvailabilityHelper;
import org.jetbrains.annotations.NotNull;

public class StringReferenceProvider extends PsiReferenceProvider {

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        AvailabilityHelper availabilityHelper = new AvailabilityHelper();
        if (availabilityHelper.checkScope(psiElement)) {
            StringReference reference = new StringReference(psiElement);
            return new PsiReference[]{reference};
        }

        return PsiReference.EMPTY_ARRAY;
    }
}
