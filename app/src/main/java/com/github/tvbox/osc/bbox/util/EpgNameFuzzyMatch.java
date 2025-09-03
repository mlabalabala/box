package com.github.tvbox.osc.bbox.util;

import android.content.res.AssetManager;
import com.github.tvbox.osc.bbox.base.App;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.GetRequest;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Hashtable;

public class EpgNameFuzzyMatch {

    private static JsonObject epgNameDoc = null;
    private static Hashtable hsEpgName = new Hashtable();

    public static void init() {
        if(epgNameDoc != null)
            return;
        Gson gson = new Gson();
        try {
            AssetManager assetManager = App.getInstance().getAssets(); //获得assets资源管理器（assets中的文件无法直接访问，可以使用AssetManager访问）
            InputStreamReader inputStreamReader = new InputStreamReader(assetManager.open("Roinlong_Epg.json"),"UTF-8"); //使用IO流读取json文件内容
            BufferedReader br = new BufferedReader(inputStreamReader);//使用字符高效流
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine())!=null){
                builder.append(line);
            }
            br.close();
            inputStreamReader.close();
            if(!builder.toString().isEmpty()){
                JsonObject  jsonObj =  gson.fromJson(builder.toString(), (Type)JsonObject.class);// 从builder中读取了json中的数据。
                //  JSONObject testJson = new JSONObject(builder.toString()); // 从builder中读取了json中的数据。
                epgNameDoc = jsonObj;
                hasAddData(epgNameDoc);
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //上述两种途径都失败后,读取网络自定义文件中的内容
        GetRequest<String> request = OkGo.<String>get("http://www.baidu.com/maotv/epg.json");
        request.headers("User-Agent", UA.random());
        request.execute(new AbsCallback<String>() {
            @Override
            public void onSuccess(Response<String> response) {
                JSONObject returnedData = new JSONObject();
                try {
                    String pageStr = response.body();
                    JsonObject infoJson = gson.fromJson(pageStr, (Type)JsonObject.class);
                    epgNameDoc = infoJson;
                    hasAddData(epgNameDoc);
                    return;
                } catch (Exception ex) { }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public String convertResponse(okhttp3.Response response) throws Throwable {
                return response.body().string();
            }
        });
    }




    public static void hasAddData(JsonObject epgNameDoc){
        for (JsonElement opt : epgNameDoc.get("epgs").getAsJsonArray()) {
            JsonObject obj = (JsonObject) opt;
            String name = obj.get("name").getAsString().trim();
            String[] names  = name.split(",");
            for (String string : names) {
                hsEpgName.put(string,obj);
            }
        }
    }

    public static JsonObject getEpgNameInfo(String channelName) {

        if(hsEpgName.containsKey(channelName)){
            JsonObject obj = (JsonObject)hsEpgName.get(channelName);
            return  obj;
        }
        return null;
    }




}
