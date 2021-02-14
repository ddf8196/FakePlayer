package com.ddf.fakeplayer;

import com.ddf.fakeplayer.cli.CLIMain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Main {
    public static void main(String[] args) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        Path baseDir = Paths.get(".").toAbsolutePath();
        Path configDir = baseDir.resolve("config");
        Files.createDirectories(configDir);
        Path configPath = configDir.resolve("config.yaml");
        Config config = Config.load(configPath);
        System.out.println("配置文件已加载: " + configPath.toRealPath().toString());
        if (config.isUseGUI()) {
            //GUIMain.main(config);
        } else {
            CLIMain.main(config);
        }
    }
}
