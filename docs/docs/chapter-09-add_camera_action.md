# Add Camera, Action !

_(TO BE REVIEWED)_

## Required

Add a `Camera` Object to move game view according to a target position in the game play area. the target must be
an `Entity`.

- add a smooth effect between target and the `Camera` move.

![Adding a camera will help to implement a platformer]( https://docs.google.com/drawings/d/e/2PACX-1vSWff4JKLV2ZThVlxOQqF0Cnr-zMMUBiKa3b0NFGl_LuFx4HvWrp11RVMtupieJSdXqI6cU7wTCoK0B/pub?w=619&h=404 "Adding a camera will help to implement a platformer")

Here are:

- the play area at (0,0) with a size of 500 x 400,
- a player GameObject at (p.x,p.y) with a size of 16x16,
- the Camera object is targeting the player entity with a specific tween factor used to add some elasticity and delay in
  the tracking operation.

## Implementation proposal

The Camera object will drive the rendering view port to follow a `GameObject` `target`, with a certain delay fixed by
the `tween` factor value.

### The Camera object

The `Camera` class will inherit from `Entity` and introduce 3 new attributes:

![The Camera mechanism implementation](http://www.plantuml.com/plantuml/png/TP11QmCX48Nl_eeflQI4N7hqK1BIqcCX_GMpwv2rg2iwxYQK_UzLfB9BwKsVRzxtncDa39oi4Gyya2Nm1hKJWrO4bdWOLHJOHGxOC_G6OuGtnvhly2tW4RIIe1sNeqDB4ZuvQ7SLYwZUzon-T5-KXxNr9WCS_oRaW3tS9AWnTORAvTukYuZ3ECCvZmTiHMfc5O5kJ4wj4QKI780nN8dbwg7ACv5hWA9CccO_XcozP1Ewgw7dLm6vIF8AcNPaq4TIiiuyoMdKIYprSqxU0p87tIMcMEiMRTkrA6tnshMKt4VYKyzwehhChaShwt5BkP1J52ZyGRM7BQ_9cL8WvnH29lzFAGKFXnUeiiHLAT72LeP-7IYEsP6i-G40
"The Camera mechanism implementation")

- _target_ which is the target to be tracked by the camera,
- _tween_ the delay factor to make the camera follow target smoothly,
- _viewport_, a rectangle corresponding to the camera view port.

And the main `Camera` processing will be a dedicated computing on the target position, according to the tween delay
factor:

```text
cam.pos = cam.pos
    + (target.pos-(viewport.size-target.size)) 
      x 0.5
      x tweenFactor
      x elapsedTime
```

So the update method in the Camera object will be :

```java
class Camera {
    //...  
    public GameObject update(double elapsed) {
        x += target.x - ((viewport.getWidth() - target.width)) * 0.5 * tween * elapsed;
        y += target.y - ((viewport.getHeight() - target.height)) * 0.5 * tween * elapsed;
    }
    //...
}
```

### The App with an active Camera

The class `App` must be enhanced with a new `Camera` attribute name `activeCamera`, and initialize it during the
`App#create()` method processing:

```java
class App implements Game {
    //...
    private Camera activeCamera;

    //...
    @Override
    public void create() {
        //...
        setActiveCamera((Camera) new Camera("cam01")
                .setTarget(player)
                .setTween(0.2)
                .setViewport(new Rectangle2D.Double(
                        0.0, 0.0,
                        ((double) screenWidth), ((double) screenHeight))));
    }

    //...
    public void setActiveCamera(Camera activeCamera) {
        this.activeCamera = activeCamera;
        this.renderer.setCurrentCamera(activeCamera);
    }
    //...

}
```

### Add a current Camera to Renderer

The class `Renderer` now need to use a current `Camera` instance to move the rendering viewport to the `currentCamera`
position, and apply this move on concerned `GameObject`.

```java
class Renderer {
    //..
    private Camera currentCamera;

    public void draw(Map<String, Object> attributes) {
        Graphics2D g = (Graphics2D) buffer.getGraphics();
        //...
        // draw all the things you need.
        game.getEntityManager().getEntities()
                .stream()
                .sorted((o1, o2) -> o1.getLayer() > o2.getLayer() ? 1 : (o1.getPriority() > o1.getPriority() ? 1 : -1))
                .forEach(e -> {
                    // move to Camera viewport if object not stick to Camera
                    if (Optional.ofNullable(currentCamera).isPresent() && !e.isStickToCamera()) {
                        g.translate(-currentCamera.x, -currentCamera.y);
                    }
                    // draw object
                    drawEntity(g, e);
                    // move back from Camera viewport if object not stick to Camera
                    if (Optional.ofNullable(currentCamera).isPresent() && !e.isStickToCamera()) {
                        g.translate(-currentCamera.x, currentCamera.y);
                    }

                });
        // draw entity's display debug information
        if (game.getDebugMode() > 0) {
            game.getEntityManager().getEntities()
                    .stream()
                    .sorted((o1, o2) -> o1.getLayer() > o2.getLayer() ? 1 : (o1.getPriority() > o1.getPriority() ? 1 : -1))
                    .forEach(e -> {
                        // move to Camera viewport if object not stick to Camera
                        if (Optional.ofNullable(currentCamera).isPresent() && !e.isStickToCamera()) {
                            g.translate(-currentCamera.x, -currentCamera.y);
                        }
                        // draw Debug information for the Entity. 
                        drawDebugInformation(g, e);
                        // move back from Camera viewport if object not stick to Camera
                        if (Optional.ofNullable(currentCamera).isPresent() && !e.isStickToCamera()) {
                            g.translate(-currentCamera.x, currentCamera.y);
                        }
                    });
            // draw some debug information.
            drawDisplayDebugLine(g, attributes);
        }

        // release Graphics API
        g.dispose();
    }

    //...
    public void setCurrentCamera(Camera currentCamera) {
        this.currentCamera = currentCamera;
    }
}
```

## Conclusion

With this new chapter, we implemented a `Camera` object that tracks a `GameObject` with a certain delay in the following
moves.  
According to our project, you will find this corresponding code with the
tag [create-camera](https://github.com/SnapGames/game101/releases/tag/create-create-camera) in
the [Game101](https://github.com/SnapGames/game101/ "go and visit the corresponding Game101 project") GitHub repository

That's all folk !

McG.