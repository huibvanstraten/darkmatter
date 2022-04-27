package com.hvs.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.hvs.darkmatter.DarkMatter
import com.hvs.darkmatter.ecs.component.RemoveComponent
import com.hvs.darkmatter.ecs.component.SetScreenComponent
import com.hvs.darkmatter.ecs.component.setScreenCmp
import com.hvs.darkmatter.screen.Screen
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.log.debug
import ktx.log.error
import ktx.log.logger

class SetScreenSystem(private val game: DarkMatter) :
    IteratingSystem(allOf(SetScreenComponent::class).exclude(RemoveComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val screenCmp = entity.setScreenCmp
        val nextScreenType = screenCmp.screenType

        if (nextScreenType == Screen::class) {
            LOG.error { "Cannot set Screen as a screen" }
            return
        }

//        if (!game.containsScreen(nextScreenType.java)) {
//            // create screen if not yet created
//            LOG.debug { "Screen '${nextScreenType.simpleName}' does not exist yet -> create it" }
//            when (nextScreenType) {
//                InventoryScreen::class -> game.addScreen(InventoryScreen(game, engine, entity))
//                CombatScreen::class -> game.addScreen(CombatScreen(game, engine, entity, entity.interactCmp.lastInteractEntity))
//                ShopScreen::class -> game.addScreen(ShopScreen(game, engine, entity, screenCmp.screenData as Entity))
//                else -> {
//                    LOG.error { "Unsupported screen type '${nextScreenType.simpleName}'" }
//                    return
//                }
//            }
//        } else {
//            // screen already exists -> update parameters if necessary
//            when (nextScreenType) {
//                InventoryScreen::class -> game.getScreen<InventoryScreen>().viewModel.playerEntity = entity
//                CombatScreen::class -> {
//                    with(game.getScreen<CombatScreen>()) {
//                        playerEntity = entity
//                        enemyEntity = entity.interactCmp.lastInteractEntity
//                    }
//                }
//                ShopScreen::class -> game.getScreen<ShopScreen>().viewModel.run {
//                    playerEntity = entity
//                    shopEntity = screenCmp.screenData as Entity
//                }
//            }
//        }

        // change to next screen
        game.setScreen(nextScreenType.java)

        entity.remove(SetScreenComponent::class.java)
    }

    companion object {
        private val LOG = logger<SetScreenSystem>()
    }
}
