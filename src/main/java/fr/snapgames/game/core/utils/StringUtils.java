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
     * @param attributes the {@link Map} of value to be displayed.
     * @param start      the character to start the string with.
     * @param end        the character to end the string with.
     * @param delimiter  the character to seperate each entry.
     * @return a concatenated {@link String} based on the {@link Map} {@link java.util.Map.Entry}.
     */
    public static String prepareStatsString(Map<String, Object> attributes, String start, String end, String delimiter) {
        return start + attributes.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(entry -> {
            String value = "";
            switch (entry.getValue().getClass().getSimpleName()) {
                case "Double", "double", "Float", "float" -> {
                    value = String.format("%04.2f", entry.getValue());
                }
                case "Integer", "int" -> {
                    value = String.format("%5d", entry.getValue());
                }
                default -> {
                    value = entry.getValue().toString();
                }
            }
            return entry.getKey().substring(((String) entry.getKey().toString()).indexOf('_') + 1)
                    + ":"
                    + value;
        }).collect(Collectors.joining(delimiter)) + end;
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

    /**
     * Convert a long duration value to a formatted String value "D hh:mm:ss.SSS".
     *
     * @param duration in ms
     * @return formatted String "%d d - %02d:%02d:%02d.%03d"
     */
    public static String formatDuration(long duration) {
        int ms, s, m, h, d;
        double dec;
        double time = duration * 1.0;

        time = (time / 1000.0);
        dec = time % 1;
        time = time - dec;
        ms = (int) (dec * 1000.0);

        time = (time / 60.0);
        dec = time % 1;
        time = time - dec;
        s = (int) (dec * 60.0);

        time = (time / 60.0);
        dec = time % 1;
        time = time - dec;
        m = (int) (dec * 60.0);

        time = (time / 24.0);
        dec = time % 1;
        time = time - dec;
        h = (int) (dec * 24.0);

        d = (int) time;

        return (String.format("%d d - %02d:%02d:%02d.%03d", d, h, m, s, ms));
    }
}
