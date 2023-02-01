Feature: U100 - the Game has GameEntity

  THe master Game class had GameEntity sub classes and an entities map to manager them.

  @GameEntity @Scene
  Scenario: U101 - I can add an entity to the Game
    Given a Game is instantiated
    And the entities map is empty
    Then I Add a new GameEntity named "player"
    And the entities map size is 1

  @GameEntity @Scene @Renderer
  Scenario: U102 - The GameEntity is rendered by its dedicated RendererPlugin
    Given a Game is instantiated
    And I Add a World with a play area of 400 x 600
    And I Add a new GameEntity named "player"
    Then I render the current Scene
    And the "GameEntity" "player" has been rendered by the "fr.snapgames.game.core.graphics.plugins.GameEntityRenderer" plugin

