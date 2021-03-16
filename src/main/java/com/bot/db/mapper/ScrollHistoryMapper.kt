package com.bot.db.mapper

import com.bot.db.entities.ScrollHistoryEntity
import com.bot.models.Scroll
import com.bot.models.ScrollHistory
import java.util.*

class ScrollHistoryMapper {
    companion object {
        fun map(entity: ScrollHistoryEntity) : ScrollHistory {
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
            return ScrollHistory(entity.id, entity.user, map, entity.created)
        }

        fun map(history: ScrollHistory) : ScrollHistoryEntity {
            return ScrollHistoryEntity(
                    history.id,
                    history.user,
                    history.created,
                    history.getScrollCount(Scroll.RED_NOSE),
                    history.getScrollCount(Scroll.AWK_RED_NOSE),
                    history.getScrollCount(Scroll.GIATH),
                    history.getScrollCount(Scroll.AWK_GIATH),
                    history.getScrollCount(Scroll.BHEG),
                    history.getScrollCount(Scroll.AWK_BHEG),
                    history.getScrollCount(Scroll.MOGHULIS),
                    history.getScrollCount(Scroll.AGRAKHAN),
                    history.getScrollCount(Scroll.NARC),
                    history.getScrollCount(Scroll.AWK_NARC),
                    history.getScrollCount(Scroll.RONIN),
                    history.getScrollCount(Scroll.AWK_RONIN),
                    history.getScrollCount(Scroll.DIM_TREE),
                    history.getScrollCount(Scroll.AWK_DIM_TREE),
                    history.getScrollCount(Scroll.MUSKAN),
                    history.getScrollCount(Scroll.AWK_MUSKAN),
                    history.getScrollCount(Scroll.HEXE),
                    history.getScrollCount(Scroll.AWK_HEXE),
                    history.getScrollCount(Scroll.AHIB),
                    history.getScrollCount(Scroll.AWK_AHIB),
                    history.getScrollCount(Scroll.URUGON),
                    history.getScrollCount(Scroll.AWK_URUGON),
                    history.getScrollCount(Scroll.PUTURUM),
                    history.getScrollCount(Scroll.TITIUM),
                    history.getScrollCount(Scroll.AWK_TITIUM),
                    history.getScrollCount(Scroll.ARC),
                    history.getScrollCount(Scroll.CARTIAN),
                    history.getScrollCount(Scroll.PILA_FE),
                    history.getScrollCount(Scroll.VOODOO),
                    history.getScrollCount(Scroll.LEEBUR),
                    history.getScrollCount(Scroll.RIFT_ECHO)
            )
        }
    }
}