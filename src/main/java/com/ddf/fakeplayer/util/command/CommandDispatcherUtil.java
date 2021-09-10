package com.ddf.fakeplayer.util.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class CommandDispatcherUtil {
    public static <S> void unregister(CommandDispatcher<S> commandDispatcher, LiteralCommandNode<S> node) {
        CommandNodeUtil.removeChild(commandDispatcher.getRoot(), node);
    }

    public static <S> void unregister(CommandDispatcher<S> commandDispatcher, String name) {
        CommandNodeUtil.removeChild(commandDispatcher.getRoot(), name);
    }
}
