package com.nikit.bobin.wordstranslate.translating.models;

import com.nikit.bobin.wordstranslate.core.Ensure;

//todo: to struct
public class Direction {
    private Language from;
    private Language to;
    private String stringified;

    public Direction(Language from, Language to) {
        Ensure.notNull(from, "from");
        Ensure.notNull(to, "to");

        this.from = from;
        this.to = to;
        stringified = String.format("%s-%s", from.getKey(), to.getKey());
    }

    public static Direction parse(String direction) {
        String[] segments = direction.split("\\-");
        return new Direction(new Language(segments[0]), new Language(segments[1]));
    }

    @Override
    public String toString() {
        return stringified;
    }

    public Language getFrom() {
        return from;
    }

    public Language getTo() {
        return to;
    }
}
