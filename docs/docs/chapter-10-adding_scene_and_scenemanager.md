# Adding Scene and Scene Manager

_(TO BE REVIEWED)_

## Goals

Add a scene manager to easily switch from one game play to another. A gameplay can be the Title screen, the menu screen
where the player select to start a new game, the Map for this famous RPG game, the Inventory screen where the player
manage all its items.

It will also be the main play screen where everything happen !

![An hypothetical implementation of a game with scenes](https://www.plantuml.com/plantuml/png/LO_12i8m38RlVOeSjyDUOCBOpPCep1SexaSBNJlJLDtRcrKdU4k-BtoGFebboPCtPJ14xTc9nt11mCuOnWnRy04XRvJenzMSl5q6iQ9c4_IyEG-ghdP2JIvH5Q9GV81hqgEcezMeAAmKsxsIesxVr7y8BoUV44DuqKhJhOmOz-mOepQInDdq2AE-ymO0 "An hypothetical implementation of a game with scenes")

## Proposed implementation

The following diagram propose an implementation layering on a `Scene` interface, an `AbstractScene` implementation to
provide
internal mechanism, and a `SceneManager`.

![Implementation class diagram](https://www.plantuml.com/plantuml/png/TP11RkCm34NtEeLcby5CBk15aQ85kkgcEG4Z_Te295aWgQHeqRlNhemuJT0DORx_QDhSYH9hJT7yqG49jaGAsGp1moReC7ff3QboE5I561T9n3vHWWbPmQ_aOFVWOt1Omhr3nZDbwi4sf1AHa5NEbWpZgKdBblLD3NyO-o_ae85YUCUcx-PrVxlZ6FoK54OXli6EGNf3GovLhs4jVbN_MqGg0j_viHxYQdccqzdXxWNPWy5h7gICWiqPzD27IMQQK7imdyRGGPJ9gMt7kL_Qzl-Nrvc1QT8ZqjW4l2colnepypVPxVRknwdDPVETpbhNJ-KgMkOwcQly4hhAZAncX-nhYby0)

## Scene interface

The scene interface describe the scene lifecycle and all its methods:

- `initialize(Game)` is the first called by the Game implementation, juste after configuration has been loaded (
  see `SceneManager#initialize(Game)` for details.
- `prepare(Game)` is called to initialize specific services that won't be already initialized by the `AbstractScene`
  implementation,
- `create(Game)` will create/instantiate all required `GameObject` for the scene description,

Then the lifecycle operation:

- `input(Game)` here is where the Scene will manage all the device input: mouse and keyboard,
- `update(Game, double)` this is where specific update mechanics for a {@link Scene} must be implemented,
- `draw(Game, Renderer)` specific drawing operation (after the GameObject rendering, then the specific effects for this
  scene can be rendered,

And finally the ending operation :

- `dispose()`  release all the loaded / instantiated resources objects.

## The Scene manager

![The SceneManager class implementation with its dependencies](http://www.plantuml.com/plantuml/png/TP3DIaCn48NtUOgugsXVu2waek3AZJx1zEPuxq1-mip4YkAxssjAAorkGkPytu5aDcearb9qLpOZmTT8dDtC0Jx1vXAq3sGcl6q6TMCaLVz42OwpGTveX7yTaV7b_0rdDgR8drXDmo9T3--5mvvKGIMvY4S1sKNAkJOx3riD9_NM8r9LX26KjeqdAmZjZeuCByW10ZbnrbeKgsCmmHueGMWVzOlxTZgNNV_LOH6Ejcf2e_c-WFl3s1rEDI8Xc_QIwz8ZKka2FWmvdDNSyaY_Nj_rqZDbcY3kx_1B5ssOAE4C7Ng3BgV-0yScWcnkWnnQYZy0 "The SceneManager class implementation with its dependencies")

### loading configuration

The `SceneManager#initialize(Game)` is loading from the configuration file the list of available scene implementation
and the default activated one:

- `app.scene.list` contains the comma separated list of Scene class implementation with their scene id separated by
  a ':' :

```properties
app.scene.list=demo:com.merckgroup.demo.scenes.MyOwnSceneClass, title:com.merckgroup.demo.scenes.MyTitleSceneClass" and the list of instances
```

is store in the availableScenes list.

- `app.scene.default` the default scene id to be activated at start:

```properties
app.scene.default=demo
```

### Add a scene

A new scene from the configuration scene is added to the list a Scene class implementation.

### Activate a scene

At activation time, the scene is instantiated and stored in a scenes instance list.
At anytime, the current scene can be retrieved.

### Using an abstract implementation for scene

The default mechanism to retrieve dependencies to other services to be used by any Scene implementation
are set into the `AsbtractScene`:

```java
abstract class AbstractScene implements Scene {
    protected Configuration config;
    protected Renderer renderer;
    protected EntityManager entityMgr;
    protected PhysicEngine physicEngine;

    protected InputHandler inputHandler;

    //...
    public void initialize(Game g) {
        config = g.getConfiguration();
        renderer = g.getRenderer();
        entityMgr = g.getEntityManager();
        physicEngine = g.getPhysicEngine();
        inputHandler = g.getInputHandler();
        prepare(g);
    }
    //...
}
```

The default current scene camera is also delegated to the AbstractScene:

```java
abstract class AbstractScene implements Scene {
    //...
    protected Camera camera;
    //...

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
        this.renderer.setCurrentCamera(camera);
    }
}
```

And finally, the instantiation of any Scene is partially delegated to the AbstractScene class:

```java
abstract class AbstractScene implements Scene {
    protected Game game;
    protected String name;

    //...
    public AbstractScene(Game g, String name) {
        logger.log(Level.INFO, "Instantiate scene {}", name);
        this.game = g;
        this.name = name;
    }
    //...
}
```

## THe Demo

The `DemoScene` is now our previous App game mechanism to be slipped into a new Scene interface.

The main things are the `create()` method where GameObject are created, and the `input` method where the `GameObject`
"player" interaction are managed with the device input:

```java
class DemoScene extends AbstractScene {
    //...
    @Override
    public void create(Game g) {
        // Create the main player entity.
        var player = (GameObject) new GameObject("player")
                .setType(Rectangle2D.class)
                .setFillColor(Color.RED)
                .setBorderColor(new Color(0.3f, 0.0f, 0.0f))
                .setSize(16.0, 16.0)
                .setPosition((screenWidth - 32) * 0.5, (screenHeight - 32) * 0.5)
                .setSpeed(0.0, 0.0)
                .setAcceleration(0.0, 0.0)
                .setMass(100.0)
                .setDebug(1)
                .setMaterial(Material.STEEL)
                .setLayer(1)
                .setAttribute(MOVE_STEP_SPEED, 50.0)
                .setAttribute(PLAYER_SCORE, (int) 0)
                .setAttribute(PLAYER_LIVE, (int) 5);
        entityMgr.add(player);
        //...
    }
    //...
}
```

And the input management are transferred:

```java
class DemoScene extends AbstractScene {
    //...
    @Override
    public void input(Game g) {
        EntityManager emgr = g.getEntityManager();
        boolean move = false;
        GameObject player = (GameObject) emgr.get("player");
        double moveStep = (double) player.getAttribute(MOVE_STEP_SPEED, 200.0);
        double jumpFactor = moveStep * 5.0;
        //...
        if (inputHandler.getKey(KeyEvent.VK_UP)) {
            player.addForce(new Point2D.Double(0.0, -jumpFactor));
            move = true;
        }
        if (inputHandler.getKey(KeyEvent.VK_DOWN)) {
            player.addForce(new Point2D.Double(0.0, moveStep));
            move = true;
        }
        if (inputHandler.getKey(KeyEvent.VK_LEFT)) {
            player.addForce(new Point2D.Double(-moveStep, 0.0));
            move = true;
        }
        if (inputHandler.getKey(KeyEvent.VK_RIGHT)) {
            player.addForce(new Point2D.Double(moveStep, 0.0));
            move = true;
        }
        //...
    }
    //...
}
```

And we finally need to adapt App class.

## App class update

The new Service implementation `SceneManager` must be initialized:

```java
public class App implements Game {
    //...
    @Override
    public int initialize(String[] args) {
        //...
        // add the SceneManager (scene list loaded at initialization)
        sceneMgr = new SceneManager(this);

        logger.log(Level.INFO, "initialization done.");
        return initStatus;
    }
    //...
}
```

### Scene delegation

And we need to create the scene:

```java

public class App implements Game {
    //...
    @Override
    public void create() {
        logger.log(Level.INFO, "- create scene content for {0}", getAppName());
        sceneMgr.getCurrent().create(this);
    }
}
```

Then delegates the input management for the active scene:

```java
public class App implements Game {
    //...

    @Override
    public void input(Game g) {

        logger.log(Level.INFO, "- Loop {0}:", updateTestCounter);
        logger.log(Level.INFO, "  - handle input");
        sceneMgr.getCurrent().input(this);

    }
}
```

Also update operation for object and scene :

```java
  public class App implements Game {
    //...

    @Override
    public void update(Game g, double elapsed) {
        logger.log(Level.INFO, "  - update thing {0}", elapsed);
        updateTestCounter += 1;


        physicEngine.update(elapsed);
        if (Optional.ofNullable(sceneMgr.getCurrent().getCamera()).isPresent()) {
            sceneMgr.getCurrent().getCamera().update(elapsed);
        }

        sceneMgr.getCurrent().update(this, elapsed);
    }
}
```

And if required, offers the opportunity for the Scene implementation to create its own rendering operation if required.

```java
    public class App implements Game {
    //...

    @Override
    public void render(Game g, int fps) {
        logger.log(Level.INFO, "  - render thing at {0} FPS", fps);
        renderer.draw();
        sceneMgr.getCurrent().draw(this, renderer);
        window.drawToWindow(renderer.getImageBuffer());
    }
}
```

Here we are !

![Displaying the scene name in debug mode](illustrations/figure-adding_scene_and_manager.png "Displaying the scene name in debug mode")

## Conclusion

With this new chapter, we implemented a `Camera` object that tracks a `GameObject` with a certain delay in the following moves.
According to our project, you will find this corresponding code with the tag [create-camera](https://github.com/SnapGames/game101/releases/tag/create-camera) in the [Game101](https://github.com/SnapGames/game101/) GitHub repository.
