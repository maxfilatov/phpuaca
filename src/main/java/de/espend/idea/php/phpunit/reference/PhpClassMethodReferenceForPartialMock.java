package de.espend.idea.php.phpunit.reference;

import com.intellij.openapi.util.TextRange;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class PhpClassMethodReferenceForPartialMock extends PhpClassMethodReference {

    public PhpClassMethodReferenceForPartialMock(@NotNull StringLiteralExpression psiElement, @NotNull String method, @NotNull String clazz) {
        super(psiElement, method, clazz);

        String elemText = psiElement.getText();
        int methodStartIndex = elemText.indexOf(method);
        TextRange methodRange = new TextRange(methodStartIndex, methodStartIndex + method.length());

        setRangeInElement(methodRange);
    }
}
