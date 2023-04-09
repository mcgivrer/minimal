# The Game class

_(TO BE REVIEWED)_

Building a game can be a game itself; or a nightmare. Depending on the tools, the team, the skills, … a lot of thing can
interact badly with the process of creating a game. And this is exactly what we won't talk about!:)

Here I will talk about creating a A-B-AB game with the only weapons provided by the nice Java JDK 19:) And no, I won't
use JavaFX (beuarrk:)) I'll use the good old AWT and Swing (yes, I am maybe a 50 years old man…).

Anyway let's try it through a series of posts to design some code.

## Creating a simple class

The basic Game class will support the famous "triptic" of the game loop: manage input, update objects, and draw
everything on screen!
But do you know why this is the basic default pattern for a game ?

## A bit of video game history

Why the game loop exists ? This is a very good question and the reason why is a historically based answer. Everything
starts from the first ever video game: PONG.

![Pong, where all begin !](https://cdn-images-1.medium.com/max/800/0*ySNC72GHeT19Nq3N "wikipedia PONG screenshot")

_figure 1 - Pong where all begins (ref:[https://fr.wikipedia.org/wiki/Pong](https://fr.wikipedia.org/wiki/Pong))_

The original Pong video game from wikipediaAt this very beginning time, the processor to execute tasks is a very a slow
on, almost some hundreds of Khz as CPU frequency. To understand the scale we are talking about, current processor are
running at 2 to 4 GHz!
So processor are very slow, each cycle of CPU is a precious one. So every line of code is very optimized and clearly
dedicated to some precise tasks.

And another element must be taken in account: the display process. At this time, screen where not flat one with a bunch
of LCD, but CRT ones. CRT display screen are based on ionic flow started from a cathode (electronic gun) and moving to
the anode (the screen grid) to excite fluorescent layer in the intern face of the glass bulb.

And swiping the all surface of the screen has a time cost: to display 25 frame per seconds, we need 16ms to swipe a
frame.
A CRT tube with its ions gun!The CRT Tube is nothing more than a big bubble light. (3) the cathode emits ions (1) and
(2) are anodes, deflecting ion ray to screen, lighting a fluorescent dot.

![A CRT diagram with ions gun and anodes deflectors](illustrations/figure-crt.jpg "A CRT diagram with ions gun and anodes deflectors (c) myself with my own hands !")

_figure 2 - A CRT diagram with ions gun and anodes deflectors_

This is the available time for the CPU to prepare next image!

So capturing input, moving things and displaying things must be done in 16ms. And loop again for the next frame.

So the main process is a LOOP. that's why we talk about a Game Loop:

![The basic Game loop explained with a pencil: the method to keep a fixed frame rate !](illustrations/figure-game-loop.jpg "the basic Game loop explained with a pencil: the method to keep a fixed frame rate ! (c) myself with my own hands !")

_figure 3 - The basic Game loop explained with a pencil: the method to keep a fixed frame rate !_

There is also some advanced version of the Game Loop, where multiple update can be performed between each rendering
phase, the timer is around the update methods only:

![The advanced method to keep a fixed update rate](illustrations/figure-game-loop-fixed.jpg "The advanced method to keep a fixed update rate (c) myself with my own hands !")

_figure 4 - The advanced method to keep a fixed update rate_

I can only invite you to read the fantastic book from Robert Nystrom for details about the Game loop.

> **Note:** diagram are largely inspired by the Robert Nystrom book, thanks to him to had shared his own knowledge!

Anyway, I need to implement my own. As a good diagram is better than word:

![A good diagram explaining the Game class and its usage](http://www.plantuml.com/plantuml/png/NOqngiCm341tdq9_-nrwWGmbMyyXOj4ARDdO4YazVULG2l4IulUafxKhDhMSmfy-AHFKX2mX-mUkDxXZfWM4zZ3-VeI5bJ7nc_ulN-FgM5hWCTui7fQDJYMNpUISsXgXKaYbL31HJa0lrW0m7QocVWi0au8KXOhMAJgO9gr6xnsZ977kD6VKt4vyHnxviN7YaNijVUHMTvRJ1m00 "figure 5 - A good diagram explaining the Game class and its usage")

_figure 5 - A good diagram explaining the Game class and its usage_

## The Game

And as any tutorial - guide - how-to won't exist without code; here it is!

```java
public class Game {
    public App(String[] args) {
        initialize(args);
    }

    //... need to implement some initialization process
    public void run() {
        create();
        loop();
        dispose();
    }

    public void create() {
        // create resources for the game.
    }

    public void loop() {
        while (!isExit()) {
            input();
            update();
            render();
            waitUntilNextFrame();
        }
    }

    public void dispose() {
        // will created free resources
    }

    public static void main(String[] args) {
        Game game = new App(args);
        game.run();
    }
}
```

So, what do you think of my (too?) basic class ? nothing fancy, nothing "bling bling". only useful and mandatory.

So yes, as you maybe now, the gameloop is the heart of any old school game. and even some Big game engine keep running
such old loop.
Let's dive into some details.

The Loop is certainly the most important one, so let get some code:

```java
public class Game {
    //...
    public void loop() {
        int frames = 0;
        int fps = getTargetFps();
        double internalTime = 0;
        double previousTime = System.currentTimeMillis();
        double currentTime = System.currentTimeMillis();
        double elapsed = currentTime - previousTime;
        create(this);
        while (!isExitRequested()) {
            currentTime = System.currentTimeMillis();
            input(this);
            elapsed = currentTime - previousTime;
            if (!isPaused()) {
                update(this, elapsed);
            }
            render(this, fps);
            frames += 1;
            internalTime += elapsed;
            if (internalTime > 1000.0) {
                fps = frames;
                frames = 0;
                internalTime = 0;
            }
            waitUntilNextFrame(elapsed);
            previousTime = currentTime;
        }
    }

    default void waitUntilNextFrame(double elapsed) {
        try {
            double timeFrame = 1000.0 / getTargetFps();
            int wait = (int) (elapsed < timeFrame ? timeFrame - elapsed : 1);
            Thread.sleep(wait);
        } catch (InterruptedException ie) {
            System.err.println("error while trying to wait for sometime");
        }
    }
}
//...
}
```

So to maintain a sustainable frequency for that loop, I use the default current traditional FPS: 60 frames per second.

So starting with that , defining the waiting time is a simple calculus.

```java
int timeFrame=1000/60;
        int wait=timeFrame-elapsed;
```

and the rest of the loop process is simple calls:

```java
elapsed=currentTime-previousTime;
        input();
        update(elapsed);
        render();
```

- input() will manage all the player's device input like keyboard, mouse or joystick,
- update(elapsed) will compute all the moves and animations for all the game objects,
- render() will draw the resulting objects and display that on screen.

And when you execute this incredible class… nothing will happen.
BUT… we now have the framework of our future game. I'm not joking, this is a fact ;)

## Going deeper in concept

To be sure that this core structure will not be alrtered by a wrong or bad action from an anonymous developer (??) we
are goinf to externalize this GameLoop and the game structure inti an interface.

- The `Game` default interface,
- The `App` implmenting this interface.

an overview of the solution:

![An overview of my implementation](http://www.plantuml.com/plantuml/png/VP0nJyCm48Lt_mgpfQByWR2q0x0Y6173jRwc9pbdupiZ1Ef_9sbT31QGANxldNtlMub2qZnwSw5x7D563yHcJmujg2RQP4Knn8ff1rkqTp25FeoTlajYKW9FxzX16gH_dF8yF3qlJlOs8IXsnucXQCGnwqyfHrTZI-j2tdYqw24Akf99o0xJLuPOxIvACNq8iIl_ZYG6mVEHv1woeAldcpVI1tp9A5hbbBaxBs_dAWjpC6RhGSIkOxGqHeyZiiht_bSutNNhOUWt8NW4FgbnVh6H_uFLlmPovz4ak9xSFo8Lwj3FYnIZactqYhSOI_GH15r_f5np5N79pNXxObrSDrnF4mtSfca3kR7d2-m8nB-MF9WLiikZ_mG0 "figure 1 - An overview of my implementation")

_figure 6 - An overview of my implementation_

## The GameLoop interface

The `GameLoop` interface provides a standard contract to delegate the loop management to a specialized class
implementation.

We will be able to create a Fixed time frame loop, where graphics are rendered in priority , or a Fixed time update loop
where physic and gameplay are prioritized first.

Defining a standard loop to a game with:

1. `input()`,
2. `update()`,
3. and then `draw()` on the screen.

This is exactly what our loop implementation will have to provide.

```java
public interface Game {

    void loop(Map<String, Object> context);

    boolean isExit();

    boolean isTestMode();

}
```

Let's have a try with a FixedFrameGameLoop first, where the Game loop update and render everything even if it takes too
long, freezing the game play.

### The Fixed Frame Loop

The default loop implementation would be a simple time frame based one. Trying to stick as far as possible to the frame
per second value set. In our sample code, we use a 1000.0/60.0 frame time corresponding to 60 frames per second.

First things first, just initialize our GameLoop for the game :

```java
public interface FixedFrameGameLoop {

    private final Game game;
    double FPS = 60.0;
    double fpsDelay = 1000000.0 / 60.0;

    public FixedFrameGameLoop(Game game) {
        this.game = game;
    }
    //...
}
```

And if the loop is during less than a frame time, add a little wait.

```java
public interface FixedFrameGameLoop {
    //...
    default void loop() {
        double start = 0;
        double end = 0;
        double dt = 0;
        // FPS measure
        long frames = 0;
        long realFPS = 0;

        long updates = 0;
        long realUPS = 0;
        long timeFrame = 0;
        long loopCounter = 0;
        int maxLoopCounter = (int) game.getConfiguration().get(ConfigAttribute.EXIT_TEST_COUNT_FRAME);
        Map<String, Object> loopData = new HashMap<>();
        while (!(isExit() || isTestMode() || isMaxLoopCounterReached(loopCounter, maxLoopCounter))) {
            start = System.nanoTime() / 1000000.0;
            loopCounter++;
            game.input();
            if (!game.isUpdatePause()) {
                game.update(dt * .04);
                updates += 1;
            }

            frames += 1;
            timeFrame += dt;
            if (timeFrame > 1000) {
                realFPS = frames;
                frames = 0;
                realUPS = updates;
                updates = 0;
                timeFrame = 0;
            }
            prepareData(realFPS, realUPS, loopCounter, loopData);

            game.draw(loopData);
            waitUntilStepEnd(dt);

            end = System.nanoTime() / 1000000.0;
            dt = end - start;
        }
    }

    default void waitUntilNextFrame(double elapsed) {
        try {
            double timeFrame = 1000.0 / getTargetFps();
            int wait = (int) (elapsed < timeFrame ? timeFrame - elapsed : 1);
            Thread.sleep(wait);
        } catch (InterruptedException ie) {
            System.err.println("error while trying to wait for sometime");
        }
    }

    private void prepareData(long realFPS, long realUPS, long loopCounter, Map<String, Object> loopData) {
        loopData.put("cnt", loopCounter);
        loopData.put("fps", realFPS);
        loopData.put("ups", realUPS);
        loopData.put("pause", game.isUpdatePause() ? "ON" : "OFF");
        loopData.put("obj", game.getSceneManager().getActiveScene().getEntities().size());
        loopData.put("scn", game.getSceneManager().getActiveScene().getName());
        loopData.put("dbg", game.getDebug());
    }
    //...
}
```

And we need to link the exit and pause mode to the game with these 2 methods:

```java
public class FixedFrameGameLoop {
    //...
    @Override
    public boolean isTestMode() {
        return game.isTestMode();
    }

    @Override
    public boolean isExit() {
        return game.isExitRequested();
    }
}
```

You can notice that we also compute the real frame rate (`fps`) with the `end` and `start` variables, and
maintain an internal `frames` counter. An `ups` counter is also maintained.

## Conclusion

BUT… we now have the framework of our future game. I am not joking, this is a fact ;)

On the next step, nothing more fancy, we will speak about configuration file and CLI parsing, with some real code in a
good old GitHub repo. Yes, before going into entertainment, we need to have solid foundation to build upon some fun, and
that will come later with some red squares and blue balls moving randomly on the screen.

You will find the corresponding code on this [game101](https://github.com/SnapGames/game101) GitHub repository on
tag [create-game](https://github.com/SnapGames/game101/releases/tag/create-game "go and see files from tag 'create-game'")

That's all folks!

McG.
