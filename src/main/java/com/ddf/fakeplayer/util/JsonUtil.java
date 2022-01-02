package com.ddf.fakeplayer.util;

public class JsonUtil {
    public static ItemParseResult parseItem(int inoutItemAux, String inString) {
        String outItemName = "";
        String outItemNamespace = "";
        if (!inString.isEmpty()) {
            outItemNamespace = "minecraft";
            String[] terms = inString.split(":");
            if (terms.length == 0) {
                return new ItemParseResult(false, outItemName, outItemNamespace, inoutItemAux);
            } else if (terms.length == 1) {
                outItemName = terms[0];
            } else if (!isNumber(terms[1])) {
                String nameSpace = terms[0];
                outItemNamespace = nameSpace.toLowerCase();
                outItemName = terms[1];
                if (terms.length > 2) {
                    inoutItemAux = toInt(terms[2]);
                }
            }
            outItemName = outItemName.toLowerCase();
            if (outItemName.startsWith("tile.")) {
                outItemName = outItemName.substring(5);
            }
            return new ItemParseResult(true, outItemName, outItemNamespace, inoutItemAux);
        }
        return new ItemParseResult(false, outItemName, outItemNamespace, inoutItemAux);
    }

    public static int toInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return Integer.MIN_VALUE;
        }
    }

    public static boolean isNumber(String string) {
        return string.matches("^-?\\d+$");
    }

    public static class ItemParseResult {
        public final boolean success;
        public final String outItemName;
        public final String outItemNamespace;
        public final int inoutItemAux;

        public ItemParseResult(boolean success, String outItemName, String outItemNamespace, int inoutItemAux) {
            this.success = success;
            this.outItemName = outItemName;
            this.outItemNamespace = outItemNamespace;
            this.inoutItemAux = inoutItemAux;
        }
    }
}
