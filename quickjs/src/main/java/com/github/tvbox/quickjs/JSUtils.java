package com.github.tvbox.quickjs;


import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JSUtils {

    public static boolean isEmpty( CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty( CharSequence str) {
        return !isEmpty(str);
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    public static boolean isEmpty(Object obj) {
        if (obj == null) return true;
        else if (obj instanceof CharSequence) return ((CharSequence) obj).length() == 0;
        else if (obj instanceof Collection) return ((Collection) obj).isEmpty();
        else if (obj instanceof Map) return ((Map) obj).isEmpty();
        else if (obj.getClass().isArray()) return Array.getLength(obj) == 0;

        return false;
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    private static final String U2028 = new String(new byte[]{ (byte)0xE2, (byte)0x80, (byte)0xA8 });
    private static final String U2029 = new String(new byte[]{ (byte)0xE2, (byte)0x80, (byte)0xA9 });

    /**
     * Escape JavaString string
     * @param line unescaped string
     * @return escaped string
     */
    public static String escapeJavaScriptString(final String line)
    {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++)
        {
            char c = line.charAt(i);
            switch (c)
            {
                case '"':
                case '\'':
                case '\\':
                    sb.append('\\');
                    sb.append(c);
                    break;

                case '\n':
                    sb.append("\\n");
                    break;

                case '\r':
                    sb.append("\\r");
                    break;

                default:
                    sb.append(c);
            }
        }

        return sb.toString()
                .replace(U2028, "\u2028")
                .replace(U2029, "\u2029");
    }

    public static String getBaseUrl(String url) {
        if (isEmpty(url)) {
            return url;
        }
        String baseUrls = url.replace("http://", "").replace("https://", "");
        String baseUrl2 = baseUrls.split("/")[0];
        String baseUrl;
        if (url.startsWith("https")) {
            baseUrl = "https://" + baseUrl2;
        } else {
            baseUrl = "http://" + baseUrl2;
        }
        return baseUrl;
    }


    public static String arrayToString(String[] list, int fromIndex, String cha) {
        return arrayToString(list, fromIndex, list == null ? 0 : list.length, cha);
    }

    public static String arrayToString(String[] list, int fromIndex, int endIndex, String cha) {
        StringBuilder builder = new StringBuilder();
        if (list == null || list.length <= fromIndex) {
            return "";
        } else if (list.length <= 1) {
            return list[0];
        } else {
            builder.append(list[fromIndex]);
        }
        for (int i = 1 + fromIndex; i < list.length && i < endIndex; i++) {
            builder.append(cha).append(list[i]);
        }
        return builder.toString();
    }

    public static String listToString(List<String> list, String cha) {
        StringBuilder builder = new StringBuilder();
        if (list == null || list.size() <= 0) {
            return "";
        } else if (list.size() <= 1) {
            return list.get(0);
        } else {
            builder.append(list.get(0));
        }
        for (int i = 1; i < list.size(); i++) {
            builder.append(cha).append(list.get(i));
        }
        return builder.toString();
    }

    public static String listToString(List<String> list, int fromIndex, String cha) {
        StringBuilder builder = new StringBuilder();
        if (list == null || list.size() <= fromIndex) {
            return "";
        } else if (list.size() <= 1) {
            return list.get(0);
        } else {
            builder.append(list.get(fromIndex));
        }
        for (int i = fromIndex + 1; i < list.size(); i++) {
            builder.append(cha).append(list.get(i));
        }
        return builder.toString();
    }

    public static String listToString(List<String> list) {
        return listToString(list, "&&");
    }

    public static String trimBlanks(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        int len = str.length();
        int st = 0;

        while ((st < len) && (str.charAt(st) == '\n' || str.charAt(st) == '\r' || str.charAt(st) == '\f' || str.charAt(st) == '\t')) {
            st++;
        }
        while ((st < len) && (str.charAt(len - 1) == '\n' || str.charAt(len - 1) == '\r' || str.charAt(len - 1) == '\f' || str.charAt(len - 1) == '\t')) {
            len--;
        }
        return ((st > 0) || (len < str.length())) ? str.substring(st, len) : str;
    }

}
