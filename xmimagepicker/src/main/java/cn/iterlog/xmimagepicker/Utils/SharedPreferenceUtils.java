package cn.iterlog.xmimagepicker.Utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SharedPreferenceUtils {
    private static final String TAG = "SharedPreferenceUtils";
    private Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public SharedPreferenceUtils(Context context) {
        this(context, PreferenceManager.getDefaultSharedPreferences(context));
    }

    public SharedPreferenceUtils(Context context, String filename) {
        this(context, context.getSharedPreferences(filename, 2));
    }

    public SharedPreferenceUtils(Context context, SharedPreferences sp) {
        this.sp = null;
        this.editor = null;
        this.context = context;
        this.sp = sp;
        this.editor = sp.edit();
    }

    public static String getDateByNumber() {
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd", new Locale("zh"));
        String cur = s.format(new Date());
        return cur;
    }

    public SharedPreferences getInstance() {
        return this.sp;
    }

    public void setValue(String key, boolean value) {
        this.editor.putBoolean(key, value);
        this.editor.commit();
    }

    public void setValue(int resKey, boolean value) {
        this.setValue(this.context.getString(resKey), value);
    }

    public void setValue(String key, float value) {
        this.editor.putFloat(key, value);
        this.editor.commit();
    }

    public void setValue(int resKey, float value) {
        this.setValue(this.context.getString(resKey), value);
    }

    public void setValue(String key, int value) {
        this.editor.putInt(key, value);
        this.editor.commit();
    }

    public void setValue(int resKey, int value) {
        this.setValue(this.context.getString(resKey), value);
    }

    public void setValue(String key, long value) {
        this.editor.putLong(key, value);
        this.editor.commit();
    }

    public void setValue(int resKey, long value) {
        this.setValue(this.context.getString(resKey), value);
    }

    public void setValue(String key, String value) {
        this.editor.putString(key, value);
        this.editor.commit();
    }

    public void setValue(int resKey, String value) {
        this.setValue(this.context.getString(resKey), value);
    }

    public boolean getValue(String key, boolean defaultValue) {
        return this.sp.getBoolean(key, defaultValue);
    }

    public boolean getValue(int resKey, boolean defaultValue) {
        return this.getValue(this.context.getString(resKey), defaultValue);
    }

    public float getValue(String key, float defaultValue) {
        return this.sp.getFloat(key, defaultValue);
    }

    public float getValue(int resKey, float defaultValue) {
        return this.getValue(this.context.getString(resKey), defaultValue);
    }

    public int getValue(String key, int defaultValue) {
        return this.sp.getInt(key, defaultValue);
    }

    public int getValue(int resKey, int defaultValue) {
        return this.getValue(this.context.getString(resKey), defaultValue);
    }

    public long getValue(String key, long defaultValue) {
        return this.sp.getLong(key, defaultValue);
    }

    public long getValue(int resKey, long defaultValue) {
        return this.getValue(this.context.getString(resKey), defaultValue);
    }

    public String getValue(String key, String defaultValue) {
        return this.sp.getString(key, defaultValue);
    }

    public String getValue(int resKey, String defaultValue) {
        return this.getValue(this.context.getString(resKey), defaultValue);
    }

    public void remove(String key) {
        this.editor.remove(key);
        this.editor.commit();
    }

    public void clear() {
        this.editor.clear();
        this.editor.commit();
    }

    public boolean isFirstStart(Context context) {
        try {
            PackageInfo e = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int curVersion = e.versionCode;
            int lastVersion = this.sp.getInt("version", 0);
            return curVersion > lastVersion;
        } catch (PackageManager.NameNotFoundException var5) {
            return false;
        }
    }

    public boolean isFirstInstall(Context context) {
        int install = this.sp.getInt("first_install", 0);
        return install == 0;
    }

    public void setStarted(Context context) {
        try {
            PackageInfo e = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int curVersion = e.versionCode;
            this.sp.edit().putInt("version", curVersion).commit();
        } catch (PackageManager.NameNotFoundException var4) {
        }

    }

    public void setInstalled(Context context) {
        this.sp.edit().putInt("first_install", 1).commit();
    }

    public boolean needChangeIndexContent(Context context, String openID) {
        String save = this.sp.getString(openID, "");
        String cur = getDateByNumber();
        return !save.equals(cur);
    }

    public void saveChangeIndexContent(Context context, String openID) {
        String cur = getDateByNumber();
        this.sp.edit().putString(openID, cur).commit();
    }

    /**
     * 根据key和预期的value类型获取value的值
     *
     * @param key
     * @param clazz
     * @return
     */
    public <T> T getValue(String key, Class<T> clazz) {
        if (context == null) {
            throw new RuntimeException("请先调用带有context，name参数的构造！");
        }
        return getValue(key, clazz, sp);
    }

    /**
     * 针对复杂类型存储<对象>
     *
     * @param key
     * @param object
     */
    public void setObject(String key, Object object) {

        //创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //创建字节对象输出流
        ObjectOutputStream out = null;
        try {
            //然后通过将字对象进行64转码，写入key值为key的sp中
            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, objectVal);
            editor.commit();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(String key, Class<T> clazz) {
        if (sp.contains(key)) {
            String objectVal = sp.getString(key, null);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            //一样通过读取字节流，创建字节流输入流，写入对象并作强制转换
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 对于外部不可见的过渡方法
     *
     * @param key
     * @param clazz
     * @param sp
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> T getValue(String key, Class<T> clazz, SharedPreferences sp) {
        T t;
        try {

            t = clazz.newInstance();

            if (t instanceof Integer) {
                return (T) Integer.valueOf(sp.getInt(key, 0));
            } else if (t instanceof String) {
                return (T) sp.getString(key, "");
            } else if (t instanceof Boolean) {
                return (T) Boolean.valueOf(sp.getBoolean(key, false));
            } else if (t instanceof Long) {
                return (T) Long.valueOf(sp.getLong(key, 0L));
            } else if (t instanceof Float) {
                return (T) Float.valueOf(sp.getFloat(key, 0L));
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
            Log.e("system", "类型输入错误或者复杂类型无法解析[" + e.getMessage() + "]");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.e("system", "类型输入错误或者复杂类型无法解析[" + e.getMessage() + "]");
        }
        Log.e("system", "无法找到" + key + "对应的值");
        return null;
    }


}
