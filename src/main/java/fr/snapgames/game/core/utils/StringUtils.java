package fr.snapgames.game.core.utils;

import java.awt.*;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * String utilities to extract or to compose String from or to other objects.
 *
 * @author Frédéric Delorme
 * @since 0.0.6
 */
public class StringUtils {

    /**
     * Create a String from all the {@link java.util.Map.Entry} of a {@link Map}.
     * <p>
     * the String is composed on the format "[ entry1:value1 | entry2:value2 ]" where, in e the map :
     * <pre>
     * Maps.of("1_entry1","value1","2_entry2","value2",...);
     * </pre>
     * <p>
     * this will sort the Entry on the `[9]` from the `[9]_[keyname]` key name.
     *
     * @param stats the {@link Map} of value to be displayed
     * @return a concatenated {@link String} based on the {@link Map} {@link java.util.Map.Entry}.
     */
    public static String prepareStatsString(Map<String, Object> stats) {
        return "[" + stats.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(entry ->
                        entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining("|")) + "]";
    }

    /**
     * Convert the String expression [width]x[height] ti a Dimension(width,height) instance
     *
     * @param value the String value to be parsed.
     * @return a new Dimension of object of (width,height)
     */
    public static Dimension toDimension(String value) {
        String[] interpretedValue = value
                .split("x");
        return new Dimension(
                Integer.parseInt(interpretedValue[0]),
                Integer.parseInt(interpretedValue[1]));
    }
}
