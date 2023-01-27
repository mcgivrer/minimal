Feature: U300 - the Game has TextEntity

  The master Game class had a TextEntity.

  @TextEntity @Scene
  Scenario: U301 - I can add TextEntity to the Game
    Given a Game is instantiated
    And I add a TextEntity named "txt01" with text "Hello World"
    Then the TextEntity with name "txt01" exists in the current Scene
    And the TextEntity with name "txt01" has a text value "Hello World"

  Scenario: U302 - The TextEntity is rendered by its dedicated RendererPlugin
    Given a Game is instantiated
    And I Add a World with a play area of 400 x 600
    And I add a TextEntity named "txt01" with text "Hello World"
    Then I render the current Scene
    And the "TextEntity" "txt01" has been rendered by the "fr.snapgames.game.core.graphics.plugins.TextEntityRenderer" plugin

