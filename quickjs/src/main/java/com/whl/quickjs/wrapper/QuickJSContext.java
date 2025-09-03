package com.whl.quickjs.wrapper;

import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;

public class QuickJSContext {

    private static final String UNKNOWN_FILE = "unknown.js";
    private static String root_module_name;
    private final long runtime;
    private final long context;
    private final NativeCleaner<JSObject> nativeCleaner = new NativeCleaner<JSObject>() {
        @Override
        public void onRemove(long pointer) {
            freeDupValue(context, pointer);
        }
    };
    private final long currentThreadId;
    private final HashMap<Integer, JSCallFunction> callFunctionMap = new HashMap<>();
    private boolean destroyed = false;
    private ModuleLoader moduleLoader;

    private QuickJSContext() {
        try {
            runtime = createRuntime();
            context = createContext(runtime);
        } catch (UnsatisfiedLinkError e) {
            throw new QuickJSException("The so library must be initialized before createContext! QuickJSLoader.init should be called on the Android platform. In the JVM, you need to manually call System.loadLibrary");
        }
        currentThreadId = Thread.currentThread().getId();
    }

    public static QuickJSContext create() {
        return new QuickJSContext();
    }

    public void destroyContext() {
        checkSameThread();

        nativeCleaner.forceClean();
        destroyContext(context);
    }

    public void setProperty(JSObject jsObj, String name, Object value) {
        checkSameThread();

        setProperty(context, jsObj.getPointer(), name, value);
    }

    public JSArray createNewJSArray() {
        return (JSArray) parseJSON("[]");
    }

    public Object parseJSON(String s) {
        return null;
    }

    public Object getProperty(JSObject jsObj, String name) {
        checkSameThread();
        return getProperty(context, jsObj.getPointer(), name);
    }

    public JSObject createNewJSObject() {
        return (JSObject) parseJSON("{}");
    }

    public boolean isLiveObject(JSObject jsObj) {
        return isLiveObject(runtime, jsObj.getPointer());
    }

