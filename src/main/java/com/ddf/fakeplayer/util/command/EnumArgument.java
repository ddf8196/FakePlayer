package com.ddf.fakeplayer.util.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.*;
import java.util.stream.Collectors;

public class EnumArgument implements ArgumentType<String> {
    private List<String> list;


    public static EnumArgument enumArg(String... members) {
        EnumArgument arg = new EnumArgument();
        arg.list = Arrays.stream(members).collect(Collectors.toList());
        return arg;
    }

    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        String str = reader.readUnquotedString();
        if (list.contains(str)) {
            return str;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("enum(");
        list.forEach(name -> builder.append(name).append('|'));
        builder.deleteCharAt(builder.length() - 1);
        builder.append(")");
        return builder.toString();
    }

    @Override
    public Collection<String> getExamples() {
        return list;
    }

}
