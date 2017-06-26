package de.espend.idea.php.phpunit.intention;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.components.JBList;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.*;
import de.espend.idea.php.phpunit.utils.processor.MethodReferenceNameProcessor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Add mock based on given context:
 * $foobar->method('foobar')->willReturn();
 *
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class AddMockMethodIntention extends PsiElementBaseIntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) throws IncorrectOperationException {
        String parameter = getMockInstanceFromMethodReferenceScope(psiElement);

        if(parameter == null) {
            HintManager.getInstance().showErrorHint(editor, "No mock context found");
            return;
        }

        Set<String> methods = new TreeSet<>();
        for (PhpClass phpClass : PhpIndex.getInstance(psiElement.getProject()).getAnyByFQN(parameter)) {
            methods.addAll(phpClass.getMethods().stream()
                .filter(method -> method.getAccess().isPublic() && !method.getName().startsWith("__"))
                .map(PhpNamedElement::getName).collect(Collectors.toSet())
            );
        }

        if(methods.size() == 0) {
            HintManager.getInstance().showErrorHint(editor, "No public method found");
            return;
        }

        // Single item direct execution without selection
        if(methods.size() == 1) {
            new MyMockWriteCommand(editor, methods.iterator().next(), psiElement).execute();
            return;
        }

        final JBList<String> list = new JBList<>(methods);

        JBPopupFactory.getInstance().createListPopupBuilder(list)
            .setTitle("PHPUnit: Mock Method")
            .setItemChoosenCallback(() -> new MyMockWriteCommand(editor, list.getSelectedValue(), psiElement).execute())
            .createPopup()
            .showInBestPositionFor(editor);

    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        return getMockInstanceFromMethodReferenceScope(psiElement) != null;
    }

    @Nullable
    private String getMockInstanceFromMethodReferenceScope(@NotNull PsiElement psiElement) {
        MethodReference methodReference = PsiTreeUtil.getTopmostParentOfType(psiElement, MethodReference.class);
        if(methodReference == null) {
            return null;
        }

        return MethodReferenceNameProcessor.createParameterWithCurrent(methodReference, "createMock", "getMockBuilder");
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
        return "PHPUnit: Add mock method";
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }

    private static class MyMockWriteCommand extends WriteCommandAction.Simple {
        @NotNull
        private final Editor editor;

        @NotNull
        private final String selectedValue;

        @NotNull
        private final PsiElement psiElement;

        private MyMockWriteCommand(@NotNull Editor editor, @NotNull String selectedValue, @NotNull PsiElement psiElement) {
            super(editor.getProject(), "Add method mock");
            this.editor = editor;
            this.selectedValue = selectedValue;
            this.psiElement = psiElement;
        }

        @Override
        protected void run() {
            Statement statement = PsiTreeUtil.getParentOfType(psiElement, Statement.class);
            if(statement == null) {
                HintManager.getInstance().showErrorHint(editor, "No mock context found");
                return;
            }

            PhpReference childOfAnyType = PsiTreeUtil.findChildOfAnyType(statement, FieldReference.class, Variable.class);
            if(childOfAnyType == null) {
                return;
            }

            // $this->foobar
            // $foobar
            String prefix = childOfAnyType.getText();

            Statement methodReference = PhpPsiElementFactory.createStatement(
                psiElement.getProject(),
                String.format("%s->method('%s')->willReturn();", prefix, selectedValue)
            );

            PsiElement add = statement.add(methodReference);

            for (MethodReference reference : PsiTreeUtil.getChildrenOfTypeAsList(add, MethodReference.class)) {
                if(!"willReturn".equals(reference.getName())) {
                    continue;
                }

                PsiElement lastChild = reference.getLastChild();
                if(lastChild != null) {
                    editor.getCaretModel().moveToOffset(lastChild.getTextRange().getStartOffset());
                    editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
                }
                return;
            }
        }
    }
}
