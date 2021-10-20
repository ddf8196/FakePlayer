package com.ddf.fakeplayer.js.functions;

import com.ddf.fakeplayer.actor.player.FakePlayer;
import com.ddf.fakeplayer.js.classes.BaseScriptableObject;
import com.ddf.fakeplayer.js.classes.item.JsItemType;
import com.ddf.fakeplayer.js.util.JSUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import org.mozilla.javascript.*;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;

import java.util.ArrayList;
import java.util.List;

public class RegisterChatCommand extends BaseFunction {
    public static final int boolArg = 0;
    public static final int intArg = 1;
    public static final int floatArg = 2;
    public static final int longArg = 3;
    public static final int doubleArg = 4;
    public static final int stringArg = 5;

    private final FakePlayer player;

    public RegisterChatCommand(Scriptable scope, FakePlayer player) {
        super(scope);
        this.player = player;
    }

    @Override
    public String getFunctionName() {
        return "registerChatCommand";
    }

    @Override
    public void install(Scriptable scope) {
        try {
            ScriptableObject.defineClass(scope, ChatCommandContext.class);
        } catch (Throwable ignored) {}
        ScriptableObject.putProperty(scope, "registerChatCommand", this);
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        switch (args.length) {
            case 2: {
                String name = JSUtil.tryCastArgument(args[0], String.class);
                Function function = JSUtil.tryCastArgument(args[1], Function.class);
                player.registerChatCommand(name , context -> {
                    ChatCommandContext chatCommandContext = JSUtil.newObject(ChatCommandContext.class, scope);
                    chatCommandContext.setContext(context);
                    function.call(cx, scope, thisObj, new Object[] {chatCommandContext});
                    return Command.SINGLE_SUCCESS;
                });
                break;
            }
            case 4: {
                String name = JSUtil.tryCastArgument(args[0], String.class);
                List<String> argumentNames = JSUtil.tryCastArrayArgument(args[1], String.class);
                List<ArgumentType<?>> argumentTypes = toArgumentTypeList(JSUtil.tryCastArrayArgument(args[2], Number.class));
                Function function = JSUtil.tryCastArgument(args[3], Function.class);
                player.registerChatCommand(name, argumentNames, argumentTypes, context -> {
                    ChatCommandContext chatCommandContext = JSUtil.newObject(ChatCommandContext.class, scope);
                    chatCommandContext.setContext(context);
                    function.call(cx, scope, thisObj, new Object[] {chatCommandContext});
                    return Command.SINGLE_SUCCESS;
                });
                break;
            }
            default:
                throw new IllegalArgumentException();
        }
        return Undefined.instance;
    }

    private List<ArgumentType<?>> toArgumentTypeList(List<Number> list) {
        ArrayList<ArgumentType<?>> arrayList = new ArrayList<>();
        list.forEach(number -> {
            int integer = number.intValue();
            switch (integer) {
                case boolArg:
                    arrayList.add(BoolArgumentType.bool());
                    break;
                case intArg:
                    arrayList.add(IntegerArgumentType.integer());
                    break;
                case floatArg:
                    arrayList.add(FloatArgumentType.floatArg());
                    break;
                case longArg:
                    arrayList.add(LongArgumentType.longArg());
                    break;
                case doubleArg:
                    arrayList.add(DoubleArgumentType.doubleArg());
                    break;
                case stringArg:
                    arrayList.add(StringArgumentType.string());
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        });
        return arrayList;
    }

    public static class ChatCommandContext extends BaseScriptableObject {
        private CommandContext<?> context;

        public ChatCommandContext() {}

        public ChatCommandContext(Scriptable scope) {
            this(scope, getPrototype(JsItemType.class, scope));
        }

        public ChatCommandContext(Scriptable scope, Scriptable prototype) {
            super(scope, prototype);
        }

        public static void finishInit(Scriptable scope, FunctionObject constructor, Scriptable prototype) {
            putPrototype(JsItemType.class, scope, prototype);
        }

        @Override
        public String getClassName() {
            return "ChatCommandContext";
        }

        public CommandContext<?> getContext() {
            return context;
        }

        public void setContext(CommandContext<?> context) {
            this.context = context;
        }

        //----------------------------------------------------------------------------------------------------------------------

        @JSConstructor
        public void constructor() {
            throw new UnsupportedOperationException();
        }

        @JSFunction
        public Object getArgument(String name, int argType) {
            switch (argType) {
                case boolArg:
                    return context.getArgument(name, boolean.class);
                case intArg:
                    return context.getArgument(name, int.class);
                case floatArg:
                    return context.getArgument(name, float.class);
                case longArg:
                    return context.getArgument(name, long.class);
                case doubleArg:
                    return context.getArgument(name, double.class);
                case stringArg:
                    return context.getArgument(name, String.class);
                default:
                    throw new IllegalArgumentException();
            }
        }
    }
}