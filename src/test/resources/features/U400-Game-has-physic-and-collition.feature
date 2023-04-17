Feature: U400 - the Game has a PhysicEngine and Collision detection

  The master Game support Newton's laws motion computing and can detect 2 GameEntity's collision event, and propose
  a corresponding `CollisionResponseBehavior` interface.

  @PhysicEngine @GameEntity
  Scenario: U401 - A GameEntity is under gravity influence
    Given a Game is instantiated with configuration "/collision-test.properties"
    And A Scene "test" is created
    And A GameEntity "player" with size of 32.0x32.0 is created at 100.0,100.0 in Scene "test"
    And A GameEntity "collide1" with size of 32.0x32.0 is created at 110.0,110.0 in Scene "test"
    Then A CollisionEvent happened between "player" and "collide1"

