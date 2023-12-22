# Add Keys and mouse

_(TO BE REVIEWED)_

In the previous parts, we created a Game main class, added some configuration capabilities, started rendering and
displaying things, and latest, we built up some basic entities and their manager.

It is now time to interact a little with our entities and do some moves.

As we rely on the JDK, we will just implement Mouse and Key listener, but elegantly to help extend capabilities later.

To make it work, I am going to write an InputHandler class implementing the KeyListener and the MouseListener
interfaces:

![A class Diagram of our InputListener](http://www.plantuml.com/plantuml/png/PS-zJiKm30NWFKyHTbWOErM81G5ITyIGjWV58h6Zs16g4EyEvGUbzuxvudDaZsgXMil9NLX4XYyf4Bj8Ato6HLdrvn7CAitDe8xOK5_20lyeJV50Qc3Kpk-n_UkIWgg_uzBipppbaf31fltevQ7ktVahNofUiUamYoG4VdhUyLpo7fn4oKXuc8JOhcz_yz-bDyrC-3JL_Np9XzCeRyTD-xD7yDfo-WG0 "A class Diagram of our InputListener")

InputHandler is implementing standard JDK interfaces.

## Handling the input

So, our class with start with some internal attributes:

```java
public class InputHandler implements KeyListener, MouseListener {
    private boolean[] preKeys = new boolean[65535];
    private boolean[] keys = new boolean[65535];

    private Point mousePosition;

    private boolean[] preMouseButtons;
    private boolean[] mouseButtons;
}
```

The 2 first arrays of boolean, keys, and prevKeys, will store the key’s states. keeping the latest and the previous one
will help us to detect if a key has been pressed or released. In an evolved version, in a future chapter, we may add
some events.

And by the way, the next attributes are dedicated to the mouse events management with a mousePosition to store 2D mouse
coordinates, and two arrays of boolean to store, like for keys: preMouseButtons and mouseButtons previous and current
states of the mouse buttons.

And we need to initialize our mouse boolean arrays:

```java
public class InputHandler implements KeyListener, MouseListener {
    public InputHandler() {
        int msButtons = MouseInfo.getNumberOfButtons();
        preMouseButtons = new boolean[msButtons];
        mouseButtons = new boolean[msButtons];
    }
}
```

With the first MouseInfo API call, I gather the number of available mouse buttons, defined by the system itself, and then
set the 2 corresponding arrays.

We must implement the 2 JDK interfaces, starting with the KeyListener one:

```java
public class InputHandler implements KeyListener, MouseListener {
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        preKeys[e.getKeyCode()] = keys[e.getKeyCode()];
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        preKeys[e.getKeyCode()] = keys[e.getKeyCode()];
        keys[e.getKeyCode()] = true;
    }
}
```

And the MouseListener one:

```java
public class InputHandler implements KeyListener, MouseListener {
    @Override
    public void mouseClicked(MouseEvent e) {
        mousePosition = e.getPoint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePosition = e.getPoint();
        preMouseButtons[e.getClickCount()] = mouseButtons[e.getClickCount()];
        mouseButtons[e.getClickCount()] = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mousePosition = e.getPoint();
        preMouseButtons[e.getClickCount()] = mouseButtons[e.getClickCount()];
        mouseButtons[e.getClickCount()] = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mousePosition = e.getPoint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        mousePosition = e.getPoint();
    }
}
```

Now we maintain some internal states for the mouse and keys.

We need some interface to request the status for those two devices from our Game class:

```java
public class InputHandler implements KeyListener, MouseListener {
    public boolean getKey(int keyCode) {
        return keys[keyCode];
    }

    public boolean isKeyPressed(int keyCode) {
        return !preKeys[keyCode] && keys[keyCode];
    }

    public boolean isKeyReleased(int keyCode) {
        return preKeys[keyCode] && !keys[keyCode];
    }
}
```

- with the `getKey()` you can request in real time the current status of a key,
- the `isKeyPressed()` will return true if the key has been pressed,
- and the `isKeyReleased()` will return true if the required key has been released.

And on the mouse side:

```java
public class InputHandler implements KeyListener, MouseListener {
    //...
    public boolean getMouseButton(int mouseButtonCode) {
        return mouseButtons[mouseButtonCode];
    }

    public boolean isMouseButtonPressed(int mouseButtonCode) {
        return !preMouseButtons[mouseButtonCode]
                && mouseButtons[mouseButtonCode];
    }

    public boolean isMouseButtonReleased(int mouseButtonCode) {
        return preMouseButtons[mouseButtonCode]
                && !mouseButtons[mouseButtonCode];
    }
    //...
}
```

- `getMouseButton()` method returns the state for the given button,
- and the `isMouseButtonPressed()` method tells if a mouse button has been pressed.
- while the `isMouseButtonReleased()` method is true if a mouse button has been released.

## Adapt the Game

Now I’ve got a fully implemented InputHandler, I can add it to my Game class:

```java
public class App implements Game {
    //...
    private InputHandler inputHandler;
    //...
}
```

and initialize it:

```java
public class App implements Game {
    //...
    public int initialize(String[] args) {
        //...
        window = new Window(...);
        inputHandler = new InputHandler();
        window.addListener(inputHandler);
    }
}
```

So we need to adapt the Window class to let it manage Mouse:

```java
class Window extends JPanel {
    //...
    public void addListener(InputHandler inputHandler) {
        frame.addKeyListener(inputHandler);
        frame.addMouseListener(inputHandler);
    }
    //...
}
```

And finally, connect it to the input method:

```java
public class App implements Game {
    //...
    public void input() {
        if (inputHandler.isKeyPressed(KeyEvent.VK_ESCAPE)) {
            requestExit(true);
            logger.log(Level.FINEST,
                    "    - key {} has been released",
                    new Object[]{KeyEvent.getKeyText(KeyEvent.VK_ESCAPE)});
        }
    }
//...
}
```

Now, after running the App class by executing :

```bash
$> gradle run
```

![An empty Window but with an exit button !](/illustrations/figure-inputhandler-screenshot-01.png "An empty Window but with an exit button !")

You can exit the app demo by pressing the [ESCAPE] key.

## Conclusion

Since the first chapter, we use the [game101](https://github.com/SnapGames/game101) project on GitHub to support our
posts, so keep this habit,
and you will find the [create-input-handler](https://github.com/SnapGames/game101/releases/tag/create-input-handler) tag linked
to this new chapter.

That’s all folks!

McG.