package com.nikit.bobin.wordstranslate.translating.models;

import com.nikit.bobin.wordstranslate.core.Ensure;

public class Direction {
    private Language from;
    private Language to;
    private String toString;

    public Direction(Language from, Language to) {
        Ensure.notNull(from, "from");
        Ensure.notNull(to, "to");

        this.from = from;
        this.to = to;
        toString = String.format("%s-%s", from.getKey(), to.getKey());
    }

    public static Direction parse(String direction) {
        Ensure.notNullOrEmpty(direction, "direction");

        String[] segments = direction.split("-");
        if (segments.length != 2)
            throw new IllegalArgumentException(
                    String.format("Target direction: %s have not correct format", direction));
        return new Direction(new Language(segments[0]), new Language(segments[1]));
    }

    @Override
    public String toString() {
        return toString;
    }

    public Language getFrom() {
        return from;
    }

    public Language getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Direction direction = (Direction) o;
        if (!from.equals(direction.from)) return false;
        if (!to.equals(direction.to)) return false;
        return toString.equals(direction.toString);
    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        result = 31 * result + toString.hashCode();
        return result;
    }
}
