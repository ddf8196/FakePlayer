package com.ddf.fakeplayer.json.skin;

public class PieceTintColor {
    private String[] Colors;
    private String PieceType;

    public PieceTintColor() {}

    public PieceTintColor(String[] colors, String pieceType) {
        Colors = colors;
        PieceType = pieceType;
    }

    public String[] getColors() {
        return Colors;
    }

    public void setColors(String[] colors) {
        Colors = colors;
    }

    public String getPieceType() {
        return PieceType;
    }

    public void setPieceType(String pieceType) {
        PieceType = pieceType;
    }
}
