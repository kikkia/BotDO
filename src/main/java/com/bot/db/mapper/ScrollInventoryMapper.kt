package com.bot.db.mapper

import com.bot.db.entities.ScrollInventoryEntity
import com.bot.models.Scroll
import com.bot.models.ScrollInventory
import java.util.*

class ScrollInventoryMapper {
    companion object {
        fun map(entity: ScrollInventoryEntity) : ScrollInventory {
            var map = EnumMap<Scroll, Int>(Scroll::class.java)
            // Oh BOYYY
            // TODO: Actually think about a better way to do this
            map[Scroll.AGRAKHAN] = entity.agrakhan
            map[Scroll.AWK_AHIB] = entity.aAhib
            map[Scroll.AHIB] = entity.ahib
            map[Scroll.AWK_BHEG] = entity.aBheg
            map[Scroll.BHEG] = entity.bheg
            map[Scroll.RED_NOSE] = entity.redNose
            map[Scroll.AWK_RED_NOSE] = entity.aRedNose
            map[Scroll.GIATH] = entity.giath
            map[Scroll.AWK_GIATH] = entity.aGiath
            map[Scroll.NARC] = entity.narc
            map[Scroll.AWK_NARC] = entity.aNarc
            map[Scroll.MOGHULIS] = entity.moghulis
            map[Scroll.RONIN] = entity.ronin
            map[Scroll.AWK_RONIN] = entity.aRonin
            map[Scroll.DIM_TREE] = entity.dim
            map[Scroll.AWK_DIM_TREE] = entity.aDim
            map[Scroll.MUSKAN] = entity.muskan
            map[Scroll.AWK_MUSKAN] = entity.aMuskan
            map[Scroll.HEXE] = entity.hexe
            map[Scroll.AWK_HEXE] = entity.aHexe
            map[Scroll.URUGON] = entity.urugon
            map[Scroll.AWK_URUGON] = entity.aUrugon
            map[Scroll.PUTURUM] = entity.puturum
            map[Scroll.TITIUM] = entity.titium
            map[Scroll.AWK_TITIUM] = entity.aTitium
            map[Scroll.ARC] = entity.arc
            map[Scroll.CARTIAN] = entity.cartian
            map[Scroll.PILA_FE] = entity.pilaFe
            map[Scroll.VOODOO] = entity.voodoo
            map[Scroll.LEEBUR] = entity.leebur
            map[Scroll.RIFT_ECHO] = entity.riftEcho
            return ScrollInventory(entity.id, entity.user, map)
        }

        fun map(inventory: ScrollInventory) : ScrollInventoryEntity {
            return ScrollInventoryEntity(
                    inventory.id,
                    inventory.user,
                    inventory.getScrollCount(Scroll.RED_NOSE),
                    inventory.getScrollCount(Scroll.AWK_RED_NOSE),
                    inventory.getScrollCount(Scroll.GIATH),
                    inventory.getScrollCount(Scroll.AWK_GIATH),
                    inventory.getScrollCount(Scroll.BHEG),
                    inventory.getScrollCount(Scroll.AWK_BHEG),
                    inventory.getScrollCount(Scroll.MOGHULIS),
                    inventory.getScrollCount(Scroll.AGRAKHAN),
                    inventory.getScrollCount(Scroll.NARC),
                    inventory.getScrollCount(Scroll.AWK_NARC),
                    inventory.getScrollCount(Scroll.RONIN),
                    inventory.getScrollCount(Scroll.AWK_RONIN),
                    inventory.getScrollCount(Scroll.DIM_TREE),
                    inventory.getScrollCount(Scroll.AWK_DIM_TREE),
                    inventory.getScrollCount(Scroll.MUSKAN),
                    inventory.getScrollCount(Scroll.AWK_MUSKAN),
                    inventory.getScrollCount(Scroll.HEXE),
                    inventory.getScrollCount(Scroll.AWK_HEXE),
                    inventory.getScrollCount(Scroll.AHIB),
                    inventory.getScrollCount(Scroll.AWK_AHIB),
                    inventory.getScrollCount(Scroll.URUGON),
                    inventory.getScrollCount(Scroll.AWK_URUGON),
                    inventory.getScrollCount(Scroll.PUTURUM),
                    inventory.getScrollCount(Scroll.TITIUM),
                    inventory.getScrollCount(Scroll.AWK_TITIUM),
                    inventory.getScrollCount(Scroll.ARC),
                    inventory.getScrollCount(Scroll.CARTIAN),
                    inventory.getScrollCount(Scroll.PILA_FE),
                    inventory.getScrollCount(Scroll.VOODOO),
                    inventory.getScrollCount(Scroll.LEEBUR),
                    inventory.getScrollCount(Scroll.RIFT_ECHO)
            )
        }
    }
}