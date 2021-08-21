package com.ddf.fakeplayer.actor;

import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.nbt.CompoundTag;
import com.ddf.fakeplayer.util.Vec3;

import java.util.ArrayList;

public class SynchedActorData {
    private final ArrayList<DataItem<?>> mItemsArray;
    private /*unsigned short*/int minIdxDirty;
    private /*unsigned short*/int maxIdxDirty;

    public SynchedActorData() {
        this.mItemsArray = new ArrayList<>();
        this.minIdxDirty = 0xFFFF;
        this.maxIdxDirty = 0;
    }

    public SynchedActorData(SynchedActorData rhs) {
        this.minIdxDirty = rhs.minIdxDirty;
        this.maxIdxDirty = rhs.maxIdxDirty;
        this.mItemsArray = new ArrayList<>(rhs.mItemsArray);
    }

    public final void _setDirty(/*unsigned short*/int id) {
        this.minIdxDirty = Math.min(id, this.minIdxDirty);
        this.maxIdxDirty = Math.max(id, this.maxIdxDirty);
    }

    public final DataItem<?> _find(/*unsigned short*/int id) {
        if (id >= this.mItemsArray.size())
            return null;
        return this.mItemsArray.get(id);
    }

    public final void _resizeToContain(/*unsigned short*/int id) {
        while (this.mItemsArray.size() <= id) {
            this.mItemsArray.add(null);
        }
    }

    public final void markDirty(DataItem<?> item) {
        item.setDirty(true);
        this._setDirty(item.getId());
    }

    public final void define(/*unsigned short*/int id, final byte value) {
        if (id <= 0x1FFF && this._find(id) == null){
            this._resizeToContain(id);
            DataItem<Byte> dataItem = new DataItem<>(DataItemType.Byte, id, value);
            this.mItemsArray.set(id, dataItem);
            this._setDirty(id);
        }
    }

    public final void define(/*unsigned short*/int id, final short value) {
        if (id <= 0x1FFF && this._find(id) == null){
            this._resizeToContain(id);
            DataItem<Short> dataItem = new DataItem<>(DataItemType.Short, id, value);
            this.mItemsArray.set(id, dataItem);
            this._setDirty(id);
        }
    }

    public final void define(/*unsigned short*/int id, final int value) {
        if (id <= 0x1FFF && this._find(id) == null){
            this._resizeToContain(id);
            DataItem<Integer> dataItem = new DataItem<>(DataItemType.Int_1, id, value);
            this.mItemsArray.set(id, dataItem);
            this._setDirty(id);
        }
    }
    public final void define(/*unsigned short*/int id, final float value) {
        if (id <= 0x1FFF && this._find(id) == null){
            this._resizeToContain(id);
            DataItem<Float> dataItem = new DataItem<>(DataItemType.Float_1, id, value);
            this.mItemsArray.set(id, dataItem);
            this._setDirty(id);
        }
    }
    public final void define(/*unsigned short*/int id, final String value) {
        if (id <= 0x1FFF && this._find(id) == null){
            this._resizeToContain(id);
            DataItem<String> dataItem = new DataItem<>(DataItemType.String_0, id, value);
            this.mItemsArray.set(id, dataItem);
            this._setDirty(id);
        }
    }

    public final void define(/*unsigned short*/int id, final CompoundTag value) {
        if (id <= 0x1FFF && this._find(id) == null){
            this._resizeToContain(id);
            DataItem<CompoundTag> dataItem = new DataItem<>(DataItemType.CompoundTag, id, value);
            this.mItemsArray.set(id, dataItem);
            this._setDirty(id);
        }
    }
    public final void define(/*unsigned short*/int id, final BlockPos value) {
        if (id <= 0x1FFF && this._find(id) == null){
            this._resizeToContain(id);
            DataItem<BlockPos> dataItem = new DataItem<>(DataItemType.Pos, id, value);
            this.mItemsArray.set(id, dataItem);
            this._setDirty(id);
        }
    }

    public final void define(/*unsigned short*/int id, final long value) {
        if (id <= 0x1FFF && this._find(id) == null){
            this._resizeToContain(id);
            DataItem<Long> dataItem = new DataItem<>(DataItemType.Int64, id, value);
            this.mItemsArray.set(id, dataItem);
            this._setDirty(id);
        }
    }

