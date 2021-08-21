package com.ddf.fakeplayer.item.items;

import com.ddf.fakeplayer.item.Item;
import com.ddf.fakeplayer.item.ItemRegistry;
import com.ddf.fakeplayer.util.NotImplemented;

@NotImplemented
public class AirBlockItem extends Item {
    public AirBlockItem(String nameId) {
        super(nameId);
    }

    @Override
    public short getId(ItemRegistry registry) {
        return 0;
    }
}
