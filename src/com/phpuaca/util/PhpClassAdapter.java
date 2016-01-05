package com.phpuaca.util;

import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

public class PhpClassAdapter {

    protected PhpClass phpClass;

    public PhpClassAdapter(@NotNull PhpClass phpClass) {
        this.phpClass = phpClass;
    }

    public PhpClass getPhpClass() {
        return phpClass;
    }

    public boolean isSubclassOf(String parentClassName) {
        PhpClass currentPhpClass = getPhpClass();

        do {
            if (currentPhpClass.getFQN().equals(parentClassName)) {
                return true;
            }
        } while ((currentPhpClass = currentPhpClass.getSuperClass()) != null);

        return false;
    }
}
