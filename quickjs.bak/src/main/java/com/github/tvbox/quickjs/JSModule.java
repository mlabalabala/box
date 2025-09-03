package com.github.tvbox.quickjs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Harlon Wang on 2021/10/12.
 */
public final class JSModule {

    private static Loader sModuleLoader;

    public interface Loader {
        String getModuleScript(String moduleName);
    }

    public static void setModuleLoader(Loader sModuleLoader) {
        JSModule.sModuleLoader = sModuleLoader;
    }

    static String getModuleScript(String moduleName) {
        return sModuleLoader.getModuleScript(moduleName);
    }

    static boolean isRemote(String name) {
        return name.startsWith("http://") || name.startsWith("https://") || name.startsWith("assets://");
    }

    public static String convertModuleName(String moduleBaseName, String moduleName) {
        if (moduleName == null || moduleName.length() == 0) {
            return moduleName;
        }
        if (isRemote(moduleName))
            return moduleName;
        moduleName = moduleName.replace("//", "/");
        if (moduleName.startsWith("./")) {
            moduleName = moduleName.substring(2);
        }
        if (moduleName.charAt(0) == '/') {
            return moduleName;
        }
        if (moduleBaseName == null || moduleBaseName.length() == 0) {
            return moduleName;
        }
        if (!isRemote(moduleBaseName))
            moduleBaseName = moduleBaseName.replace("//", "/");
        if (moduleBaseName.startsWith("./")) {
            moduleBaseName = moduleBaseName.substring(2);
        }
        if (moduleBaseName.equals("/")) {
            return "/" + moduleName;
        }
        if (moduleBaseName.endsWith("/")) {
            return moduleBaseName + moduleName;
        }
        String[] parentSplit = moduleBaseName.split("/");
        String[] pathSplit = moduleName.split("/");
        List<String> parentStack = new ArrayList<>();
        List<String> pathStack = new ArrayList<>();
        Collections.addAll(parentStack, parentSplit);
        Collections.addAll(pathStack, pathSplit);
        while (!pathStack.isEmpty()) {
            String tmp = pathStack.get(0);
            if (tmp.equals("..")) {
                pathStack.remove(0);
                parentStack.remove(parentStack.size() - 1);
            } else {
                break;
            }
        }
        if (!parentStack.isEmpty()) {
            parentStack.remove(parentStack.size() - 1);
        }
        StringBuilder builder = new StringBuilder();
        if (moduleBaseName.startsWith("/")) {
            builder.append("/");
        }
        for (String it : parentStack) {
            builder.append(it).append("/");
        }
        for (String it : pathStack) {
            builder.append(it).append("/");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

}
