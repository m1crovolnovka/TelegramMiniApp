package com.casino.quests.bot.telegram;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSession {

    private boolean awaitingQr;
    private String pendingPartnerUsername;
    private boolean awaitingProof;
    private boolean awaitingNewTaskText;
    private boolean awaitingNewTaskReward;
    private String newTaskText;

    public void clearPending() {
        awaitingQr = false;
        pendingPartnerUsername = null;
    }
}