    public final void define(/*unsigned short*/int id, final Vec3 value) {
        if (id <= 0x1FFF && this._find(id) == null){
            this._resizeToContain(id);
            DataItem<Vec3> dataItem = new DataItem<>(DataItemType.Vec3, id, value);
            this.mItemsArray.set(id, dataItem);
            this._setDirty(id);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> DataItem<T> _get(/*unsigned short*/int id) {
        return (DataItem<T>) this.mItemsArray.get(id);
    }

    public final int getInt(/*unsigned short*/int id) {
        DataItem<?> dataItem = this._find(id);
        if (dataItem != null && dataItem.getType() == DataItemType.Int_1)
            return (Integer) dataItem.getData();
        else
            return 0;
    }

    public final float getFloat(/*unsigned short*/int id) {
        DataItem<?> dataItem = this._find(id);
        if (dataItem != null && dataItem.getType() == DataItemType.Float_1)
            return (Float) dataItem.getData();
        else
            return 0.0f;
    }

    public boolean getFlag(/*unsigned short*/int id, int flag) {
        DataItem<?> item = this._find(id);
        return item != null && item.getFlag(flag);
    }

    public final void set(DataItem<Byte> dataItem, final byte value) {
        if (dataItem != null && dataItem.getType() == DataItemType.Byte && dataItem.getData() != value) {
            dataItem.setData(value);
            this.markDirty(dataItem);
        }
    }

    public final void set(DataItem<Short> dataItem, final short value) {
        if (dataItem != null && dataItem.getType() == DataItemType.Byte && dataItem.getData() != value) {
            dataItem.setData(value);
            this.markDirty(dataItem);
        }
    }

    public final void set(DataItem<Integer> dataItem, final int value) {
        if (dataItem != null && dataItem.getType() == DataItemType.Byte && dataItem.getData() != value) {
            dataItem.setData(value);
            this.markDirty(dataItem);
        }
    }

    public final void set(DataItem<Float> dataItem, final float value) {
        if (dataItem != null && dataItem.getType() == DataItemType.Byte && dataItem.getData() != value) {
            dataItem.setData(value);
            this.markDirty(dataItem);
        }
    }

    public final void set(DataItem<String> dataItem, final String value) {
        if (dataItem != null && dataItem.getType() == DataItemType.Byte && dataItem.getData() != value) {
            dataItem.setData(value);
            this.markDirty(dataItem);
        }
    }

    public final void set(DataItem<CompoundTag> dataItem, final CompoundTag value) {
        if (dataItem != null && dataItem.getType() == DataItemType.Byte && dataItem.getData() != value) {
            dataItem.setData(value);
            this.markDirty(dataItem);
        }
    }

    public final void set(DataItem<BlockPos> dataItem, final BlockPos value) {
        if (dataItem != null && dataItem.getType() == DataItemType.Byte && dataItem.getData() != value) {
            dataItem.setData(value);
            this.markDirty(dataItem);
        }
    }

    public final void set(DataItem<Long> dataItem, final long value) {
        if (dataItem != null && dataItem.getType() == DataItemType.Byte && dataItem.getData() != value) {
            dataItem.setData(value);
            this.markDirty(dataItem);
        }
    }

    public final void set(DataItem<Vec3> dataItem, final Vec3 value) {
        if (dataItem != null && dataItem.getType() == DataItemType.Byte && dataItem.getData() != value) {
            dataItem.setData(value);
            this.markDirty(dataItem);
        }
    }

    public void set(/*unsigned short*/int id, byte value) {
        this.set(this.<Byte>_get(id), value);
    }

    public void set(/*unsigned short*/int id, short value) {
        this.set(this.<Short>_get(id), value);
    }

    public void set(/*unsigned short*/int id, int value) {
        this.set(this.<Integer>_get(id), value);
    }

    public void set(/*unsigned short*/int id, float value) {
        this.set(this.<Float>_get(id), value);
    }

    public void set(/*unsigned short*/int id, String value) {
        this.set(this.<String>_get(id), value);
    }

    public void set(/*unsigned short*/int id, CompoundTag value) {
        this.set(this.<CompoundTag>_get(id), value);
    }

    public void set(/*unsigned short*/int id, BlockPos value) {
        this.set(this.<BlockPos>_get(id), value);
    }

    public void set(/*unsigned short*/int id, long value) {
        this.set(this.<Long>_get(id), value);
    }

    public void set(/*unsigned short*/int id, Vec3 value) {
        this.set(this.<Vec3>_get(id), value);
    }
}
