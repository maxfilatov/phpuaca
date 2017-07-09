package de.espend.idea.php.phpunit.intention;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.lang.psi.elements.*;
import de.espend.idea.php.phpunit.utils.PhpElementsUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ConstructorMockIntention extends PsiElementBaseIntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) throws IncorrectOperationException {
        NewExpression newExpression = getScopeForOperation(psiElement);
        if(newExpression != null) {
            ClassReference classReference = newExpression.getClassReference();
            if (classReference != null) {
                PsiElement method = classReference.resolve();
                if(method instanceof Method) {
                    ParameterList parameterList = PsiTreeUtil.getChildOfType(newExpression, ParameterList.class);
                    if(parameterList != null) {
                        new MyConstructorCommandActionArgument(psiElement, parameterList, (Method) method, newExpression, editor).execute();
                    }
                }
            }
        }
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        NewExpression newExpression = getScopeForOperation(psiElement);
        if(newExpression == null) {
            return false;
        }

        PhpClass phpClass = PsiTreeUtil.getParentOfType(psiElement, PhpClass.class);
        if(phpClass == null) {
            return false;
        }

        return PhpElementsUtil.isInstanceOf(phpClass, "\\PHPUnit\\Framework\\TestCase")
            || PhpElementsUtil.isInstanceOf(phpClass, "\\PHPUnit_Framework_TestCase");
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "PHPUnit";
    }

    @NotNull
    @Override
    public String getText() {
        return "PHPUnit: Add constructor mocks";
    }

    @Nullable
    private NewExpression getScopeForOperation(@NotNull PsiElement psiElement) {
        // $foo = new Foo<caret>bar();
        NewExpression newExpression = PsiTreeUtil.getParentOfType(psiElement, NewExpression.class);

        if(newExpression == null) {
            // scope outside method reference chaining
            // $f<caret>oo = new Foobar();
            PsiElement variable = psiElement.getParent();
            if(variable instanceof Variable) {
                PsiElement assignmentExpression = variable.getParent();
                if(assignmentExpression instanceof AssignmentExpression) {
                    newExpression = PsiTreeUtil.getChildOfAnyType(assignmentExpression, NewExpression.class);
                }
            }
        }

        return newExpression;
    }

    private static class MyConstructorCommandActionArgument extends WriteCommandAction.Simple {
        @NotNull
        private final PsiElement scope;

        @NotNull
        private final ParameterList parameterList;

        @NotNull
        private final Method method;

        @NotNull
        private final NewExpression newExpression;

        @NotNull
        private final Editor editor;

        private MyConstructorCommandActionArgument(@NotNull PsiElement scope, @NotNull ParameterList parameterList, @NotNull Method method, @NotNull NewExpression newExpression, @NotNull Editor editor) {
            super(scope.getProject(), scope.getContainingFile());
            this.scope = scope;
            this.parameterList = parameterList;
            this.method = method;
            this.newExpression = newExpression;
            this.editor = editor;
        }

        @Override
        protected void run() throws Throwable {
            List<String> strings = new ArrayList<>();

            PsiElement[] parameters = parameterList.getParameters();
            int length = parameters.length;

            int pos = 0;
            for (Parameter parameter : method.getParameters()) {
                String className = parameter.getDeclaredType().toString();

                if(pos++ < length) {
                    continue;
                }

                strings.add(PhpElementsUtil.insertUseIfNecessary(newExpression, className));
            }

            PsiDocumentManager.getInstance(scope.getProject())
                .doPostponedOperationsAndUnblockDocument(editor.getDocument());

            PsiDocumentManager.getInstance(scope.getProject())
                .commitDocument(editor.getDocument());

            List<String> collect = strings
                .stream()
                .map(s -> String.format("$this->createMock(%s::class)", s))
                .collect(Collectors.toList());

            String join = StringUtils.join(collect, ", ");

            int startOffset = parameterList.getTextRange().getStartOffset();
            if(parameters.length > 0) {
                PsiElement parameter = parameters[parameters.length - 1];
                startOffset = parameter.getTextRange().getEndOffset();

                join = ", " + join;
            }

            editor.getDocument().insertString(startOffset, join);

            PsiElement statement = newExpression.getParent();

            CodeStyleManager.getInstance(scope.getProject()).reformatText(
                newExpression.getContainingFile(),
                statement.getTextRange().getStartOffset(),
                statement.getTextRange().getEndOffset() + join.length()
            );
        }
    }
}
