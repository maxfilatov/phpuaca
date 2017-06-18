package de.espend.idea.php.phpunit.type;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider3;
import de.espend.idea.php.phpunit.type.utils.PhpTypeProviderUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

/**
 * $this->prophesize(Foobar::class)->find()->will<caret>Return;
 *
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class GetMockBuilderTypeProvider implements PhpTypeProvider3 {
    public static final char SIGNATURE_KEY = '\u1533';
    public static char TRIM_KEY = '\u1536';

    @Override
    public char getKey() {
        return SIGNATURE_KEY;
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement psiElement) {
        if(!(psiElement instanceof MethodReference)) {
            return null;
        }

        String name = ((MethodReference) psiElement).getName();
        if("getMockBuilder".equals(name)) {
            String signature = PhpTypeProviderUtil.getReferenceSignatureByFirstParameter((MethodReference) psiElement);
            return signature == null ? null : new PhpType().add("#" + SIGNATURE_KEY + signature + TRIM_KEY);
        }

        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String expression, Set<String> visited, int depth, Project project) {
        return null;
    }
}
