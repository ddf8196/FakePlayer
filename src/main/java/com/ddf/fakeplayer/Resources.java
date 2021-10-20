package com.ddf.fakeplayer;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Resources {
    public static Properties VERSION_PROPERTIES;
    public static String SKIN_DATA_STEVE_JSON;
    public static String SKIN_DATA_ALEX_JSON;
    public static String SKIN_DATA_CUSTOM_JSON;
    public static String SKIN_DATA_CUSTOM_SLIM_JSON;
    public static Image ICON;

    static {
        try {
            VERSION_PROPERTIES = new Properties();
            VERSION_PROPERTIES.load(new InputStreamReader(getResAsStream("/version.properties"), StandardCharsets.UTF_8));
            SKIN_DATA_STEVE_JSON = getResAsString("/skin/skin_data_steve.json");
            SKIN_DATA_ALEX_JSON = getResAsString("/skin/skin_data_alex.json");
            SKIN_DATA_CUSTOM_JSON = getResAsString("/skin/skin_data_custom.json");
            SKIN_DATA_CUSTOM_SLIM_JSON = getResAsString("/skin/skin_data_custom_slim.json");
//            ICON = ImageIO.read(getRes("/icon.png"));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static String getResAsString(String path) throws IOException {
        return new String(getResAsBytes(path));
    }

    private static byte[] getResAsBytes(String path) throws IOException {
        InputStream is = getResAsStream(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(is.available());
        byte[] buf = new byte[1024];
        int n;
        while ((n = is.read(buf)) > 0){
            baos.write(buf, 0, n);
        }
        return baos.toByteArray();
    }

    private static InputStream getResAsStream(String path) {
        return Resources.class.getResourceAsStream(path);
    }

    private static URL getRes(String path) {
        return Resources.class.getResource(path);
    }
}
