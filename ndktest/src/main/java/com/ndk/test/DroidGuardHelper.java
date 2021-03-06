package com.ndk.test;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import dalvik.system.DexClassLoader;

/**
 * Created by apple on 16/12/8.
 */
public class DroidGuardHelper {


        private static final String TAG = "GmsDroidguardHelper";
        private static final String DG_CLASS_NAME = "com.google.ccc.abuse.droidguard.DroidGuard";
        private static final String DG_URL = "https://www.googleapis.com/androidantiabuse/v1/x/create?alt=PROTO&key=AIzaSyBofcZsgLSS7BOnBjZPEkk4rYwzOIz-lTI";
        private  Map<String, Class<?>> loadedClass = new HashMap<>();

        public  String guard(Context context,String androidId,String email,byte[] input,String type) throws Exception {
            String packageName = "com.google.android.gms";
            Bundle extras = new Bundle();
            extras.putString("dg_email",email);
            extras.putString("dg_androidId",androidId);
            extras.putString("dg_gmsCodeVersion","4323036");
            extras.putString("dg_package",packageName);
            Log.d(TAG,"输入大小:"+input.length);
            DroidGuard.SignedDGResponse signedDGResponse = DroidGuard.SignedDGResponse.parseFrom(input);
            Log.d(TAG,"datas:"+new String(signedDGResponse.getData().toByteArray()));
            byte[] data = signedDGResponse.getData().toByteArray();
            DroidGuard.DGResponse dgResponse = DroidGuard.DGResponse.parseFrom(data);
            Log.d(TAG,"dgData:"+new String(dgResponse.getByteCode().toByteArray()));
            Log.d(TAG,"dgCheckSum:"+new String(dgResponse.getVmChecksum().toByteArray()));
            com.google.ccc.abuse.droidguard.DroidGuard droidGuard = new com.google.ccc.abuse.droidguard.DroidGuard(context,type,dgResponse.getByteCode().toByteArray(),new Callback(packageName,androidId));
            final Map<String, String> map = new HashMap<>();
            if (extras != null) {
                for (String key : extras.keySet()) {
                    String val = extras.getString(key);
                    if (val != null) map.put(key, val);
                }
            }
            byte[] resultBytes = droidGuard.run(map);
            String result = Base64.encodeToString(resultBytes, 11);
            Log.d(TAG,"droidGuard result:"+result);
            return result;
        }


