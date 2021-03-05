package com.ddf.fakeplayer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleProperties {
    private final List<Line> lines = new ArrayList<>();
    private final Map<String, Line> propertyLines = new HashMap<>();

    public void load(String text) throws IOException {
        load(new BufferedReader(new StringReader(text)));
    }

    public void load(Path path) throws IOException {
        load(new String(Files.readAllBytes(path), StandardCharsets.UTF_8));
    }

    public void load(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            putLine(line);
        }
    }

    public void save(OutputStream outputStream) throws IOException {
        outputStream.write(toString().getBytes(StandardCharsets.UTF_8));
    }

    public void save(Path path) throws IOException {
        Files.write(path, toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    public String getProperty(String key) {
        Line line = propertyLines.get(key);
        if (line != null) {
            return line.getValue();
        }
        return null;
    }

    public void putProperty(String key, String value) {
        Line line = propertyLines.get(key);
        if (line != null) {
            line.setValue(value);
            return;
        }
        line = new Line(key, value);
        lines.add(line);
        propertyLines.put(key, line);
    }

    public void putLine(String line) {
        Line line1 = new Line(line);
        lines.add(line1);
        if (line1.getKey() != null) {
            propertyLines.put(line1.getKey(), line1);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Line line : lines) {
            stringBuilder.append(line.toString());
            stringBuilder.append("\r\n");
        }
        return stringBuilder.toString();
    }

    private static class Line extends Pair<String, String> {
        public Line(String key, String value) {
            super(key, value);
        }

        public Line(String line) {
            int index = line.indexOf("=");
            if (line.startsWith("#") || index < 0) {
                setValue(line);
                return;
            }
            setKey(line.substring(0, index));
            if (line.length() - 1 > index) {
                setValue(line.substring(index + 1));
            } else {
                setValue("");
            }
        }

        @Override
        public String toString() {
            if (getKey() == null) {
                return getValue();
            } else {
                return getKey() + "=" + getValue();
            }
        }
    }
}
