package com.ddf.fakeplayer.util;

import java.awt.*;
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
}
