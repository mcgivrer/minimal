# Adding JInput

THe JInput library wil offer the opportunity to connect any device like joystick, joypad or gamepad to interact with the
Game.

## tricking the pom

first add some libraries to the maven project to get benefits from the JInput library;

```xml

<dependecies>
    <dependency>
        <groupId>net.java.jinput</groupId>
        <artifactId>jinput</artifactId>
        <version>2.0.9</version>
    </dependency>
    <dependency>
        <groupId>net.java.jinput</groupId>
        <artifactId>jinput</artifactId>
        <version>2.0.9</version>
        <classifier>natives-all</classifier>
    </dependency>
</dependecies>
```

And in te build/plugins:

```xml

<plugin>
    <groupId>com.googlecode.mavennatives</groupId>
    <artifactId>maven-nativedependencies-plugin</artifactId>
</plugin>
```

And a first simple code to detect devices (extracted
from [JInput the official web site](https://jinput.github.io/jinput/))

```java
public class TestJInput {
    public void TestJInput() {
        /* Create an event object for the underlying plugin to populate */
        Event event = new Event();

        /* Get the available controllers */
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        for (int i = 0; i < controllers.length; i++) {
            /* Remember to poll each one */
            controllers[i].poll();

            /* Get the controllers event queue */
            EventQueue queue = controllers[i].getEventQueue();

            /* For each object in the queue */
            while (queue.getNextEvent(event)) {
                /* Get event component */
                Component comp = event.getComponent();

                /* Process event (your awesome code) */
                //...
            }
        }
    }
}

```