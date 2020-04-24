package de.espend.idea.php.phpunit.type;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4;
import de.espend.idea.php.phpunit.type.utils.PhpTypeProviderUtil;
import de.espend.idea.php.phpunit.utils.PhpElementsUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * Attach the types from the wrapped prophesize class based on the method index
 *
 * $this->prophesize(Foobar::class)->find(Arguments::a<caret>ny());
 *
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ProphecyArgumentTypeProvider implements PhpTypeProvider4 {
    private static final Collection<String> METHODS = Arrays.asList(
        "any",
        "cetera" // fill arguments, we can not fix magic arguments here, but the type
    );

    private static final String PROPHECY_ARGUMENT_CLASS = "\\Prophecy\\Argument";
    private static final char TRIM_KEY = '\u1539';

    @Override
    public char getKey() {
        return '\u0162';
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement psiElement) {
        if(!(psiElement instanceof MethodReference) || !((MethodReference) psiElement).isStatic() || !METHODS.contains(((MethodReference) psiElement).getName())) {
            return null;
        }

        PsiElement parameterList = psiElement.getParent();
        if (!(parameterList instanceof ParameterList)) {
            return null;
        }

        if (!PhpElementsUtil.isLocalResolveMethodReferenceInstanceOf(((MethodReference) psiElement), PROPHECY_ARGUMENT_CLASS)) {
            return null;
        }

        MethodReference methodReference = PsiTreeUtil.getParentOfType(psiElement, MethodReference.class);
        if (methodReference != null) {
            // first type wins from main "prophecy" call
            for (String type : methodReference.getType().getTypes()) {
                // pipe the parameter prophecy type provider without its "#" starting elements; also attach the parameter to resolve it later
                if(type.startsWith("#" + ProphecyTypeProvider.TYPE_KEY)) {
                    Integer parameterIndex = PhpElementsUtil.getParameterIndex((ParameterList) parameterList, psiElement);
                    if (parameterIndex != null) {
                        return new PhpType().add("#" + this.getKey() + type.substring(2) + TRIM_KEY + parameterIndex);
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    @Override
    public PhpType complete(String s, Project project) {
        // strip our type prefix "#..." and extract our signature with the type and the parameter index
        String substring = s.substring(2);
        String[] signature = substring.split(String.valueOf(TRIM_KEY));
        if (signature.length != 2) {
            return null;
        }

        // split the foreign prophecy type resolve to get the main instance
        String foreignSignature = signature[0];
        String[] regex = foreignSignature.split(String.valueOf(ProphecyTypeProvider.TRIM_KEY));
        if (regex.length != 2) {
            return null;
        }

        String className = PhpTypeProviderUtil.getResolvedParameter(PhpIndex.getInstance(project), regex[0]);
        if (className == null) {
            return null;
        }

        // attach the mocked prophecy class instance with its used parameter
        PhpType types = null;
        for (PhpClass phpClass : PhpIndex.getInstance(project).getAnyByFQN(className)) {
            Method methodByName = phpClass.findMethodByName(regex[1]);
            if (methodByName == null) {
                continue;
            }

            int methodParameter = Integer.parseInt(signature[1]);
            Parameter parameter = methodByName.getParameter(methodParameter);
            if (parameter != null) {
                // init PhpType; allow collection all types for all classes
                if (types == null) {
                    types = new PhpType();
                }

                types.add(parameter.getType());
            }
        }

        return types;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String s, Set<String> set, int i, Project project) {
        return null;
    }
}
