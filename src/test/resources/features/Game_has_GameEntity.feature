Feature: U100 - the Game has GameEntity

  THe master Game class had GameEntity sub classes and an entities map to manager them.

  Scenario: U101 - I can add an entity to the Game
    Given a Game is instantiated
    And the entities map is empty
    Then I Add a new GameEntity named "player"
    And the entities map size is 1