    public void setConsole(final Console console) {
        if (console == null) {
            return;
        }
        JSObject consoleObject = createJSObject();
        getGlobalObject().set("console", consoleObject);
        consoleObject.set("log", new JSCallFunction() {
            @Override
            public Object call(Object... args) {
                StringBuilder value = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    value.append(args[i]);
                    if (i < args.length - 1) {
                        value.append(" ");
                    }
                }
                console.log(value.toString());
                return null;
            }
        });
    }

    public void setMaxStackSize(int maxStackSize) {
        setMaxStackSize(runtime, maxStackSize);
    }

    public void runGC() {
        runGC(runtime);
    }

    public void setMemoryLimit(int memoryLimitSize) {
        setMemoryLimit(runtime, memoryLimitSize);
    }

    public void dumpMemoryUsage(File target) {
        if (target == null || !target.exists()) {
            return;
        }

        dumpMemoryUsage(runtime, target.getAbsolutePath());
    }

    // will use stdout to print.
    public void dumpMemoryUsage() {
        dumpMemoryUsage(runtime, null);
    }

    public void dumpObjects(File target) {
        if (target == null || !target.exists()) {
            return;
        }

        dumpObjects(runtime, target.getAbsolutePath());
    }

    // will use stdout to print.
    public void dumpObjects() {
        dumpObjects(runtime, null);
    }

    private void checkSameThread() {
        boolean isSameThread = currentThreadId == Thread.currentThread().getId();
        if (!isSameThread) {
            throw new QuickJSException("Must be call same thread in QuickJSContext.create!");
        }
    }

    public long getCurrentThreadId() {
        return currentThreadId;
    }

    public ModuleLoader getModuleLoader() {
        return moduleLoader;
    }

    public void setModuleLoader(ModuleLoader moduleLoader) {
        checkSameThread();
        checkDestroyed();

        if (moduleLoader == null) {
            throw new NullPointerException("The moduleLoader can not be null!");
        }

        this.moduleLoader = moduleLoader;
    }

    private void checkDestroyed() {
        if (destroyed) {
            throw new QuickJSException("Can not called this after QuickJSContext was destroyed!");
        }
    }

    public JSObject getGlobalObject() {
        checkSameThread();
        checkDestroyed();
        return getGlobalObject(context);
    }

    public void destroy() {
        checkSameThread();
        checkDestroyed();

        nativeCleaner.forceClean();
        callFunctionMap.clear();
        destroyContext(context);
        destroyed = true;
    }

    public String stringify(JSObject jsObj) {
        checkSameThread();
        checkDestroyed();
        return stringify(context, jsObj.getPointer());
    }

    public boolean contains(JSObject jsObj, String key) {
        checkSameThread();
        checkDestroyed();
        return contains(context, jsObj.getPointer(), key);
    }

    public String[] getKeys(JSObject jsObj) {
        checkSameThread();
        checkDestroyed();
        return getKeys(context, jsObj.getPointer());
    }

    public void arrayAdd(JSObject jsObj, Object value) {
        checkSameThread();
        checkDestroyed();
        arrayAdd(context, jsObj.getPointer(), value);
    }

    public Object get(JSObject jsObj, String name) {
        checkSameThread();
        checkDestroyed();
        return getProperty(context, jsObj.getPointer(), name);
    }

    public void set(JSObject jsObj, String name, Object value) {
        checkSameThread();
        checkDestroyed();

        if (value instanceof JSCallFunction) {
            // Todo 优化：可以只传 callFunctionId 给到 JNI.
            putCallFunction((JSCallFunction) value);
        }

        setProperty(context, jsObj.getPointer(), name, value);
    }

    private void putCallFunction(JSCallFunction callFunction) {
        int callFunctionId = callFunction.hashCode();
        callFunctionMap.put(callFunctionId, callFunction);
    }

    /**
     * 该方法只提供给 Native 层回调.
     *
     * @param callFunctionId JSCallFunction 对象标识
     */
    public void removeCallFunction(int callFunctionId) {
        callFunctionMap.remove(callFunctionId);
    }

    /**
     * 该方法只提供给 Native 层回调.
     *
     * @param callFunctionId JSCallFunction 对象标识
     * @param args           JS 到 Java 的参数映射
     */
    public Object callFunctionBack(int callFunctionId, Object... args) {
        checkSameThread();
        checkDestroyed();

        JSCallFunction callFunction = callFunctionMap.get(callFunctionId);
        Object ret = callFunction.call(args);
        if (ret instanceof JSCallFunction) {
            putCallFunction((JSCallFunction) ret);
        }

        return ret;
    }

    public void freeValue(JSObject jsObj) {
        checkSameThread();
        checkDestroyed();

        freeValue(context, jsObj.getPointer());
    }

    /**
     * @VisibleForTesting 该方法仅供单元测试使用
     */
    int getCallFunctionMapSize() {
        return callFunctionMap.size();
    }

    /**
     * Native 层注册的 JS 方法里的对象需要在其他地方使用，
     * 调用该方法进行计数加一增加引用，不然 JS 方法执行完会被回收掉。
     * 注意：不再使用的时候，调用对应的 {@link #freeDupValue(JSObject)} 方法进行计数减一。
     */
    private void dupValue(JSObject jsObj) {
        checkSameThread();
        checkDestroyed();

        dupValue(context, jsObj.getPointer());
    }

    /**
     * 引用计数减一，对应 {@link #dupValue(JSObject)}
     */
    private void freeDupValue(JSObject jsObj) {
        checkSameThread();
        checkDestroyed();

        freeDupValue(context, jsObj.getPointer());
    }

    public int length(JSArray jsArray) {
        checkSameThread();
        checkDestroyed();

        return length(context, jsArray.getPointer());
    }

    public Object get(JSArray jsArray, int index) {
        checkSameThread();
        checkDestroyed();

        return get(context, jsArray.getPointer(), index);
    }

    public void set(JSArray jsArray, Object value, int index) {
        checkSameThread();
        checkDestroyed();

        set(context, jsArray.getPointer(), value, index);
    }

    Object call(JSObject func, long objPointer, Object... args) {
        checkSameThread();
        checkDestroyed();

        for (Object arg : args) {
            if (arg instanceof JSCallFunction) {
                putCallFunction((JSCallFunction) arg);
            }
        }

        return call(context, func.getPointer(), objPointer, args);
    }

    /**
     * Automatically manage the release of objects，
     * the hold method is equivalent to call the
     * dupValue and freeDupValue methods with NativeCleaner.
     */
    public void hold(JSObject jsObj) {
        checkSameThread();
        checkDestroyed();

        dupValue(jsObj);
        nativeCleaner.register(jsObj, jsObj.getPointer());
    }

    public JSObject createJSObject() {
        return (JSObject) parse("{}");
    }

    public JSArray createJSArray() {
        return (JSArray) parse("[]");
    }

    public Object parse(String json) {
        checkSameThread();
        checkDestroyed();

        return parseJSON(context, json);
    }

    public byte[] compile(String source) {
        return compile(source, UNKNOWN_FILE);
    }

    public byte[] compile(String source, String fileName) {
        checkSameThread();
        checkDestroyed();

        return compile(context, source, fileName, false);
    }

    public byte[] compileModule(String source) {
        return compileModule(source, UNKNOWN_FILE);
    }

    public byte[] compileModule(String source, String fileName) {
        checkSameThread();
        checkDestroyed();

        return compile(context, source, fileName, true);
    }

    public Object execute(byte[] code) {
        return execute(code, UNKNOWN_FILE);
    }

    public Object execute(byte[] code, String fileName) {
        return execute(context, code, fileName, "default");
    }

    public Object gexecute(byte[] code, String moduleExtName) {
        return execute(context, code, UNKNOWN_FILE, moduleExtName);
    }

    public Object execute(byte[] code, String fileName, String moduleExtName) {
        checkSameThread();
        checkDestroyed();

        if (TextUtils.isEmpty(moduleExtName)) {
            moduleExtName = "default";
        }
        return execute(context, code, fileName, moduleExtName);
    }

    public Object evaluate(String script) {
        return evaluate(script, UNKNOWN_FILE);
    }

    public Object evaluate(String script, String fileName) {
        checkSameThread();
        checkDestroyed();
        return evaluate(context, script, fileName, "default", false);
    }

    public Object evaluateModule(String script, String fileName, String moduleExtName) {
        checkSameThread();
        checkDestroyed();

        if (TextUtils.isEmpty(moduleExtName)) {
            moduleExtName = "default";
        }
        return evaluate(context, script, fileName, moduleExtName, true);
    }

    public Object evaluateModule(String script, String fileName) {
        return evaluate(context, script, fileName, "default", true);
    }

    public Object evaluateModule(String script) {
        return evaluateModule(script, UNKNOWN_FILE);
    }

    public void throwJSException(String error) {
        // throw $error;
        String errorScript = "throw " + "\"" + error + "\"" + ";";
        evaluate(errorScript);
    }

    // runtime
    private native long createRuntime();

    private native void setMaxStackSize(long runtime, int size); // The default is 1024 * 256, and 0 means unlimited.

    private native boolean isLiveObject(long runtime, long objValue);

    private native void runGC(long runtime);

    private native void setMemoryLimit(long runtime, int size);

    private native void dumpMemoryUsage(long runtime, String fileName);

    private native void dumpObjects(long runtime, String fileName);

    // context
    private native long createContext(long runtime);

    private native Object evaluate(long context, String script, String fileName, String moduleext_name, boolean isModule);

    private native JSObject getGlobalObject(long context);

    private native Object call(long context, long func, long thisObj, Object[] args);

    private native Object getProperty(long context, long objValue, String name);

    private native void setProperty(long context, long objValue, String name, Object value);

    private native String stringify(long context, long objValue);

    private native int length(long context, long objValue);

    private native Object get(long context, long objValue, int index);

    private native void set(long context, long objValue, Object value, int index);

    private native void freeValue(long context, long objValue);

    private native void dupValue(long context, long objValue);

    private native void freeDupValue(long context, long objValue);

    private native Object parseJSON(long context, String json);

    private native byte[] compile(long context, String sourceCode, String fileName, boolean isModule); // Bytecode compile

    private native Object execute(long context, byte[] bytecode, String fileName, String moduleext_name); // Bytecode execute

    private native boolean contains(long context, long objValue, String key);

    private native String[] getKeys(long context, long objValue);

    private native void arrayAdd(long context, long objValue, Object value);

    // destroy context and runtime
    private native void destroyContext(long context);

    public interface Console {
        void log(String info);
    }

    public static abstract class DefaultModuleLoader extends ModuleLoader {
        @Override
        public boolean isBytecodeMode() {
            return false;
        }

        @Override
        public byte[] getModuleBytecode(String moduleName) {
            return null;
        }

        @Override
        public String moduleNormalizeName(String moduleBaseName, String moduleName) {
            return UriUtil.resolve(moduleBaseName, moduleName);
        }
    }

    public static abstract class BytecodeModuleLoader extends ModuleLoader {
        @Override
        public boolean isBytecodeMode() {
            return true;
        }

        @Override
        public String getModuleStringCode(String moduleName) {
            return null;
        }

        @Override
        public String moduleNormalizeName(String baseModuLeName, String moduleName) {
            return UriUtil.resolve(baseModuLeName, moduleName);
        }
    }
}
