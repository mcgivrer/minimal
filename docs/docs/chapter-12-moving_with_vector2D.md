# Moving to Vector2D

_(TO BE REVIEWED)_

## Goals

We already had developed a fantastic `PhysicEngine` class, supporting some Newton's laws, and we rely on the `Entity`
attributes `(x,y)`, `(dx,dy)` and `(ax, ay)` for respectively position, velocity and acceleration, but also Material
attributes and the mass of this entity

We also used the `World` object as `PhysicEngine` world's limited context with the play area, the `gravity` and a clear
identified `Material` to define physic attributes of the play area where the `Entity` will move on.

It is now time to use some Math to compute things with a Vector2D class.

## The Vector2D

A `Vector2D` is math entity used to define a force on a 2D world. It basically contains 2 attributes: `x` and `y`.

![The Vector2D class diagram](http://www.plantuml.com/plantuml/png/TS-nJWCn30RWFKznR8VO63jrk3T0OlV5Lch9TahYL6qHxmw5eZQ8RB_ovK_iRNKetbJ2W-z8QTeBi8KeKElqIj5pULxUm_Gq7JUOsofqoQUx38ZpONEzYe-_QVasIq93ZSHomwL7v0CZpNZzwIzM1uiqRcVlx3OklJMGsk6Qin3OHOKi-Mw-BoWSMVbCi_uxYZPntijfm4O9dBe7BHS5uND_MjjQlK3qqKwUNxomfQ_MJmWVOKk6JUKJ "The Vector2D class diagram")

This new class must be used to define our Entity position, velocity and acceleration :

```java
public class Entity<T>{
    public Vector2D position;
    public Vector2D velocity;
    public Vector2D acceleration;
}
```

With this introduction a lot of changes in the global design, thourght multiple systems are impacted: Renderer, PhysicEngine are the most impacted.

let's dive into those 2 big pals.

## PhysicEngine with Vector2D



## Renderer with Vector2D

