Feature: U500 - the Game has a PhysicEngine and Collision detection

  The master Game support Newton's laws motion computing and can detect 2 GameEntity's collision event, and propose
  a corresponding `CollisionResponseBehavior` interface.

  @Animations @Animation
  Scenario: U501 - An Animations object is loaded with Animation frames
    Given a Game is instantiated with configuration "/test-animations.properties"
    And A Scene "anim" is created
    And An Animations is loaded with "/my-animations-sets.properties" from scene "anim"
    And A GameEntity "player" with size of 32.0x32.0 is created at 100.0,100.0 in Scene "test"
    And The GameEntity "player" is set with currentAnimation to "player_walk"
    Then The GameEntity "player" has animation of 8 frames

#  Scenario: U502 - An Animations object is loaded with Animation frames
#    Given a Game is instantiated with configuration "/animations-test.properties"
#    And A Scene "test" is created
#    And A GameEntity "player" with size of 32.0x32.0 is created at 100.0,100.0 in Scene "test"
#    And A GameEntity "collide1" with size of 32.0x32.0 is created at 110.0,110.0 in Scene "test"
#    Then A CollisionEvent happened between "player" and "collide1"
