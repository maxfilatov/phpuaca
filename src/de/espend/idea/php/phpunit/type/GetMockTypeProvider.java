package de.espend.idea.php.phpunit.type;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider3;
import de.espend.idea.php.phpunit.type.utils.PhpTypeProviderUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class GetMockTypeProvider implements PhpTypeProvider3 {
    @Override
    public char getKey() {
        return '\u1534';
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement psiElement) {
        if(!(psiElement instanceof MethodReference)) {
            return null;
        }

        String name = ((MethodReference) psiElement).getName();
        if("getMock".equals(name)) {
            PhpExpression classReference = ((MethodReference) psiElement).getClassReference();
            if(classReference instanceof Variable || classReference instanceof MethodReference) {
                String signature = extractTypeSignature(classReference.getType().getTypes());
                return signature == null ? null : new PhpType().add("#" + this.getKey() + signature);
            }
        }

        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String expression, Set<String> visited, int depth, Project project) {
        PhpIndex phpIndex = PhpIndex.getInstance(project);

        String resolvedParameter = PhpTypeProviderUtil.getResolvedParameter(phpIndex, expression, visited, depth);
        if(resolvedParameter == null) {
            return null;
        }

        return phpIndex.getAnyByFQN(resolvedParameter);
    }

    /**
     * #M#M#ᔳ#K#C\Foo.classᔳ.disableOriginalConstructor.disableOriginalConstructor => #K#C\Foo.class
     * #ᔳ#K#C\Foo.class => #K#C\Foo.class
     */
    private String extractTypeSignature(@NotNull Collection<String> types) {
        for (String type : types) {
            // getMockBuilder()->getMock();
            if(type.startsWith("#" + GetMockBuilderTypeProvider.SIGNATURE_KEY)) {
                return type.substring(2, type.length() - 1);
            }

            // $t->getMockBuilder(\Foo::class)->disableOriginalConstructor()->getMock();
            int i = type.indexOf("#" + GetMockBuilderTypeProvider.SIGNATURE_KEY);
            if(i > 0) {
                // #ᔳ#K#C\Foo.classᔳ...
                String typeParameter = type.substring(i + 2);
                int i1 = typeParameter.indexOf(GetMockBuilderTypeProvider.TRIM_KEY);
                if(i1 > 0) {
                    return typeParameter.substring(0, i1);
                }
            }
        }

        return null;
    }
}
