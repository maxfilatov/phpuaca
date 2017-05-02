package de.espend.idea.php.phpunit.type;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider3;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

/**
 * $this->prophesize(Foobar::class)->find()->will<caret>Return;
 *
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ProphecyTypeProvider implements PhpTypeProvider3 {
    @Override
    public char getKey() {
        return '\u1530';
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement element) {
        if(element instanceof MethodReference) {
            PhpExpression classReference = ((MethodReference) element).getClassReference();
            if(classReference instanceof Variable || classReference instanceof MethodReference) {
                Method method = PhpPsiUtil.getParentByCondition(element, Method.INSTANCEOF);
                if(method != null) {
                    PhpClass containingClass = method.getContainingClass();

                    // filter phpunit test methods
                    if(containingClass != null && containingClass.getName().endsWith("Test")) {
                        boolean isProphesize = classReference.getType().getTypes().stream().anyMatch(s -> {
                            boolean b = s.startsWith("#" + MockProphecyTypeProvider.CHAR);
                            if (!b) {
                                return false;
                            }

                            int i = s.indexOf(MockProphecyTypeProvider.TRIM_KEY);
                            return i >= 0 && s.substring(2, i).endsWith("prophesize");
                        });

                        if(isProphesize) {
                            return (new PhpType()).add("\\Prophecy\\Prophecy\\MethodProphecy");
                        }
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String expression, Set<String> visited, int depth, Project project) {
        return null;
    }
}