    public  String guardFromFile(Context context,String androidId,String email,byte[] input,String type) throws Exception {
        String packageName = "com.google.android.gms";
        Bundle extras = new Bundle();
        extras.putString("dg_email",email);
        extras.putString("dg_androidId",androidId);
        extras.putString("dg_gmsCodeVersion","4323036");
        extras.putString("dg_package",packageName);
        Log.d(TAG,"输入大小:"+input.length);
        DroidGuard.SignedDGResponse signedDGResponse = DroidGuard.SignedDGResponse.parseFrom(input);
        Log.d(TAG,"datas:"+new String(signedDGResponse.getData().toByteArray()));
        byte[] data = signedDGResponse.getData().toByteArray();
        DroidGuard.DGResponse dgResponse = DroidGuard.DGResponse.parseFrom(data);
        Log.d(TAG,"dgData:"+new String(dgResponse.getByteCode().toByteArray()));
        Log.d(TAG,"dgCheckSum:"+new String(dgResponse.getVmChecksum().toByteArray()));
        com.google.ccc.abuse.droidguard.DroidGuard droidGuard = new com.google.ccc.abuse.droidguard.DroidGuard(context,type,dgResponse.getByteCode().toByteArray(),new Callback(packageName,androidId));
        final Map<String, String> map = new HashMap<>();
        if (extras != null) {
            for (String key : extras.keySet()) {
                String val = extras.getString(key);
                if (val != null) map.put(key, val);
            }
        }
        byte[] resultBytes = droidGuard.run(map);
        String result = Base64.encodeToString(resultBytes, 11);
        Log.d(TAG,"droidGuard result:"+result);
        return result;
    }

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public  byte[] invoke(Context context, Class<?> clazz, String packageName, String type, byte[] byteCode, String androidIdLong, Bundle extras) throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
            Object instance = clazz
                    .getDeclaredConstructor(Context.class, String.class, byte[].class)
                    .newInstance(context, type, byteCode);
            Log.d(TAG,"clazz invoke");
            final Map<String, String> map = new HashMap<>();
            if (extras != null) {
                for (String key : extras.keySet()) {
                    String val = extras.getString(key);
                    if (val != null) map.put(key, val);
                }
            }
//            Log.d(TAG,">...");
//            try {
//                Thread.sleep(10000);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            byte[] result= (byte[]) clazz.getMethod("run",Map.class).invoke(instance,map);
            Log.d(TAG,"result:"+result);
            return null;
        }




        private  void downloadAndPrepareDg(DroidGuard.DGResponse response, File dgCacheDir, File dgDir, File dgFile) throws IOException {
            String fileName = new String(response.getVmChecksum().toByteArray());
            Log.d(TAG,"name:"+fileName);
            File dgCacheFile = new File(dgCacheDir,   "child.apk");
            if (!dgFile.exists()) {

                    Log.d(TAG, "Downloading DG implementation from " + response.getVmUrl() + " to " + dgCacheFile);
                    InputStream is = new URL(response.getVmUrl()).openStream();
                    streamToFile(is, dgCacheFile);

                new File(dgDir, "opt").mkdirs();
                File libDir = new File(dgDir, "lib");
                libDir.mkdirs();
                ZipFile zipFile = new ZipFile(dgCacheFile);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (!entry.isDirectory()) {
                        String name = entry.getName();
                        String targetName;
                        if (name.startsWith("lib/" + Build.CPU_ABI + "/")) {
                            targetName = name.substring(Build.CPU_ABI.length() + 5);
                        } else if (name.startsWith("lib/" + Build.CPU_ABI2 + "/")) {
                            targetName = name.substring(Build.CPU_ABI2.length() + 5);
                        } else {
                            continue;
                        }
                        streamToFile(zipFile.getInputStream(entry), new File(libDir, targetName));
                    }
                }
                dgCacheFile.renameTo(dgFile);
            } else {
                Log.d(TAG, "Using cached file from " + dgFile);
            }
        }

        private  void streamToFile(InputStream is, File targetFile) throws IOException {
            FileOutputStream fos = new FileOutputStream(targetFile);
            byte[] bytes = new byte[1024];
            while (true) {
                int n = is.read(bytes);
                if (n < 0) {
                    break;
                }
                fos.write(bytes, 0, n);
            }
            is.close();
            fos.close();
        }

        private  String getArch() {
            try {
                return System.getProperty("os.arch");
            } catch (Exception ex) {
                return "";
            }
        }


        private  byte[] readStreamToEnd(final InputStream is) throws IOException {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            if (is != null) {
                final byte[] buff = new byte[1024];
                int read;
                do {
                    bos.write(buff, 0, (read = is.read(buff)) < 0 ? 0 : read);
                } while (read >= 0);
                is.close();
            }
            return bos.toByteArray();
        }

        private  String getBytesAsString(byte[] bytes) {
            if (bytes == null) return "null";
            try {
                CharsetDecoder d = Charset.forName("US-ASCII").newDecoder();
                CharBuffer r = d.decode(ByteBuffer.wrap(bytes));
                return r.toString();
            } catch (Exception e) {
                return Base64.encodeToString(bytes, Base64.NO_WRAP);
            }
        }

        public  final class Callback {

            private String packageName;
            private String androidIdLong;

            public Callback(String packageName, String androidIdLong) {
                this.packageName = packageName;
                this.androidIdLong = androidIdLong;
            }

            public final String a(final byte[] array) {
                String guasso = new String(DroidguassoHelper.guasso(array));
                Log.d(TAG, "a: " + Base64.encodeToString(array, Base64.NO_WRAP) + " -> " + guasso);
                return guasso;
            }

            public final String b() {
                Log.d(TAG, "b -> " + androidIdLong);
                return androidIdLong;
            }

            public final String c() {
                Log.d(TAG, "c -> " + packageName);
                return packageName;
            }

            public final void d(final Object o, final byte[] array) {
                Log.d(TAG, "d: " + o + ", " + Base64.encodeToString(array, Base64.NO_WRAP));
            }
        }

}
