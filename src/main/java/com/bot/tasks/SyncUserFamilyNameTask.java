package com.bot.tasks;

import com.bot.service.UserService;
import com.bot.utils.FamilyNameUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class SyncUserFamilyNameTask extends Thread {

    private Guild guild;
    private UserService userService;

    public SyncUserFamilyNameTask(Guild guild, UserService userService) {
        this.guild = guild;
        this.userService = userService;
    }

    @Override
    public void run() {
        for (Member m : guild.getMembers()) {
            var user = userService.getById(m.getUser().getId());
            if (!user.getFamilyName().equalsIgnoreCase(user.getName())) {
                if (FamilyNameUtils.INSTANCE.shouldChangeName(m.getEffectiveName(), user.getFamilyName()) &&
                        guild.getSelfMember().canInteract(m)) {
                    m.modifyNickname(FamilyNameUtils.INSTANCE.getFamilyInjectedName(m.getEffectiveName(), user.getFamilyName())).queue();
                }
            }
        }
    }
}
