# TCrafting

## Overview
The main class is the ``TRecipeManager``. It holds a ``Map<Identifier, TRecipe>``, where ``TRecipe`` is the base class for recipes. In ``TRecipe`` are:
* ``recipeType``: Recipe type determines how the recipe will be exposed to the player. A lot of vanilla blocks have been registered as recipe types, but note that recipe types will not always correlate with blocks.
* ``result``: The result of the recipe.
* ``reagents``: Map of reagents to integers, which represents a list of ingredients and how many are required.

The ``Reagent`` class represents an ingredient, which can be fulfilled using multiple item stacks. This can be seen in the ``matchingStacks`` field, which holds the wrapper class ``ComparableItemStack``.

On a higher level, recipes are exposed to players via nearby blocks that are registered as recipe interfaces. Recipe interfaces (see ``TRecipeInterfaceRegistry``) map block identifiers to a ``TRecipeInterface`` that holds recipe type identifiers. Recipe types (see ``TRecipeTypeRegistry``) are identifiers used to expose recipes. For example, a recipe of type ``"minecraft:anvil"`` will only be available to the player if they are standing near a block that is registered as a recipe interface that also contains ``"minecraft:anvil"``.

## Adding Your Own Recipes
### Dynamic Recipes
Dynamic recipes are recipes that are generated every refresh of the crafting window and can use contextual information such as the player and their inventory to determine what recipes are available. Creating your own is as easy as implementing ``DynamicRecipe`` and registering it in ``DynamicRecipeRegistry``.

### Static Recipes
Any technical JSON files in the "tcrafting_recipes" data folder (ie data/mod_id/tcrafting_recipes) will be loaded directly into the TCrafting recipe manager. Here is an example of a custom recipe:
```
{
  "type": "minecraft:fletching_table",
  "recipe": {
    "output": {
      "item": "minecraft:diamond",
      "count": 3
    },
    "reagents": [
      {
        "item": "minecraft:dirt"
      }
    ]
  }
}
```
Note that the ``count`` property is optional (defaults to 1).

A couple reason to to register recipes as TCrafting recipes instead of vanilla recipes:
* Set a custom recipe type
* Bypass the item count restrictions imposed by a 3x3 grid

Other than that, feel free to continue using vanilla recipes.