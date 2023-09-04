package com.github.tvbox.osc.bbox.util;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * 字符集工具类，提供了检测字符集的工具方法
 * 首先当然是使用mozilla的开源工具包universalchardet进行字符集检测，对于检测失败的，使用中文常用字进行再次检测
 */
public class CharsetUtils {

    /**
     * 中文常用字符集
     */
    public static final String[] AVAILABLE_CHINESE_CHARSET_NAMES = new String[] { "GBK", "gb2312", "GB18030", "UTF-8", "Big5" };

    /**
     * 中文常用字
     */
    private static final Pattern CHINESE_COMMON_CHARACTER_PATTERN = Pattern.compile("的|一|是|了|我|不|人|在|他|有|这|个|上|们|来|到|时|大|地|为|子|中|你|说|生|国|年|着|就|那|和|要");

    public static Charset detect(byte[] content) {
        String charset = universalDetect(content);
        if (charset != null && !charset.isEmpty()) {
            return Charset.forName(charset);
        }

        int longestMatch = 0;
        for (String cs : AVAILABLE_CHINESE_CHARSET_NAMES) {
            String temp = new String(content, Charset.forName(cs));
            Matcher matcher = CHINESE_COMMON_CHARACTER_PATTERN.matcher(temp);

            int count = 0;
            while (matcher.find()) {
                count += 1;
            }
            if (count > longestMatch) {
                longestMatch = count;
                charset = cs;
            }
        }
        return charset == null ? Charset.forName("GB18030") : Charset.forName(charset);
    }

    /**
     * 使用mozilla的开源工具包universalchardet进行字符集检测，不一定能完全检测中文字符集
     */
    public static String universalDetect(byte[] content) {
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(content, 0, content.length);
        detector.dataEnd();
        return detector.getDetectedCharset();
    }
}