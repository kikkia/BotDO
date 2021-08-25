package com.bot.models

import com.bot.db.entities.BDOGuildEntity

data class BdoGuild(val id: Int,
                    val name: String,
                    val warDays: Int?) {
 companion object {
     fun from(bdoGuild: BDOGuildEntity?) : BdoGuild? {
      return if(bdoGuild == null) bdoGuild else BdoGuild(bdoGuild.id, bdoGuild.name, bdoGuild.warDays)
     }
 }
}