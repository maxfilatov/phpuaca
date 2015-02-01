package com.phpuaca.completion.filter;

import com.jetbrains.php.lang.psi.elements.MethodReference;

public class FilterContext {

    private FilterConfigItem filterConfigItem;
    private MethodReference methodReference;

    public FilterContext(FilterConfigItem filterConfigItem, MethodReference methodReference)
    {
        this.filterConfigItem = filterConfigItem;
        this.methodReference = methodReference;
    }

    public FilterConfigItem getFilterConfigItem()
    {
        return filterConfigItem;
    }

    public MethodReference getMethodReference()
    {
        return methodReference;
    }
}
