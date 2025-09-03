package com.whl.quickjs.wrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class JSObject {
    private final ConcurrentHashMap<Class<?>, BindingContext> bindingContextMap = new ConcurrentHashMap<>();
    private final QuickJSContext context;
    private final long pointer;
    private boolean isReleased;

    public JSObject(QuickJSContext context, long pointer) {
        this.context = context;
        this.pointer = pointer;
    }

    public void setProperty(String name, String value) {
        context.setProperty(this, name, value);
    }

    public Object getProperty(String name) {
        checkReleased();
        return context.getProperty(this, name);
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        String[] keys = getKeys();
        for (String key : keys) {
            Object obj = this.getProperty(key);
            if (obj == null || obj instanceof JSFunction) {
                continue;
            }
            if (obj instanceof Number || obj instanceof String || obj instanceof Boolean) {
                try {
                    jsonObject.put(key, obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (obj instanceof JSArray) {
                try {
                    jsonObject.put(key, ((JSArray) obj).toJSONArray());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (obj instanceof JSObject) {
                try {
                    jsonObject.put(key, ((JSObject) obj).toJSONObject());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObject;
    }

    public long getPointer() {
        return pointer;
    }

    public QuickJSContext getContext() {
        return context;
    }

    public Object get(String name) {
        checkReleased();
        return context.get(this, name);
    }

    public void set(String name, Object value) {
        checkReleased();
        context.set(this, name, value);
    }

    public String getString(String name) {
        Object value = get(name);
        return value instanceof String ? (String) value : null;
    }

    public Integer getInteger(String name) {
        Object value = get(name);
        return value instanceof Integer ? (Integer) value : null;
    }

    public Boolean getBoolean(String name) {
        Object value = get(name);
        return value instanceof Boolean ? (Boolean) value : null;
    }

    public Double getDouble(String name) {
        Object value = get(name);
        return value instanceof Double ? (Double) value : null;
    }

    public Long getLong(String name) {
        Object value = get(name);
        return value instanceof Long ? (Long) value : null;
    }

    public JSObject getJSObject(String name) {
        Object value = get(name);
        return value instanceof JSObject ? (JSObject) value : null;
    }

    public JSFunction getJSFunction(String name) {
        Object value = get(name);
        return value instanceof JSFunction ? (JSFunction) value : null;
    }

    public JSArray getJSArray(String name) {
        Object value = get(name);
        return value instanceof JSArray ? (JSArray) value : null;
    }

    public JSArray getNames() {
        JSFunction getOwnPropertyNames = (JSFunction) context.evaluate("Object.getOwnPropertyNames");
        return (JSArray) getOwnPropertyNames.call(this);
    }

    public Boolean getHas(String key) {
        JSFunction hasOwnProperty = (JSFunction) context.evaluate("Object.hasOwnProperty");
        return (Boolean) hasOwnProperty.call(key);
    }

    /**
     * JSObject 确定不再使用后，调用该方法可主动释放对 JS 对象的引用。
     * 注意：该方法不能调用多次以及释放后不能再被使用对应的 JS 对象。
     */
    public void release() {
        checkReleased();

        context.freeValue(this);
        isReleased = true;
    }

    public void hold() {
        context.hold(this);
    }

    public boolean contains(String key) {
        checkReleased();
        return context.contains(this, key);
    }

    public String[] getKeys() {
        checkReleased();
        return context.getKeys(this);
    }

    /**
     * 这里与 JavaScript 的 toString 方法保持一致
     * 返回结果参考：https://262.ecma-international.org/14.0/#sec-tostring
     *
     * @return toString in JavaScript.
     */
    @Override
    public String toString() {
        checkReleased();

        JSFunction toString = getJSFunction("toString");
        return (String) toString.call();
    }

    public String toJsonString() {
        return context.stringify(this);
    }

    public JSONObject toJsonObject() {
        return toJsonObject(true);
    }

    public JSONObject toJsonObject(boolean isNative) {
        checkReleased();
        JSONObject jsonObject = new JSONObject();
        if (isNative) {
            String[] keys = getKeys();
            for (String key : keys) {
                Object obj = this.get(key);
                if (obj == null || obj instanceof JSFunction) {
                    continue;
                }
                if (obj instanceof Number || obj instanceof String || obj instanceof Boolean) {
                    try {
                        jsonObject.put(key, obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (obj instanceof JSArray) {
                    try {
                        jsonObject.put(key, ((JSArray) obj).toJsonArray());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (obj instanceof JSObject) {
                    try {
                        jsonObject.put(key, ((JSObject) obj).toJsonObject());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            JSONArray json = getNames().toJsonArray();
            for (int i = 0; i < json.length(); i++) {
                String key = json.optString(i);
                Object obj = this.get(key);
                if (obj == null || obj instanceof JSFunction) {
                    continue;
                }
                if (obj instanceof Number || obj instanceof String || obj instanceof Boolean) {
                    try {
                        jsonObject.put(key, obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (obj instanceof JSArray) {
                    try {
                        jsonObject.put(key, ((JSArray) obj).toJsonArray());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (obj instanceof JSObject) {
                    try {
                        jsonObject.put(key, ((JSObject) obj).toJsonObject());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return jsonObject;
    }

    final void checkReleased() {
        if (isReleased) {
            throw new NullPointerException("This JSObject was Released, Can not call this!");
        }
    }

    public boolean isAlive() {
        return context.isLiveObject(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSObject jsObject = (JSObject) o;
        return pointer == jsObject.pointer;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new long[]{pointer});
    }

    public void bind(final Object callbackReceiver) throws QuickJSException {
        Objects.requireNonNull(callbackReceiver);
        checkReleased();

        BindingContext bindingContext = getBindingContext(callbackReceiver.getClass());
        Map<String, Method> functionMap = bindingContext.getFunctionMap();

        Method contextSetter = bindingContext.getContextSetter();
        if (contextSetter != null) {
            try {
                contextSetter.invoke(callbackReceiver, this.context);
            } catch (Exception e) {
                throw new QuickJSException(
                        e.getMessage());
            }
        }

        if (!functionMap.isEmpty()) {
            for (Map.Entry<String, Method> entry : functionMap.entrySet()) {
                String functionName = entry.getKey();
                final Method functionMethod = entry.getValue();
                try {
                    set(functionName, new JSCallFunction() {
                        @Override
                        public Object call(Object... args) {
                            try {
                                return functionMethod.invoke(callbackReceiver, args);
                            } catch (Exception e) {
                                throw new QuickJSException(
                                        e.getMessage());
                            }
                        }
                    });
                } catch (Exception e) {
                    throw new QuickJSException(
                            e.getMessage());
                }
            }
        }
    }

    BindingContext getBindingContext(Class<?> callbackReceiverClass) throws QuickJSException {
        Objects.requireNonNull(callbackReceiverClass);
        BindingContext bindingContext = bindingContextMap.get(callbackReceiverClass);
        if (bindingContext == null) {
            bindingContext = new BindingContext();
            Map<String, Method> functionMap = bindingContext.getFunctionMap();
            for (Method method : callbackReceiverClass.getMethods()) {
                boolean methodHandled = false;
                Function fan = method.getAnnotation(Function.class);
                if (fan != null) {
                    Function v8Function = method.getAnnotation(Function.class);
                    String functionName = v8Function.name();
                    if (functionName.length() == 0) {
                        functionName = method.getName();
                    }
                    if (!functionMap.containsKey(functionName)) {
                        functionMap.put(functionName, method);
                        methodHandled = true;
                    }
                }
                if (!methodHandled) {
                    ContextSetter can = method.getAnnotation(ContextSetter.class);
                    if (can != null) {
                        bindingContext.setContextSetter(method);
                    }
                }
            }
            bindingContextMap.put(callbackReceiverClass, bindingContext);
        }
        return bindingContext;
    }

    public String stringify() {
        return context.stringify(this);
    }

    public void setProperty(String name, JSCallFunction value) {
        context.setProperty(this, name, value);
    }
}
