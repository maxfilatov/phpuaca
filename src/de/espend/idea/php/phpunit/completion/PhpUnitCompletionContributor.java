package de.espend.idea.php.phpunit.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.phpunit.utils.PatternUtil;
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpUnitCompletionContributor extends CompletionContributor {
    public PhpUnitCompletionContributor() {
        extend(CompletionType.BASIC, PatternUtil.getMethodReferenceWithParameterInsideTokenStringPattern(), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
                PsiElement psiElement = completionParameters.getPosition();

                PsiElement parent = psiElement.getParent();
                if(parent instanceof StringLiteralExpression) {
                    String parameter = PhpUnitPluginUtil.findCreateMockParameterOnParameterScope((StringLiteralExpression) parent);
                    if(parameter != null) {
                        for (PhpClass phpClass : PhpIndex.getInstance(psiElement.getProject()).getAnyByFQN(parameter)) {
                            resultSet.addAllElements(phpClass.getMethods().stream()
                                .filter(method -> !method.getAccess().isPublic() || !method.getName().startsWith("__"))
                                .map((Function<Method, LookupElement>) PhpLookupElement::new)
                                .collect(Collectors.toSet())
                            );
                        }
                    }
                }
            }
        });
    }
}
