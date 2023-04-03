# Add Physic

_(TO BE REVIEWED)_

Here is the most complex thing we are going to manage: move of our GameObject according to some common newton's laws.

The Physic Engine is one more service for our application. we will use the same approach.

![Class diagram for our PhysicEngine](http://www.plantuml.com/plantuml/png/RL11QiCm4Bph5Iev-m7M8GHCw2bGwA7dZRMr5wHLaDQ4KFhtIaZKDib5Q6PtPjWv9O4eYtVgJMRqg6yEYDLC5hL7cODD5O18fEiYcDJe82Nz3XxRzsD-91hFF16ZFlHzt_Yl49tT4cSMaiS562QCAitXtgXd-yOVXc7uKLftscVMsB1S7QhVghs0O2HmJ-frKiApljCC7irdNkEfG8kSidypB0nceT2u9xyZZKA1r-1ojkwwu_uMWnLDSLtOjc9k6w1sVNN7sfEvbrVjR3T1DO4mjOsi-x_I-FPHjV6NenEoNRpx0m00 "Class diagram for our PhysicEngine")

## Some mathematics

The implementation of our `PhysicEngine` service will try to satisfy to some of the Newton's laws of physic.
We will compute the acceleration, sum of all applied forces, then compute the resulting speed to finally define the
position of our `Entity`.

![The acceleration, the spedd and the gravity are used as parameters in physic computation](https://docs.google.com/drawings/d/e/2PACX-1vS1mK0tLz4VBBNbMNIJxtHGTymADBu7emdwWDRA5RIwxEnJQ0DcOFqP4uCc7lFwj77qbLl3Ntm9tzbO/pub?w=549&h=362 "The acceleration, the speed and the gravity are used as parameters in physic computation")

The previous diagram try to illustre the mathematics vectors we will apply to this Entity named "player".

But let's dive into some mathematics.

> _**Note**_
> _If you don't need/want to, just jump to the next "Implementation" paragraph._

The acceleration of applied on a GameObject s the sum of all applied forces, divided by its mass:

$$
a = {\sum(F)\over m} t
$$

where:

- `t` is the elapsed time since previous computation,
- `m` is the mass of the `GameObject`,
- `F` are all the forces applied on this `GameObject`,
- `a` the resulting acceleration.

The speed for this `GameObject` will be the result of the applied acceleration:

$$
v = v_0 * {1\over 2 } a t
$$

where :

- `t` is the elapsed time since previous computation,
- `a` the GameObject acceleration,
- `v0` the previous speed value,
- `v` the resulting new speed.

And, finally our `GameObject`'s position is :

$$
p = p_0 + v * t
$$

- `t` is the elapsed time since previous computation,
- `p0` the previous GameObject position,
- `v` the speed value.

## Implementation

Now I know about theory, let's dive into the code with the core of the service:

### Main PhysicEngine class

```java
class PhysicEngine {
    private Game game;

    private World world;

    public PhysicEngine(Game g) {
        this.game = g;
    }

    public void update(doubel elapsed) {
        //... to be done soon
    }
}
```

- The `game` is a reference to the parent class.
- `world` is an instance of the following `World` object, defining some context and constrains to the world
  where `Entity`'s instances are going to moves.

### The World

To limit the playground of our game and to apply some physic to our entities, we need an object to define the world
constrains:

```java
public class World {

    int paWidth;
    int paHeight;

    public Point2D gravity = new Point2D.Double(0, 0.000981);
}
```

We also need to define some maximum speed and acceleration to manage out physic simulation limit, because, and this is
not the main theme of this chapter and tutorial, Newton's mathematics laws has some limitation, and our game is not a
nuclear physic simulator !

> The goals for those threshold limits is also to avoid infinite values driving to unpredictable behaviors of our
> entities.
> **Evil is hidden in details**, and a too large number of decimals in a number closed to 0.0 is a killing detail.

So here are our technical limitation :

```java
public class World {
    //...
    // speed limits
    public final Double minSpeed;
    public final Double maxSpeedX;
    protected final Double maxSpeedY;
    // acceleration limits
    public double maxAccX;
    public double maxAccY;
    public double minAcc;
    //...
}
```

And we need some specific configuration, so all limitation values are extracted from configuration file (
config.properties):

```properties
#...
# gravity
app.physic.world.gravity=v(0.0,0.000981)
# world play area
app.physic.world.play.area.width=320
app.physic.world.play.area.height=200
# speed limitation
app.physic.world.speed.min=0.000001
app.physic.world.speed.x.max=0.2
app.physic.world.speed.y.max=0.4
# acceleration limitation
app.physic.world.acceleration.min=0.000001
app.physic.world.acceleration.x.max=0.01
app.physic.world.acceleration.y.max=0.1
```

Corresponding to thos following descriptions :

- `app.physic.world.gravity` defines the default gravity for the world ; any object in the play area takes effect from
  this value.
- `app.physic.world.play.area.*` defin ethe play area size in pixels,
- `app.physic.world.speed.*` set the speed limitation and threshold,
- `app.physic.world.acceleration.*` set the acceleration limitation and threshold,

The `World` constructor will set all those values to defined through the `Configuration` service:

```java
public class World {
    //...
    public World(Configuration configuration) {
        gravity = (Point2D) configuration.get(ConfigAttribute.PHYSIC_GRAVITY);
        minSpeed = (double) configuration.get(ConfigAttribute.PHYSIC_MIN_SPEED);
        maxSpeedX = (double) configuration.get(ConfigAttribute.PHYSIC_MAX_SPEED_X);
        maxSpeedY = (double) configuration.get(ConfigAttribute.PHYSIC_MAX_SPEED_Y);
        minAcc = (double) configuration.get(ConfigAttribute.PHYSIC_MIN_ACCELERATION);
        maxAccX = (double) configuration.get(ConfigAttribute.PHYSIC_MAX_ACCELERATION_X);
        maxAccY = (double) configuration.get(ConfigAttribute.PHYSIC_MAX_ACCELERATION_Y);
        paWidth = (int) configuration.get(ConfigAttribute.PLAY_AREA_WIDTH);
        paHeight = (int) configuration.get(ConfigAttribute.PLAY_AREA_HEIGHT);
    }
    //...
}
```

And by the way, you've noticed the new `ConfigAttribute` enumeration items with their default keys, value and
definition :

```java

public enum ConfigAttribute implements IConfigAttribute {
    //...
    PLAY_AREA_WIDTH(
            "playAreaWidth",
            "app.physic.world.play.area.width",
            "set the width of the play area",
            320,
            Integer::valueOf),
    PLAY_AREA_HEIGHT(
            "playAreaHeight",
            "app.physic.world.play.area.height",
            "set the height of the play area",
            200,
            Integer::valueOf),
    PHYSIC_GRAVITY(
            "physicGravity",
            "app.physic.world.gravity",
            "set the 2D vector for gravity applied by physic engine",
            new Point2D.Double(0.0, 0.0),
            v -> {
                return stringToPoint2D(v, new Point2D.Double(0.0, 0.0));
            }),
    PHYSIC_MIN_SPEED(
            "physicSpeedMin",
            "app.physic.world.speed.min",
            "set the minimum speed below considered as zero",
            0.01,
            Double::valueOf
    ),
    PHYSIC_MAX_SPEED_X(
            "physicSpeedXMax",
            "app.physic.world.speed.x.max",
            "set the maximum speed on X axis",
            0.01,
            Double::valueOf
    ),
    PHYSIC_MAX_SPEED_Y(
            "physicSpeedYMax",
            "app.physic.world.speed.y.max",
            "set the maximum speed on Y axis",
            0.01,
            Double::valueOf
    ),
    PHYSIC_MIN_ACCELERATION(
            "physicMinAcceleration",
            "app.physic.world.acceleration.min",
            "Set the minimum acceleration below considered as zero",
            0.00001,
            Double::valueOf),
    PHYSIC_MAX_ACCELERATION_X(
            "physicMaxAccelerationX",
            "app.physic.world.acceleration.x.max",
            "Set the maximum acceleration on X axis",
            0.2,
            Double::valueOf),
    PHYSIC_MAX_ACCELERATION_Y(
            "physicMaxAccelerationY",
            "app.physic.world.acceleration.y.max",
            "Set the maximum acceleration on Y axis",
            0.2,
            Double::valueOf);
    //...
}
```

The new helper `stringToPoint2D()` add the capability to read configuration value as a `Point2D`, primarily used to the
get the gravity value, by converting a `String` value `v([double],[double])` to `Point2D`:

```java
public enum ConfigAttribute implements IConfigAttribute {
    //...
    private static Point2D stringToPoint2D(String value, Point2D defaultValue) {
        if (value == null || value.equals("")) {
            return defaultValue;
        }
        String[] interpretedValue = value
                .substring(
                        "v(".length(),
                        value.length() - ")".length())
                .split(",");
        Point2D convertedValue = new Point2D.Double(
                Double.parseDouble(interpretedValue[0]),
                Double.parseDouble(interpretedValue[1]));
        return convertedValue;
    }
    //...
}
```

### The Physic 'update'

All the processing for the `GameObject` is hidden in the details of the `update()` method:

Applying what we've described at the beginning of this chapter, we compute acceleration, speed, and the position for all
the entities taking part in the `Game` through the `EntityManager` entity collection.

```java
class PhysicEngine {
    //...
    public void update(double elapsed) {

        game.getEntityManager().getEntities().forEach(e -> {
            updateEntity(game, e, elapsed);
            constrained(game, e, elapsed):;
        });
    }

    private void updateEntity(
            Game game,
            Entity e,
            double elapsed) {


        // compute acceleration
        e.addForce(world.gravity);
        e.forces.forEach(f -> {
            e.ax += f.getX();
            e.ay += f.getY();
        });

        e.ax = thresholdMinMax(
                e.ax, world.minAcc, world.maxAccX);
        e.ay = thresholdMinMax(
                e.ay, world.minAcc, world.maxAccY);

        // compute speed
        e.dx += e.ax * (0.5 * (elapsed * elapsed));
        e.dy += e.ay * (0.5 * e.mass * (elapsed * elapsed));

        e.dx = thresholdMinMax(
                e.dx, world.minSpeed, world.maxSpeedX);
        e.dy = thresholdMinMax(
                e.dy, world.minSpeed, world.maxSpeedY);

        // compute position with contact and friction
        double friction = e.contact != 0 ? e.material.friction : 1.0;
        e.x += e.dx * elapsed * friction;
        e.y += e.dy * elapsed * friction;

        e.updateBox();
        e.forces.clear();
    }

    //...
}

```

But this is only the necessary, I need to limit the moves of our objects by constraining the Entity into
the `World` play area defined by `(paWidth, paHeight)`, but taking in account the speed limit defined at the world
level (see `minSpeed`, `maxSpeedX`, `maxSpeedY`) and using the entity's define `Material` `elasticity` value to compute
the bouncing effect in case of collision with play area limits.

```java
class PhysicEngine {
    //...
    private void constrained(Game game, Entity e, double elapsed) {
        e.contact = 0;
        if (e.x + e.width > world.paWidth) {
            e.x = world.paWidth - e.width;
            e.contact = 1;
            e.dx = thresholdMinMax(
                    -e.dx * e.material.elasticity,
                    world.minSpeed,
                    world.maxSpeedX);
            ;
        }
        if (e.y + e.height > world.paHeight) {
            e.y = world.paHeight - e.height;
            e.contact += 2;
            e.dy = thresholdMinMax(
                    -e.dy * e.material.elasticity,
                    world.minSpeed,
                    world.maxSpeedY);
            ;
        }
        if (e.x < 0.0) {
            e.x = 0.0;
            e.contact += 4;
            e.dx = thresholdMinMax(
                    -e.dx * e.material.elasticity,
                    world.minSpeed,
                    world.maxSpeedX);
        }
        if (e.y < 0.0) {
            e.y = 0.0;
            e.contact += 8;
            e.dy = thresholdMinMax(
                    -e.dy * e.material.elasticity,
                    world.minSpeed,
                    world.maxSpeedY);
        }
    }
}
```

## The Game

I now modify the `Game` class to apply physic engine computation to the Entities, which was achieved by the previously
existing `Entity#update()` method itself.
The update is now delegated to the `PhysicEngine` :

```java
public class App extends Game {
    //...
    public int initialize(String[] args) {
        //...
        // Define the rendering system.
        renderer = new Renderer(this);

        // prepare the physic engine.
        physicEngine = new PhysicEngine(this);
        //...
    }

    //...
    @Override
    public void update(Game g, double elapsed) {
        //...
        physicEngine.update(elapsed);
    }
    //...
}
```

Now let's add some other entities:

```java
public class App extends Game {
    //...
    @Override
    public void create() {
        int screenWidth = (int) config
                .get(ConfigAttribute.SCREEN_WIDTH);
        int screenHeight = (int) config
                .get(ConfigAttribute.SCREEN_HEIGHT);
        entityMgr.add(
                new Entity("player")
                        .setFillColor(Color.RED)
                        .setBorderColor(Color.WHITE)
                        .setSize(16.0, 16.0)
                        .setPosition((screenWidth - 32) * 0.5, (screenHeight - 32) * 0.5)
                        .setSpeed(0.0, 0.0)
                        .setAcceleration(0.0, 0.0)
                        .setDebug(2)
                        .setMaterial(Material.RUBBER));

        for (int t = 0; t < 30; t++) {
            entityMgr.add(
                    new Entity("ball_" + t)
                            .withFillColor(Color.BLUE)
                            .withBorderColor(Color.CYAN)
                            .withMass(0.01)
                            .setSize(8.0, 8.0)
                            .setPosition(
                                    Math.random() * screenWidth,
                                    Math.random() * screenHeight)
                            .setSpeed(
                                    Math.random() * 20.0,
                                    Math.random() * 20.0)
                            .setAcceleration(0.0, 0.0)
                            .setDebug(2)
                            .setMaterial(Material.SUPER_BALL));
        }
    }
    //...
}
```

Only adding a simple red square is a good start, but with interaction would be better no ?

### Cursor keys to move !

We need to do some action when the cusor keys are pressed:

|       Key        | Action                   |
|:----------------:|:-------------------------|
|  <kbd>UP</kbd>   | move player up           |
| <kbd>DOWN</kbd>  | move player down         |
| <kbd>LEFT</kbd>  | move player to the left  |
| <kbd>RIGHT</kbd> | move player to the right |

We need to enhance the input() method from App:

```java
public class App implements Game {
    //...
    @Override
    public void input() {
        //...
        // (1)
        boolean move = false;
        double accelerationStep = 200.0;
        double jumpFactor = 0.5 * accelerationStep;

        // (2)
        Entity<?> player = entityMgr.get("player");
        // (3)

        // move up
        if (inputHandler.getKey(KeyEvent.VK_UP)) {
            player.addForce(new Point2D.Double(0.0, -jumpFactor));
            move = true;
        }
        // move Down
        if (inputHandler.getKey(KeyEvent.VK_DOWN)) {
            player.addForce(new Point2D.Double(0.0, accelerationStep));
            move = true;
        }
        /// move Left
        if (inputHandler.getKey(KeyEvent.VK_LEFT)) {
            player.addForce(new Point2D.Double(-accelerationStep, 0.0));
            move = true;
        }
        // move right
        if (inputHandler.getKey(KeyEvent.VK_RIGHT)) {
            player.addForce(new Point2D.Double(accelerationStep, 0.0));
            move = true;
        }
        // (4)
        if (!move) {
            if (Optional.ofNullable(player.material).isPresent()) {
                player.dx *= player.material.friction;
                player.dy *= player.material.friction;
            }
        }
    }
    //...
}
```

1. we define some internal variables to support move acceleration for a simple step and the force value to be used in a
   Jumping action.
2. We retrieve the 'player' `Entity` from the `EntityManager`
3. let's detect keys pressed for <kbd>UP</kbd>, <kbd>DOWN</kbd>, <kbd>LEFT</kbd> and <kbd>RIGHT</kbd>, and apply the
   corresponding force, here is
   a [Point2D](https://download.java.net/java/early_access/panama/docs/api/java.desktop/java/awt/geom/Point2D.html "Go and read the corresponding JDK19 javadoc for Point2D interface")
   vector, on both axis for vertical and horizontal moves,
4. If not move require, let's apply the default friction effect from the Entity's material itself.

So, run the application with :

```bash
$> gradle run
```

Here be the following window with some `GameObject`, :

![Multiple object with physic and interaction](illustrations/figure-add-physic-engine_screenshot.png  "Multiple object with physic and interaction")

Please, have fun by pressing the CURSOR quad keys to move your red square player entity.

## Conclusion

If we stick to the previous posts, you may know now that: the
GitHub [Game101](https://github.com/SnapGames/game101/ "Open the GitHub project game101") project hosts a specific tag
for this
part: [create-physic-engine](https://github.com/SnapGames/game101/releases/tag/create-physic-engine "Go and explore the code from this tag create-physic-engine").

See you soon to add 7th’s chapter talking about Camera and more !

That’s all folks !

McG.
