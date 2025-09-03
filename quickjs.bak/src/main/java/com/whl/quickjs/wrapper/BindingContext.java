package com.whl.quickjs.wrapper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BindingContext {

    protected Map<String, Method> functionMap;

    protected Method contextSetter;

    public BindingContext() {
        functionMap = new HashMap<>();
        contextSetter = null;
    }

    public Map<String, Method> getFunctionMap() {
        return functionMap;
    }


    public Method getContextSetter() {
        return contextSetter;
    }

    public void setContextSetter(Method contextSetter) {
        this.contextSetter = contextSetter;
    }
}
