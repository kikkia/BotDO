package com.bot.tasks;

import com.bot.service.UserService;
import com.bot.utils.FamilyNameUtils;
import net.dv8tion.jda.api.entities.Member;

public class SyncUserFamilyNameTask extends Thread {

    private Member member;
    private UserService userService;

    public SyncUserFamilyNameTask(Member member, UserService userService) {
        this.member = member;
        this.userService = userService;
    }

    @Override
    public void run() {
        var user = userService.getById(member.getUser().getId());
        if (user.getFamilyName() != null) {
            if (FamilyNameUtils.INSTANCE.shouldChangeName(member.getEffectiveName(), user.getFamilyName()) &&
                    member.getGuild().getSelfMember().canInteract(member)) {
                member.modifyNickname(
                        FamilyNameUtils.INSTANCE.getFamilyInjectedName(
                                member.getEffectiveName(), user.getFamilyName()))
                        .queue();
            }
        }
    }
}
