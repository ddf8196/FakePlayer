package com.ddf.fakeplayer.util.command;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import java.lang.reflect.Field;
import java.util.Map;

public class CommandNodeUtil {
    public static <S> void removeChild(CommandNode<S> commandNode, final CommandNode<S> node) {
        removeChild(commandNode, node.getName());
    }

    @SuppressWarnings("unchecked")
    public static <S> void removeChild(CommandNode<S> commandNode, final String name) {
        try {
            Field childrenField = CommandNode.class.getDeclaredField("children");
            childrenField.setAccessible(true);
            Map<String, CommandNode<S>> children = (Map<String, CommandNode<S>>) childrenField.get(commandNode);

            Field literalsField = CommandNode.class.getDeclaredField("literals");
            literalsField.setAccessible(true);
            Map<String, LiteralCommandNode<S>> literals = (Map<String, LiteralCommandNode<S>>) literalsField.get(commandNode);

            Field argumentsField = CommandNode.class.getDeclaredField("arguments");
            argumentsField.setAccessible(true);
            Map<String, ArgumentCommandNode<S, ?>> arguments = (Map<String, ArgumentCommandNode<S, ?>>) argumentsField.get(commandNode);

            children.remove(name);
            literals.remove(name);
            arguments.remove(name);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
