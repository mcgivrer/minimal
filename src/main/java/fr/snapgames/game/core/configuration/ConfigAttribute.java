package fr.snapgames.game.core.configuration;

import fr.snapgames.game.core.math.Material;
import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.utils.StringUtils;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Function;

/**
 * List of Configuration attributes than can be set to specific value ot to a default value.
 * <p>
 * Documentation is auto-defined through the Enum values declaration. Don't hesitate to be precise on definition.
 * <p>
 * This would help not only users but also developers to maintain the list of attributes.
 *
 * @author Frédéric Delorme
 * @since 0.0.2
 **/
public enum ConfigAttribute implements IConfigAttribute {
    /**
     * The required name for this application to be displayed on the window.
     */
    APP_TITLE(
            "appTitle",
            "game.main.title",
            "Set the name of the application to be displayed in log and UI",
            "Game",
            v -> v),
    /*
     * debug mode argument.
     */
    DEBUG_LEVEL(
            "debugMode",
            "game.debug.level",
            "Setting the debug mode (0 to 5)",
            0,
            Integer::valueOf),
    /*
     * number of loop cycle before exit in test mode.
     */
    EXIT_TEST_COUNT_FRAME(
            "testCounter",
            "game.test.loop.counter",
            "if debug>0, set a number of frame to execute before exit (test mode)",
            -1,
            Integer::valueOf),
    /**
     * the FPS required for looping and rendering process.
     */
    RENDER_FPS(
            "fps",
            "game.render.fps",
            "set the frame per second for the render engine",
            60,
            Integer::valueOf),
    /**
     * scale ratio between Window and rendering buffer.
     */
    WINDOW_SCALE(
            "scale",
            "game.window.scale",
            "Set the window scale ration based on game.viewport.size",
            1.0,
            Double::valueOf),
    /**
     * flag indicates if game's window is on full screen.
     */
    WINDOW_FULL_SCREEN(
            "windowFullscreen",
            "game.window.fullscreen",
            "Switch the Window to full screen (not implemented yet)",
            false,
            Boolean::valueOf),
    /**
     * Width for the Screen rendering size
     */
    SCREEN_WIDTH(
            "screenWidth",
            "game.screen.width",
            "set the screen width",
            320,
            Integer::valueOf),
    /**
     * Height for the Screen rendering size
     */
    SCREEN_HEIGHT(
            "screenHeight",
            "game.screen.height",
            "set the screen height",
            200,
            Integer::valueOf),
    PLAY_AREA_SIZE(
            "playAreaSize",
            "game.physic.world.playarea.size",
            "set the size of the play area: [width]x[height]",
            new Dimension(320, 200),
            StringUtils::toDimension),
    PHYSIC_UPS(
            "physicUps",
            "game.physic.ups",
            "set the Update Per Second (UPS) rate",
            60,
            Integer::valueOf
    ),
    PHYSIC_GRAVITY(
            "physicGravity",
            "game.physic.world.gravity",
            "set the 2D vector for gravity applied by physic engine",
            new Vector2D(0.0, 0.0),
            v -> IConfigAttribute.stringToVector2D(v, new Vector2D(0.0, 0.0))
    ),
    PHYSIC_MATERIAL(
            "worldMaterial",
            "game.physic.world.material",
            "Set the default Material for the world context",
            Material.AIR,
            v -> IConfigAttribute.stringToMaterial(v, Material.DEFAULT)
    ),
    PHYSIC_MIN_SPEED(
            "physicSpeedMin",
            "game.physic.world.speed.min",
            "set the minimum speed below considered as zero",
            0.01,
            Double::valueOf
    ),
    PHYSIC_MAX_SPEED_X(
            "physicSpeedXMax",
            "game.physic.world.speed.x.max",
            "set the maximum speed on X axis",
            4.0,
            Double::valueOf
    ),
    PHYSIC_MAX_SPEED_Y(
            "physicSpeedYMax",
            "game.physic.world.speed.y.max",
            "set the maximum speed on Y axis",
            0.01,
            Double::valueOf
    ),
    PHYSIC_MIN_ACCELERATION(
            "physicMinAcceleration",
            "game.physic.world.acceleration.min",
            "Set the minimum acceleration below considered as zero",
            0.00001,
            Double::valueOf),
    PHYSIC_MAX_ACCELERATION_X(
            "physicMaxAccelerationX",
            "game.physic.world.acceleration.x.max",
            "Set the maximum acceleration on X axis",
            4.0,
            Double::valueOf),
    PHYSIC_MAX_ACCELERATION_Y(
            "physicMaxAccelerationY",
            "game.physic.world.acceleration.y.max",
            "Set the maximum acceleration on Y axis",
            0.2,
            Double::valueOf),
    DEBUG_WHILE_LIST("debugWhiteList",
            "game.debug.filter.white.list",
            "List of entity to display debug information for",
            "",
            v -> v),

    DEBUG_BLACK_LIST("debugBlackList",
            "game.debug.filter.black.list",
            "List of entity to NOT display debug information for",
            "",
            v -> v),
    SCENE_LIST("sceneList",
            "game.scene.list",
            "List of available scene implementations for that application",
            new String[0],
            v -> Arrays.stream(v.split(",")).toList()
    ),
    SCENE_DEFAULT("sceneDefault",
            "game.scene.default",
            "define the default scene to be activated at start",
            "",
            v -> v),
    GAME_RESHUFFLE_FORCE("reshuffleForce",
            "game.physic.ingame.balls.reshuffle.force",
            "Force used to reshuffle balls' acceleration and move: [width]x[height]",
            200.00,
            Double::valueOf),
    WINDOW_SIZE("wsize",
            "game.window.size",
            "Define Window size",
            new Dimension(640, 400),
            StringUtils::toDimension),
    VIEWPORT_SIZE(
            "viewport",
            "game.viewport.size",
            "Define viewport rendering buffer size: [width]x[height]",
            new Dimension(320, 200),
            StringUtils::toDimension),
    WINDOW_BUFFER_NUMBER(
            "buffer",
            "game.window.strategy",
            "Define the number of display buffer"
            , 1,
            Integer::valueOf);
    private final String attrName;
    private final String attrDescription;
    private final Object attrDefaultValue;
    private final Function<String, Object> attrParser;
    private final String attrConfigKey;

    ConfigAttribute(String attrName, String attrConfigKey, String attrDescription, Object attrDefaultValue, Function<String, Object> parser) {
        this.attrName = attrName;
        this.attrConfigKey = attrConfigKey;
        this.attrDescription = attrDescription;
        this.attrDefaultValue = attrDefaultValue;
        this.attrParser = parser;
    }

    @Override
    public String getAttrName() {
        return attrName;
    }

    @Override
    public Function<String, Object> getAttrParser() {
        return this.attrParser;
    }

    @Override
    public Object getDefaultValue() {
        return this.attrDefaultValue;
    }

    @Override
    public String getAttrDescription() {
        return this.attrDescription;
    }

    @Override
    public String getConfigKey() {
        return attrConfigKey;
    }
}
