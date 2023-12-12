package com.github.tvbox.osc.bbox.util.live;

import com.github.tvbox.osc.bbox.util.LOG;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TxtSubscribe {
    public static void parse(LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> linkedHashMap, String text) {
        if (text.trim().startsWith("#EXTM3U")) parseM3u(linkedHashMap, text);
        else parseTxt(linkedHashMap, text);
    }


    private static final Pattern GROUP = Pattern.compile(".*group-title=\"(.?|.+?)\".*");
    private static final Pattern LOGO = Pattern.compile(".*tvg-logo=\"(.?|.+?)\".*");
    private static final Pattern NAME = Pattern.compile(".*,(.+?)$");

    private static String extract(String line, Pattern pattern) {
        Matcher matcher = pattern.matcher(line.trim());
        if (matcher.matches()) return matcher.group(1);
        return "";
    }

    public static void parseM3u(LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> linkedHashMap, String text) {
        ArrayList<String> playSource;
        LinkedHashMap<String, ArrayList<String>> channels = new LinkedHashMap<>();
        LinkedHashMap<String, ArrayList<String>> channelsTemp = channels;
        String group = "";
        String name = "";
        String logo = "";
        for (String line : text.split("\n")) {

            if (Thread.interrupted()) break;
            if (line.startsWith("#EXTINF:")) {
                LOG.i(String.format("GROUP: %s, NAME: %s, LOGO: %s\n", extract(line, GROUP), extract(line, NAME), extract(line, LOGO)));
                group = extract(line, GROUP);
                name = extract(line, NAME);
                logo = extract(line, LOGO);

                if (linkedHashMap.containsKey(group)) {
                    channelsTemp = linkedHashMap.get(group);
                }
                else {
                    channelsTemp = new LinkedHashMap<>();
                    linkedHashMap.put(group, channelsTemp);
                }
            } else if (line.contains("://")) {
                LOG.i(line);
                assert channelsTemp != null;
                if (channelsTemp.containsKey(name)) {
                    playSource = channelsTemp.get(name);
                }
                else {
                    playSource = new ArrayList<>();
                    channelsTemp.put(name, playSource);
                }

                assert playSource != null;
                if (!playSource.contains(line.trim()))
                    playSource.add(line.trim());

            }
        }

        if (channels.isEmpty()) {
            return;
        }
        linkedHashMap.put("未分组", channels);
    }

    public static void parseTxt(LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> linkedHashMap, String str) {

        ArrayList<String> arrayList;
        try {
            BufferedReader bufferedReader = new BufferedReader(new StringReader(str));
            String readLine = bufferedReader.readLine();
            LinkedHashMap<String, ArrayList<String>> linkedHashMap2 = new LinkedHashMap<>();
            LinkedHashMap<String, ArrayList<String>> linkedHashMap3 = linkedHashMap2;
            while (readLine != null) {
                if (readLine.trim().isEmpty()) {
                    readLine = bufferedReader.readLine();
                } else {
                    String[] split = readLine.split(",");
                    if (split.length < 2) {
                        readLine = bufferedReader.readLine();
                    } else {
                        if (readLine.contains("#genre#")) {
                            String trim = split[0].trim();
                            if (!linkedHashMap.containsKey(trim)) {
                                linkedHashMap3 = new LinkedHashMap<>();
                                linkedHashMap.put(trim, linkedHashMap3);
                            } else {
                                linkedHashMap3 = linkedHashMap.get(trim);
                            }
                        } else {
                            String trim2 = split[0].trim();
                            for (String str2 : split[1].trim().split("#")) {
                                String trim3 = str2.trim();
                                if (!trim3.isEmpty() && (trim3.startsWith("http") || trim3.startsWith("rtp") || trim3.startsWith("rtsp") || trim3.startsWith("rtmp"))) {
                                    if (!linkedHashMap3.containsKey(trim2)) {
                                        arrayList = new ArrayList<>();
                                        linkedHashMap3.put(trim2, arrayList);
                                    } else {
                                        arrayList = linkedHashMap3.get(trim2);
                                    }
                                    if (!arrayList.contains(trim3)) {
                                        arrayList.add(trim3);
                                    }
                                }
                            }
                        }
                        readLine = bufferedReader.readLine();
                    }
                }
            }
            bufferedReader.close();
            if (linkedHashMap2.isEmpty()) {
                return;
            }
            linkedHashMap.put("未分组", linkedHashMap2);
        } catch (Throwable unused) {
        }
    }

    public static JsonArray live2JsonArray(LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> linkedHashMap) {
        JsonArray jsonarr = new JsonArray();
        for (String str : linkedHashMap.keySet()) {
            JsonArray jsonarr2 = new JsonArray();
            LinkedHashMap<String, ArrayList<String>> linkedHashMap2 = linkedHashMap.get(str);
            if (!linkedHashMap2.isEmpty()) {
                for (String str2 : linkedHashMap2.keySet()) {
                    ArrayList<String> arrayList = linkedHashMap2.get(str2);
                    if (!arrayList.isEmpty()) {
                        JsonArray jsonarr3 = new JsonArray();
                        for (int i = 0; i < arrayList.size(); i++) {
                            jsonarr3.add(arrayList.get(i));
                        }
                        JsonObject jsonobj = new JsonObject();
                        try {
                            jsonobj.addProperty("name", str2);
                            jsonobj.add("urls", jsonarr3);
                        } catch (Throwable e) {
                        }
                        jsonarr2.add(jsonobj);
                    }
                }
                JsonObject jsonobj2 = new JsonObject();
                try {
                    jsonobj2.addProperty("group", str);
                    jsonobj2.add("channels", jsonarr2);
                } catch (Throwable e) {
                }
                jsonarr.add(jsonobj2);
            }
        }
        return jsonarr;
    }
}
