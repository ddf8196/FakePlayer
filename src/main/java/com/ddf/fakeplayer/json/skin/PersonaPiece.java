package com.ddf.fakeplayer.json.skin;

public class PersonaPiece {
    public boolean IsDefault;
    public String PackId;
    public String PieceId;
    public String PieceType;
    public String ProductId;

    public PersonaPiece() {
    }

    public PersonaPiece(boolean isDefault, String packId, String pieceId, String pieceType, String productId) {
        IsDefault = isDefault;
        PackId = packId;
        PieceId = pieceId;
        PieceType = pieceType;
        ProductId = productId;
    }
}
