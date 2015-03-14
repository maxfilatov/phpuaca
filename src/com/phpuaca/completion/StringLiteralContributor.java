package com.phpuaca.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.ProcessingContext;
import com.phpuaca.completion.filter.Filter;
import com.phpuaca.completion.filter.FilterFactory;
import com.phpuaca.completion.provider.LookupElementProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StringLiteralContributor extends CompletionContributor {

    public StringLiteralContributor()
    {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<CompletionParameters>() {

            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                PsiElement originalPosition = completionParameters.getOriginalPosition();
                if (originalPosition != null) {
                    Filter filter = FilterFactory.getInstance().getFilter(originalPosition.getParent());
                    if (filter != null) {
                        List<LookupElement> elements = (new LookupElementProvider()).find(filter);
                        completionResultSet.addAllElements(elements);
                    }
                }
            }
        });
    }

    @Override
    public boolean invokeAutoPopup(@NotNull PsiElement position, char typeChar)
    {
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
