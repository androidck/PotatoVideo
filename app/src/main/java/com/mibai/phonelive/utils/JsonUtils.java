package com.mibai.phonelive.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @author Daijiawe
 * @description json转换工具类
 * @date 2016/07/11 19:05
 */
public class JsonUtils {

    private static Gson mGson = new Gson();

    /**
     * 将对象准换为json字符串
     *
     * @param object
     * @param <T>
     * @return
     */
    public static <T> String serialize(T object) {
        return mGson.toJson(object);
    }

    /**
     * 将json字符串转换为对象
     *
     * @param json
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> T deserialize(String json, Class<T> clz)
            throws JsonSyntaxException {
        return mGson.fromJson(json, clz);
    }

    /**
     * 将json对象转换为实体对象
     *
     * @param json
     * @param clz
     * @param <T>
     * @return
     * @throws JsonSyntaxException
     */
    public static <T> T deserialize(JsonObject json, Class<T> clz)
            throws JsonSyntaxException {
        return mGson.fromJson(json, clz);
    }

    /**
     * 将json字符串转换为对象
     *
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T deserialize(String json, Type type)
            throws JsonSyntaxException {
        return mGson.fromJson(json, type);
    }


    /**
     * 解析成数组
     *
     * @param json
     * @param clazz
     * @return
     */
    public static <T> ArrayList<T> deserializeToArrayList(String json, Class<T> clazz) {
        ArrayList<T> arrayList = new ArrayList<T>();
        try {
            Type type = new TypeToken<ArrayList<JsonObject>>() {
            }.getType();
            ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);

            for (JsonObject jsonObject : jsonObjects) {
                arrayList.add(new Gson().fromJson(jsonObject, clazz));
            }
        } catch (Exception e) {
            Log.e("JsonUtils", e.getMessage());
        }
        return arrayList;
    }

    /**
     * 将一个数组型json字符串转换成包含子json字符串的List集合
     *
     * @param json 给定的JSON字符串
     * @return 返回一个List集合，包含一组字符串，对应于给定原始JSON数据内元素的字符串形式
     */
    public static ArrayList<String> toJsonStrList(String json) {
        ArrayList<String> strList = new ArrayList<String>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                String jsonStr = jsonArray.getString(i);
                strList.add(jsonStr);
            }
        } catch (JSONException e) {
            Log.e("JsonUtils", e.getMessage());
        }
        return strList;
    }

    public static String getResponseResult(String json) {
        String result = getResponseByKey(json, "result");
        return result;
    }

    /**
     * 获取指定的值
     *
     * @param json
     * @return
     */
    public static String getResponseByKey(String json, String key) {
        String value = "";
        try {
            JSONObject obj = new JSONObject(json);
            value = obj.optString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取指定的布尔值
     *
     * @param json
     * @param key
     * @return
     */
    public static boolean getResponseBooleanByKey(String json, String key) {
        boolean value = false;
        try {
            JSONObject obj = new JSONObject(json);
            value = obj.optBoolean(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取指定的int值
     * @param json
     * @param key
     * @return
     */
    public static int getResponseIntByKey(String json, String key) {
        int value = 0;
        try {
            JSONObject obj = new JSONObject(json);
            value = obj.optInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取data值
     *
     * @param json
     * @return
     */
    public static String getResponseAddressData(String json) {
        String value = "";
        try {
            JSONObject obj = new JSONObject(json);
            value = obj.optString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取错误信息
     *
     * @param json
     * @return
     */
    public static String getErrorMessage(String json) {
        String message = getResponseByKey(json, "message");
        return message;
    }

    /**
     * 根据JSON字段解析为字符串
     *
     * @param json
     * @param key
     * @return
     */
    public static String getJsonValueFromDataByKey(String json, String key) {
        String value = "";
        try {
            JSONObject obj = new JSONObject(json);
            JSONObject items = obj.optJSONObject("data");
            value = items.optString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }


    /**
     * 从Asset文件获取json数据
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 转化为Json对象
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        return new Gson().toJson(obj);
    }

}
