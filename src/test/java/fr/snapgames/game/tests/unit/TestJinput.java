package fr.snapgames.game.tests.unit;

import net.java.games.input.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class TestJinput {


    @Test
    public void detectControllersTest() {
        /* Create an event object for the underlying plugin to populate */
        Event event = new Event();


        /* Get the available controllers */
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        Arrays.stream(controllers)
                .filter(c -> c.getType().toString().equals("Gamepad"))
                .forEach(c -> {
                    System.out.printf("- name=%s : type=%s : port=%d%n", c.getName(), c.getType(), c.getPortNumber());
                });
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
