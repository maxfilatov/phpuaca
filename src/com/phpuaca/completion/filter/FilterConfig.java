package com.phpuaca.completion.filter;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FilterConfig {
    Map<String, FilterConfigItem> config;

    public FilterConfig()
    {
        config = new HashMap<String, FilterConfigItem>();
    }

    public FilterConfig(FilterConfig filterConfig)
    {
        config = filterConfig.config;
    }

    public FilterConfig add(FilterConfigItem filterConfigItem)
    {
        String hash = createHash(filterConfigItem.getClassName(), filterConfigItem.getMethodName());
        config.put(hash, filterConfigItem);
        return this;
    }

    @Nullable
    public FilterConfigItem getItem(String className, String methodName)
    {
        String hash = createHash(className, methodName);
        return config.get(hash);
    }

    @Nullable
    public FilterConfigItem getItem(String methodName)
    {
        String hash = createHash("", methodName);
        for (Map.Entry<String, FilterConfigItem> entry : config.entrySet()) {
            if (entry.getKey().endsWith(hash)) {
                return entry.getValue();
            }
        }

        return null;
    }

    private String createHash(String className, String methodName)
    {
        return className + "::" + methodName;
    }
}
