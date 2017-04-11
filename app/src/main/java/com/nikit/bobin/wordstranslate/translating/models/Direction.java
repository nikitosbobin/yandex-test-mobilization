package com.nikit.bobin.wordstranslate.translating.models;

import com.nikit.bobin.wordstranslate.helpers.Ensure;
import com.nikit.bobin.wordstranslate.helpers.Strings;

//Model describes direction of future translation
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

    public static Direction parseKeySerialized(String keysSerializedDirection) {
        Ensure.notNullOrEmpty(keysSerializedDirection, "keysSerializedDirection");

        String[] segments = keysSerializedDirection.split("-");
        if (segments.length != 2)
            throw new IllegalArgumentException(
                    String.format("Target keysSerializedDirection: %s " +
                            "have incorrect format: <fromKey>-<toKey>",
                            keysSerializedDirection));
        return new Direction(new Language(segments[0]), new Language(segments[1]));
    }

    public static Direction parseFullSerialized(String fullSerializedDirection) {
        String[] segments = fullSerializedDirection.split("\\|");
        if (segments.length < 2)
            throw new IllegalArgumentException(
                    String.format("Target fullSerializedDirection: %s " +
                            "have incorrect format: <fromKey>|<toKey>|<fromTitle>|<toTitle>",
                            fullSerializedDirection));

        String fromTitle = null;
        String toTitle = null;
        if (segments.length > 2) {
            fromTitle = segments[2].length() == 0 ? null : segments[2];
            if (segments.length == 4) {
                toTitle = segments[3].length() == 0 ? null : segments[3];
            }
        }

        return new Direction(
                new Language(segments[0], fromTitle),
                new Language(segments[1], toTitle));
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

    public String fullSerialize() {
        String fromKey = from.getKey();
        String toKey = to.getKey();
        String fromTitle = from.getTitle();
        String toTitle = to.getTitle();

        if (fromTitle == null)
            fromTitle = Strings.empty;
        if (toTitle == null)
            toTitle = Strings.empty;

        return String.format("%s|%s|%s|%s", fromKey, toKey, fromTitle, toTitle);
    }
}
