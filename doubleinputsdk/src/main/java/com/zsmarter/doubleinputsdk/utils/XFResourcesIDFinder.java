package com.zsmarter.doubleinputsdk.utils;

import android.content.Context;
import android.content.res.Resources;

import java.lang.reflect.Field;

public final class XFResourcesIDFinder {
    public static String packageName;
    public static Resources resources;
    public static final String anim = "anim";
    public static final String animator = "animator";
    public static final String interpolator = "interpolator";
    public static final String menu = "menu";
    public static final String mipmap = "mipmap";
    public static final String array = "array";
    public static final String bool = "bool";
    public static final String stringArray = "string-array";
    public static final String attr = "attr";
    public static final String color = "color";
    public static final String dimen = "dimen";
    public static final String drawable = "drawable";
    public static final String id = "id";
    public static final String layout = "layout";
    public static final String raw = "raw";
    public static final String string = "string";
    public static final String style = "style";
    public static final String xml = "xml";
    public static final String styleable = "styleable";
    public static final String integer = "integer";
    public static final String integerArray = "integer-array";

    public XFResourcesIDFinder() {
    }

    public static void init(Context ctx) {
        packageName = ctx.getPackageName();
        resources = ctx.getResources();
    }

    public static int getResDrawableID(String resName) {
        return resources.getIdentifier(resName, "drawable", packageName);
    }

    public static int getResLayoutID(String resName) {
        return resources.getIdentifier(resName, "layout", packageName);
    }

    public static int getResAnimID(String resName) {
        return resources.getIdentifier(resName, "anim", packageName);
    }

    public static int getResAnimatorID(String resName) {
        return resources.getIdentifier(resName, "animator", packageName);
    }

    public static int getResAttrID(String resName) {
        return resources.getIdentifier(resName, "attr", packageName);
    }

    public static int getResColorID(String resName) {
        return resources.getIdentifier(resName, "color", packageName);
    }

    public static int getResDimenID(String resName) {
        return resources.getIdentifier(resName, "dimen", packageName);
    }

    public static int getResIdID(String resName) {
        return resources.getIdentifier(resName, "id", packageName);
    }

    public static int getResRawID(String resName) {
        return resources.getIdentifier(resName, "raw", packageName);
    }

    public static int getResStringID(String resName) {
        return resources.getIdentifier(resName, "string", packageName);
    }

    public static int getResStyleID(String resName) {
        return resources.getIdentifier(resName, "style", packageName);
    }

    public static int getResStyleableID(String name) {
        return resources.getIdentifier(name, "styleable", packageName);
    }

    public static int getResXmlID(String resName) {
        return resources.getIdentifier(resName, "xml", packageName);
    }

    public static int getResInterpolatorID(String resName) {
        return resources.getIdentifier(resName, "interpolator", packageName);
    }

    public static int getResMenuID(String resName) {
        return resources.getIdentifier(resName, "menu", packageName);
    }

    public static int getResMipmapID(String resName) {
        return resources.getIdentifier(resName, "mipmap", packageName);
    }

    public static int getResArrayID(String resName) {
        return resources.getIdentifier(resName, "array", packageName);
    }

    public static int getResBoolID(String resName) {
        return resources.getIdentifier(resName, "bool", packageName);
    }

    public static int getResStringArrayID(String resName) {
        return resources.getIdentifier(resName, "string-array", packageName);
    }

    public static String getString(String resName) {
        int id = getResStringID(resName);
        return resources.getString(id);
    }

    public static int getResIntID(String resName) {
        return resources.getIdentifier(resName, "integer", packageName);
    }

    public static int getResIntArrayID(String resName) {
        return resources.getIdentifier(resName, "integer-array", packageName);
    }

    public static int getInt(String resName) {
        int id = getResIntID(resName);
        return resources.getInteger(id);
    }

    public static int[] getIntArray(String resName) {
        int id = getResIntArrayID(resName);
        return resources.getIntArray(id);
    }

    public static int dipToPixels(int dip) {
        float density = resources.getDisplayMetrics().density;
        int valuePixels = (int)((float)dip * density + 0.5F);
        return valuePixels;
    }

    public static int getAppIcon() {
        return getResDrawableID("uz_icon");
    }

    public static String getAppName() {
        return getString("app_name");
    }

    /**

     * 对于 context.getResources().getIdentifier 无法获取的数据 , 或者数组

     * 资源反射值

     * @paramcontext

     * @param name

     * @param type

     * @return

     */

    private static Object getResourceId(Context context,String name, String type) {

        String className = context.getPackageName() +".R";

        try {

            Class<?> cls = Class.forName(className);

            for (Class<?> childClass : cls.getClasses()) {

                String simple = childClass.getSimpleName();

                if (simple.equals(type)) {

                    for (Field field : childClass.getFields()) {

                        String fieldName = field.getName();

                        if (fieldName.equals(name)) {

                            System.out.println(fieldName);

                            return field.get(null);

                        }

                    }

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;

    }
    /**

     * 获取 styleable 的 ID 号数组

     * @paramcontext

     * @param name

     * @return

     */

    public static int[] getStyleableArray(Context context,String name) {

        return (int[])getResourceId(context, name,"styleable");

    }
}


