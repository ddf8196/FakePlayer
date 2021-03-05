package com.ddf.fakeplayer.json.skin;

public class PersonaPiece {
    private boolean IsDefault;
    private String PackId;
    private String PieceId;
    private String PieceType;
    private String ProductId;

    public PersonaPiece() {}

    public PersonaPiece(boolean isDefault, String packId, String pieceId, String pieceType, String productId) {
        IsDefault = isDefault;
        PackId = packId;
        PieceId = pieceId;
        PieceType = pieceType;
        ProductId = productId;
    }

    public boolean isDefault() {
        return IsDefault;
    }

    public void setDefault(boolean aDefault) {
        IsDefault = aDefault;
    }

    public String getPackId() {
        return PackId;
    }

    public void setPackId(String packId) {
        PackId = packId;
    }

    public String getPieceId() {
        return PieceId;
    }

    public void setPieceId(String pieceId) {
        PieceId = pieceId;
    }

    public String getPieceType() {
        return PieceType;
    }

    public void setPieceType(String pieceType) {
        PieceType = pieceType;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }
}
