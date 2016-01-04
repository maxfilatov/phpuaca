package com.phpuaca.filter;

import com.jetbrains.php.lang.psi.elements.MethodReference;
import org.jetbrains.annotations.NotNull;

public class FilterContext {

    private FilterConfigItem filterConfigItem;
    private MethodReference methodReference;

    public FilterContext(@NotNull FilterConfigItem filterConfigItem, @NotNull MethodReference methodReference) {
        this.filterConfigItem = filterConfigItem;
        this.methodReference = methodReference;
    }

    @NotNull
    public FilterConfigItem getFilterConfigItem() {
        return filterConfigItem;
    }

    @NotNull
    public MethodReference getMethodReference() {
        return methodReference;
    }
}
