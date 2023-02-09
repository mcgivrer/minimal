package fr.snapgames.game.core.math;

import java.awt.*;

/**
 * Provide some randomness to java components
 *
 * @author Frédéric Delorme
 * @since 0.0.3
 **/
public class RandomUtils {

    /**
     * Generate a total random color.
     *
     * @return a randomly generated color.
     */
    public static Color randomColor() {
        return new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
    }

    public static Color randomRangedColor(float red, float green, float blue, float alpha, float rangeRandom) {
        return new Color((float) (red * Math.random() * rangeRandom),
                (float) (green * Math.random() * rangeRandom),
                (float) (blue * Math.random() * rangeRandom),
                (float) (alpha * Math.random() * rangeRandom)
        );
    }

    /**
     * Generate a new color with red, green and blue color component range and an alpha channel range.
     *
     * @param minRed
     * @param maxRed
     * @param minGreen
     * @param maxGreen
     * @param minBlue
     * @param maxBlue
     * @param minAlpha
     * @param maxAlpha
     * @return
     */
    public static Color randomColorMinMax(
            float minRed,
            float maxRed,
            float minGreen,
            float maxGreen,
            float minBlue,
            float maxBlue,
            float minAlpha,
            float maxAlpha) {
        return new Color(
                (float) (minRed + (Math.random() * (maxRed - minRed))),
                (float) (minGreen + (Math.random() * (maxGreen - minGreen))),
                (float) (minBlue + (Math.random() * (maxBlue - minBlue))),
                (float) (minAlpha + (Math.random() * (maxAlpha - minAlpha)))
        );
    }

    /**
     * random generate {@link Vector2D} in a {@link Dimension} playArea.
     *
     * @param playArea the {@link Dimension} instance where so spread the rando {@link Vector2D}.
     * @return
     */
    public static Vector2D ramdomVector(Dimension playArea) {
        return new Vector2D(Math.random() * playArea.getWidth(), Math.random() * playArea.getHeight());
    }
}
