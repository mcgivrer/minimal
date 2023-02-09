Feature: U200 - the Game has Camera

  The master Game class had a Camera sub classe.

  Scenario: U201 - I can add Camera to the Game
    Given a Game is instantiated
    And no Camera is set
    And I add a Camera named "cam01"
    Then the current Camera is not null
    And the current Camera name is "cam01"

  Scenario: U202 - I can target a GameEntity with a Camera
    Given a Game is instantiated
    And I add a new GameEntity named "player" at 160.0,100.0
    And I add a Camera named "cam01" with tween at 0.05
    And I set Camera "cam01" viewport as 100,100
    And the GameEntity "player" is stick to camera
    And I set Camera "cam01" target as GameEntity "player"
    Then I update 10 times the Game of 16 ms steps
    And the current Camera "cam01" name is centered on "player"

