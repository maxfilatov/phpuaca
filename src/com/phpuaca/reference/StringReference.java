package com.phpuaca.reference;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.refactoring.PhpNameUtil;
import com.phpuaca.filter.Filter;
import com.phpuaca.filter.FilterFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringReference implements PsiReference {

    private PsiElement psiElement;

    public StringReference(PsiElement psiElement) {
        this.psiElement = psiElement;
    }

    @Override
    public PsiElement getElement() {
        return psiElement;
    }

    @Override
    public TextRange getRangeInElement() {
        return new TextRange(1, getElement().getTextLength() - 1);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        if (DumbService.isDumb(getElement().getProject())) {
            return null;
        }

        PsiElement resolvedElement = null;
        Filter filter = FilterFactory.getInstance().getFilter(getElement());
        if (filter != null) {
            PhpClass phpClass = filter.getPhpClass();
            if (phpClass != null) {
                String name = getName();
                resolvedElement = phpClass.findMethodByName(name);
                if (resolvedElement == null) {
                    resolvedElement = phpClass.findFieldByName(name, false);
                }
            }
        }

        return resolvedElement;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getName();
    }

    @Override
    public PsiElement handleElementRename(String s) throws IncorrectOperationException {
        PsiElement element = getElement();
        ASTNode nameNode = element.getNode();
        if (nameNode != null && !getCanonicalText().equals(s)) {
            String replacement = getText().replace(getName(), s);
            ASTNode node = PhpPsiElementFactory.createFromText(element.getProject(), PhpElementTypes.STRING, replacement).getNode();
            nameNode.getTreeParent().replaceChild(nameNode, node);
        }

        return element;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement psiElement) throws IncorrectOperationException {
        return null;
    }

    @Override
    public boolean isReferenceTo(PsiElement psiElement) {
        PsiElement resolvedElement = resolve();
        return resolvedElement != null && resolvedElement.equals(psiElement);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    @Override
    public boolean isSoft() {
        return false;
    }

    protected String getName() {
        return PhpNameUtil.unquote(getText());
    }

    protected String getText() {
        return getElement().getText();
    }
}
