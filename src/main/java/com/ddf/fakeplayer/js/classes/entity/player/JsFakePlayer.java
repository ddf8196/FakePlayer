package com.ddf.fakeplayer.js.classes.entity.player;

import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.actor.player.FakePlayer;
import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.container.ContainerID;
import com.ddf.fakeplayer.container.inventory.PlayerInventoryProxy;
import com.ddf.fakeplayer.container.inventory.transaction.ItemUseInventoryTransaction;
import com.ddf.fakeplayer.js.classes.entity.JsEntity;
import com.ddf.fakeplayer.js.classes.item.JsItemStack;
import com.ddf.fakeplayer.js.classes.location.JsBlockLocation;
import com.ddf.fakeplayer.js.classes.location.JsLocation;
import com.ddf.fakeplayer.js.util.JSUtil;
import com.ddf.fakeplayer.util.Vec2;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;
import org.mozilla.javascript.annotations.JSSetter;

public class JsFakePlayer extends JsPlayer {
    private FakePlayer fakePlayer;
    private BlockPos destroyingBlockPos;

    public JsFakePlayer() {}

    public JsFakePlayer(Scriptable scope) {
        super(scope, getPrototype(JsFakePlayer.class, scope));
    }

    public JsFakePlayer(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    public static void finishInit(Scriptable scope, FunctionObject constructor, Scriptable prototype) {
        putPrototype(JsFakePlayer.class, scope, prototype);
    }

    @Override
    public FakePlayer getActor() {
        return fakePlayer;
    }

    @Override
    public void setActor(Actor actor) {
        if (!(actor instanceof FakePlayer))
            return;
        fakePlayer = (FakePlayer) actor;
    }

    @Override
    public Player getPlayer() {
        return fakePlayer;
    }

    @Override
    public void setPlayer(Player player) {
        if (!(player instanceof FakePlayer))
            return;
        fakePlayer = (FakePlayer) player;
    }

    public FakePlayer getFakePlayer() {
        return fakePlayer;
    }

    public void setFakePlayer(FakePlayer fakePlayer) {
        this.fakePlayer = fakePlayer;
    }

//----------------------------------------------------------------------------------------------------------------------

    @JSConstructor
    public void constructor() {
        throw new UnsupportedOperationException();
    }

    @JSGetter
    @Override
    public String id() {
        return "minecraft:player";
    }

    @JSGetter
    public double bodyRotation() {
        return fakePlayer.getRotation().y;
    }

    @JSGetter
    public JsScriptPlayerHeadRotation headRotation() {
        JsScriptPlayerHeadRotation rotation = JSUtil.newObject(JsScriptPlayerHeadRotation.class, getParentScope());
        rotation.setFakePlayer(fakePlayer);
        return rotation;
    }

    @JSGetter
    public double pitch() {
        return fakePlayer.getRotation().x;
    }

    @JSSetter
    public void pitch(double pitch) {
        fakePlayer.setRot(new Vec2((float) pitch, fakePlayer.getRotation().y));
    }

    @JSGetter
    public double yaw() {
        return fakePlayer.getYHeadRot();
    }

    @JSSetter
    public void yaw(double yaw) {
        fakePlayer.setYHeadRot((float) yaw);
    }

    @JSFunction
    public boolean attack() {
        return false;
    }

    @JSFunction
    public boolean attackEntity(JsEntity entity) {
        return false;
    }

    @JSFunction
    public boolean startDestroyingBlock(JsBlockLocation blockLocation, int direction) {
        if (destroyingBlockPos != null) {
            stopDestroyingBlock();
        }
        destroyingBlockPos = blockLocation.toBlockPos();
        fakePlayer.sendStartBreak(destroyingBlockPos, direction);
        fakePlayer.sendSwingArm();
        return false;
    }

    @JSFunction
    public boolean destroyBlock(JsBlockLocation blockLocation, int direction) {
        stopDestroyingBlock();
        return false;
    }

    @JSFunction
    public boolean interact() {
        return false;
    }

    @JSFunction
    public boolean interactWithBlock(JsBlockLocation blockLocation, int direction) {
        return false;
    }

    @JSFunction
    public boolean interactWithEntity(JsEntity entity) {
        return fakePlayer.getGameMode().interact(entity.getActor(), entity.getActor().getPos());
    }

    @JSFunction
    public boolean jump() {
        return false;
    }

    @JSFunction
    public void lookAtBlock(JsBlockLocation blockLocation) {

    }

    @JSFunction
    public void lookAtEntity(JsEntity entity) {

    }

    @JSFunction
    public void lookAtLocation(JsLocation location) {

    }

    @JSFunction
    public void move(double westEast, double northSouth, double speed) {

    }

    @JSFunction
    public void moveRelative(double leftRight, double backwardForward, double speed) {

    }

    @JSFunction
    public void moveToBlock(JsBlockLocation blockLocation, double speed) {

    }

    @JSFunction
    public void moveToLocation(JsLocation location, double speed) {

    }

    @JSFunction
    public JsScriptNavigationResult navigateToBlock(JsBlockLocation blockLocation, double speed) {
        return null;
    }

    @JSFunction
    public JsScriptNavigationResult navigateToEntity(JsEntity entity, double speed) {
        return null;
    }

    @JSFunction
    public JsScriptNavigationResult navigateToLocation(JsLocation location, double speed) {
        return null;
    }

    @JSFunction
    public void navigateToLocations(/*Location[]*/Object locations, double speed) {

    }

    @JSFunction
    public void rotateBody(double angleInDegrees) {

    }

    @JSFunction
    public void selectSlot(int slot) {
        fakePlayer.getSupplies().selectSlot(slot, ContainerID.CONTAINER_ID_INVENTORY);
    }

    @JSFunction
    public void setBodyRotation(double angleInDegrees) {
        fakePlayer.setRot(new Vec2(fakePlayer.getRotation().x, (float) angleInDegrees));
    }

    @JSFunction
    public void stopDestroyingBlock() {
        if (destroyingBlockPos != null) {
            fakePlayer.sendStopBreak();
            fakePlayer.sendAbortBreak(destroyingBlockPos);
            destroyingBlockPos = null;
        }
    }

    @JSFunction
    public void stopInteracting() {

    }

    @JSFunction
    public void stopMoving() {

    }

    @JSFunction
    public void stopUsingItem() {
        fakePlayer.stopUsingItem();
    }

    @JSFunction
    public boolean useItem(JsItemStack itemStack) {
        return false;
    }

    @JSFunction
    public boolean useItemInSlot(int slot) {
        return false;
    }

    @JSFunction
    public boolean useItemInSlotOnBlock(int slot, JsBlockLocation blockLocation, int direction, int faceLocationX, int faceLocationY) {
        PlayerInventoryProxy inventory = fakePlayer.getSupplies();
        ItemUseInventoryTransaction transaction = new ItemUseInventoryTransaction(fakePlayer.getLevel().getItemRegistry());
        inventory.createTransactionContext((container, slot1, oldItem, newItem) -> {

        }, () -> {

        });
        fakePlayer.sendComplexInventoryTransaction(transaction);
        return false;
    }

    @JSFunction
    public boolean useItemOnBlock(JsItemStack itemStack, JsBlockLocation blockLocation, int direction, int faceLocationX, int faceLocationY) {
        return false;
    }

}
