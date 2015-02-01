package com.phpuaca.completion.filter;

import org.jetbrains.annotations.Nullable;

public class FilterConfigItemHolder {

    private FilterConfigItem item;

    public FilterConfigItemHolder setItem(@Nullable FilterConfigItem filterConfigItem)
    {
        item = filterConfigItem;
        return this;
    }

    @Nullable
    public FilterConfigItem getItem()
    {
        return item;
    }
}
