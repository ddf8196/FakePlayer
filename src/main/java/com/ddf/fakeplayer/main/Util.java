package com.ddf.fakeplayer.main;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;

public class Util {
    public static boolean isValidPort(int port) {
        return 1 <= port && port <= 65535;
    }

    public static boolean tryOpenBrowser(String url) {
        if(Desktop.isDesktopSupported()){
            try {
                URI uri = URI.create(url);
                Desktop desktop = Desktop.getDesktop();
                if(desktop.isSupported(Desktop.Action.BROWSE)){
                    desktop.browse(uri);
                    return true;
                }
            } catch (Exception ignored) { }
        }
        return false;
    }

//    public static boolean isSystemTraySupported() {
//        return SystemTray.isSupported();
//    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
