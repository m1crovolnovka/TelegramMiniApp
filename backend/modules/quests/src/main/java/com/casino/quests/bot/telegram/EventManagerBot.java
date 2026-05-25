package com.casino.quests.bot.telegram;

import com.casino.quests.bot.BotMessages;
import com.casino.quests.bot.entity.ProofType;
import com.casino.quests.bot.entity.QuestAssignmentEntity;
import com.casino.quests.bot.entity.QuestTaskEntity;
import com.casino.quests.bot.entity.TaskStatus;
import com.casino.quests.bot.entity.UserEntity;
import com.casino.quests.bot.service.AdminService;
import com.casino.quests.bot.service.BotUserService;
import com.casino.quests.bot.service.QuestService;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class EventManagerBot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(EventManagerBot.class);

    private final String token;
    private final String username;
    private final String miniAppUrl;
    private final BotUserService userService;
    private final QuestService questService;
    private final AdminService adminService;
    private final TelegramFileFetcher fileFetcher;
    private final Map<Long, UserSession> sessions = new ConcurrentHashMap<>();

    public EventManagerBot(
            String token,
            String username,
            String miniAppUrl,
            BotUserService userService,
            QuestService questService,
            AdminService adminService,
            TelegramFileFetcher fileFetcher) {
        super(token);
        this.token = token;
        this.username = username;
        this.miniAppUrl = miniAppUrl != null ? miniAppUrl : "";
        this.userService = userService;
        this.questService = questService;
        this.adminService = adminService;
        this.fileFetcher = fileFetcher;
        registerCommands();
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    private UserSession session(long chatId) {
        return sessions.computeIfAbsent(chatId, id -> new UserSession());
    }

    private void registerCommands() {
        try {
            execute(
                    SetMyCommands.builder()
                            .commands(
                                    List.of(
                                            new BotCommand("/start", BotMessages.CMD_START_DESC),
                                            new BotCommand("/menu", BotMessages.CMD_MENU_DESC),
                                            new BotCommand("/admin", BotMessages.CMD_ADMIN_DESC)))
                            .build());
        } catch (TelegramApiException e) {
            log.warn("Failed to set commands", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                handleMessage(update.getMessage());
            } else if (update.hasCallbackQuery()) {
                handleCallback(update.getCallbackQuery());
            }
        } catch (Exception e) {
            log.error("Update error", e);
        }
    }

    private void handleMessage(Message message) throws Exception {
        if (!message.hasText() && !message.hasPhoto() && !message.hasVideo() && !message.hasVideoNote()) {
            return;
        }
        long chatId = message.getChatId();
        User from = message.getFrom();
        String tgUsername = from.getUserName();
        if (tgUsername == null || tgUsername.isBlank()) {
            sendText(chatId, BotMessages.NEED_USERNAME);
            return;
        }

        UserEntity me = userService.getOrCreateByTelegram(tgUsername, chatId);
        UserSession sess = session(chatId);
        sess.setAwaitingProof(questService.findActiveForUser(me).isPresent());

        if (message.hasText()) {
            handleTextMessage(message, me, sess, chatId, from, tgUsername);
            return;
        }

        if (message.hasPhoto() && sess.isAwaitingQr() && !sess.isAwaitingProof()) {
            handlePhotoQr(message, me, sess, chatId);
            return;
        }

        if (message.hasPhoto() || message.hasVideo() || message.hasVideoNote()) {
            handleProofMedia(message, me, chatId);
        }
    }

    private void handleTextMessage(
            Message message, UserEntity me, UserSession sess, long chatId, User from, String tgUsername)
            throws Exception {
        String text = message.getText();
        if (text.startsWith("/start")) {
            sess.clearPending();
            sendWelcome(chatId, adminService.isAdmin(from.getId(), tgUsername));
            return;
        }
        if ("/menu".equalsIgnoreCase(text)) {
            sendMainMenu(chatId, adminService.isAdmin(from.getId(), tgUsername));
            return;
        }
        if ("/admin".equalsIgnoreCase(text) || BotMessages.BTN_ADMIN.equals(text)) {
            if (adminService.isAdmin(from.getId(), tgUsername)) {
                sendAdminMenu(chatId);
            } else {
                sendText(chatId, BotMessages.NO_ADMIN_RIGHTS);
            }
            return;
        }

        switch (text) {
            case BotMessages.BTN_QR -> {
                if (sess.isAwaitingProof()) {
                    sendText(chatId, BotMessages.FINISH_OR_CANCEL_FIRST);
                    return;
                }
                sess.setAwaitingQr(true);
                sendText(chatId, BotMessages.SEND_QR_PHOTO);
            }
            case BotMessages.BTN_RANDOM -> startRandomQuest(me, chatId);
            case BotMessages.BTN_CANCEL -> cancelAssignment(me, chatId);
            default -> {
                if (sess.isAwaitingNewTaskText()) {
                    sess.setNewTaskText(text);
                    sess.setAwaitingNewTaskText(false);
                    sess.setAwaitingNewTaskReward(true);
                    sendText(chatId, BotMessages.ENTER_REWARD_COINS);
                } else if (sess.isAwaitingNewTaskReward()) {
                    try {
                        long reward = Long.parseLong(text.trim());
                        adminService.addNewTask(sess.getNewTaskText(), reward);
                        sess.setAwaitingNewTaskReward(false);
                        sess.setNewTaskText(null);
                        sendText(chatId, BotMessages.QUEST_ADDED.formatted(reward));
                    } catch (NumberFormatException e) {
                        sendText(chatId, BotMessages.ENTER_NUMBER);
                    }
                } else if (sess.isAwaitingProof()) {
                    sendText(chatId, BotMessages.SEND_PROOF_MEDIA);
                } else {
                    sendText(chatId, BotMessages.USE_MENU);
                }
            }
        }
    }

    private void startRandomQuest(UserEntity me, long chatId) throws TelegramApiException {
        if (questService.findActiveForUser(me).isPresent()) {
            sendText(chatId, BotMessages.ALREADY_HAS_QUEST);
            return;
        }
        List<UserEntity> candidates =
                userService.all().stream()
                        .filter(u -> !u.getId().equals(me.getId()))
                        .filter(u -> questService.findActiveForUser(u).isEmpty())
                        .toList();
        if (candidates.isEmpty()) {
            sendText(chatId, BotMessages.NO_FREE_PARTNERS);
            return;
        }
        UserEntity partner = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        createQuestForPartner(me, partner, chatId);
    }

    private void createQuestForPartner(UserEntity me, UserEntity partner, long chatId)
            throws TelegramApiException {
        QuestService.CreateQuestResult res = questService.createQuest(me, partner);
        if (!res.ok()) {
            sendText(chatId, res.error());
            return;
        }
        QuestAssignmentEntity a = res.assignment();
        String info = questText(a);
        sendText(chatId, BotMessages.QUEST_WITH_PARTNER.formatted(partner.getUsername(), info));
        sendText(
                partner.getTelegramChatId(),
                BotMessages.PARTNER_ASSIGNED_DETAILED.formatted(
                        me.getUsername(), partner.getUsername(), info));
    }

    private void cancelAssignment(UserEntity me, long chatId) throws TelegramApiException {
        Optional<QuestAssignmentEntity> cancelled = questService.cancelActiveForUser(me);
        if (cancelled.isEmpty()) {
            sendText(chatId, BotMessages.NO_ACTIVE_QUEST);
            return;
        }
        UserEntity other = cancelled.get().other(me);
        sendText(chatId, BotMessages.QUEST_CANCELLED);
        sendText(other.getTelegramChatId(), BotMessages.PARTNER_CANCELLED.formatted(me.getUsername()));
    }

    private void handlePhotoQr(Message message, UserEntity me, UserSession sess, long chatId)
            throws Exception {
        PhotoSize best =
                message.getPhoto().stream()
                        .max(Comparator.comparing(PhotoSize::getFileSize))
                        .orElse(message.getPhoto().get(0));
        String decoded = decodeQr(best.getFileId());
        if (decoded == null || decoded.isBlank()) {
            sendText(chatId, BotMessages.QR_DECODE_FAIL);
            return;
        }
        String target = decoded.trim().replace("@", "");
        if (target.equalsIgnoreCase(me.getUsername())) {
            sendText(chatId, BotMessages.CANNOT_WITH_SELF);
            return;
        }
        Optional<UserEntity> partner = userService.findByUsername(target);
        if (partner.isEmpty()) {
            sendText(chatId, BotMessages.PARTNER_NOT_STARTED.formatted(target));
            return;
        }
        sess.setAwaitingQr(false);
        createQuestForPartner(me, partner.get(), chatId);
    }

    private void handleProofMedia(Message message, UserEntity me, long chatId) throws TelegramApiException {
        String fileId;
        ProofType type;
        if (message.hasVideo()) {
            fileId = message.getVideo().getFileId();
            type = ProofType.VIDEO;
        } else if (message.hasVideoNote()) {
            fileId = message.getVideoNote().getFileId();
            type = ProofType.VIDEO_NOTE;
        } else if (message.hasPhoto()) {
            fileId =
                    message.getPhoto().stream()
                            .max(Comparator.comparing(PhotoSize::getFileSize))
                            .orElse(message.getPhoto().get(0))
                            .getFileId();
            type = ProofType.PHOTO;
        } else {
            return;
        }

        QuestService.CompleteProofResult res = questService.submitProof(me, fileId, type);
        if (!res.ok()) {
            sendText(chatId, res.error());
            return;
        }
        QuestAssignmentEntity a = res.assignment();
        UserEntity other = a.other(me);
        sendText(chatId, BotMessages.PROOF_SENT);
        sendText(other.getTelegramChatId(), BotMessages.PARTNER_SENT_PROOF.formatted(me.getUsername()));
        sendText(other.getTelegramChatId(), BotMessages.PARTNER_PROOF_FORWARD.formatted(me.getUsername()));
        sendProofMedia(other.getTelegramChatId(), a);
        if (!a.isAdminNotified()) {
            notifyAdminsAboutPending(a);
            questService.markAdminNotified(a);
        }
    }

    private void sendPendingReviewToAdmin(long adminChatId, QuestAssignmentEntity a) {
        String text =
                BotMessages.PENDING_REVIEW.formatted(
                        a.getId(),
                        a.getUserA().getUsername(),
                        a.getUserB().getUsername(),
                        a.getTask().getRewardCoins(),
                        a.getTask().getDescription());
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton approve = new InlineKeyboardButton(BotMessages.BTN_APPROVE);
        approve.setCallbackData("admin:approve:" + a.getId());
        InlineKeyboardButton reject = new InlineKeyboardButton(BotMessages.BTN_REJECT);
        reject.setCallbackData("admin:reject:" + a.getId());
        markup.setKeyboard(List.of(List.of(approve, reject)));
        try {
            execute(
                    SendMessage.builder()
                            .chatId(adminChatId)
                            .text(text)
                            .replyMarkup(markup)
                            .build());
            sendProofMedia(adminChatId, a);
        } catch (TelegramApiException e) {
            log.warn("Notify admin {}", adminChatId, e);
        }
    }

    private void notifyAdminsAboutPending(QuestAssignmentEntity a) {
        for (Long adminChatId : userService.adminChatIds()) {
            sendPendingReviewToAdmin(adminChatId, a);
        }
    }

    private void sendProofMedia(long chatId, QuestAssignmentEntity a) throws TelegramApiException {
        if (a.getProofFileId() == null || a.getProofType() == null) {
            return;
        }
        switch (a.getProofType()) {
            case PHOTO -> execute(
                    SendPhoto.builder().chatId(chatId).photo(new InputFile(a.getProofFileId())).build());
            case VIDEO -> execute(
                    SendVideo.builder().chatId(chatId).video(new InputFile(a.getProofFileId())).build());
            case VIDEO_NOTE -> execute(
                    SendVideoNote.builder()
                            .chatId(chatId)
                            .videoNote(new InputFile(a.getProofFileId()))
                            .build());
        }
    }

    private void handleCallback(CallbackQuery callback) throws TelegramApiException {
        String callbackId = callback.getId();
        if (!(callback.getMessage() instanceof Message message)) {
            answerCallback(callbackId, null);
            return;
        }
        long chatId = message.getChatId();
        User from = callback.getFrom();
        String data = callback.getData();
        if (data == null || !data.startsWith("admin:")) {
            answerCallback(callbackId, null);
            return;
        }
        if (!adminService.isAdmin(from.getId(), from.getUserName())) {
            answerCallback(callbackId, BotMessages.NO_RIGHTS);
            sendText(chatId, BotMessages.NO_RIGHTS);
            return;
        }
        String[] parts = data.split(":");
        String action = parts[1];
        try {
            if ("start_add_task".equals(action)) {
                session(chatId).setAwaitingNewTaskText(true);
                sendText(chatId, BotMessages.ENTER_NEW_TASK);
                clearInlineKeyboard(chatId, message.getMessageId());
                answerCallback(callbackId, null);
                return;
            }
            if ("view_pending".equals(action)) {
                Optional<QuestAssignmentEntity> pending = questService.findFirstPending();
                if (pending.isPresent()) {
                    sendPendingReviewToAdmin(chatId, pending.get());
                } else {
                    sendText(chatId, BotMessages.ALL_REVIEWED);
                }
                answerCallback(callbackId, null);
                return;
            }
            if ("list_tasks".equals(action)) {
                sendTasksList(chatId, parts.length > 2 ? Integer.parseInt(parts[2]) : 0);
                answerCallback(callbackId, null);
                return;
            }
            if (parts.length < 3) {
                answerCallback(callbackId, null);
                return;
            }
            long assignmentId = Long.parseLong(parts[2]);
            processAdminDecision(action, assignmentId, chatId, message, callbackId);
        } catch (Exception e) {
            log.error("Admin callback failed: {}", data, e);
            answerCallback(callbackId, "Ошибка: " + e.getMessage());
        }
    }

    private void processAdminDecision(
            String action, long assignmentId, long chatId, Message message, String callbackId)
            throws TelegramApiException {
        Optional<QuestAssignmentEntity> opt =
                "approve".equals(action)
                        ? questService.approveAssignment(assignmentId)
                        : questService.reject(assignmentId);
        clearInlineKeyboard(chatId, message.getMessageId());
        if (opt.isEmpty()) {
            String msg = BotMessages.ALREADY_HANDLED.formatted(assignmentId);
            execute(
                    EditMessageText.builder()
                            .chatId(chatId)
                            .messageId(message.getMessageId())
                            .text(msg)
                            .build());
            answerCallback(callbackId, msg);
            return;
        }
        QuestAssignmentEntity a = opt.get();
        boolean approved = a.getStatus() == TaskStatus.APPROVED;
        String msg =
                approved
                        ? BotMessages.APPROVED.formatted(assignmentId)
                        : BotMessages.REJECTED.formatted(assignmentId);
        execute(
                EditMessageText.builder()
                        .chatId(chatId)
                        .messageId(message.getMessageId())
                        .text(msg)
                        .build());
        answerCallback(callbackId, approved ? "Подтверждено" : "Отклонено");
        notifyUsersAboutDecision(a, approved);
    }

    private void answerCallback(String callbackQueryId, String text) {
        try {
            var builder = AnswerCallbackQuery.builder().callbackQueryId(callbackQueryId);
            if (text != null && !text.isBlank()) {
                builder.text(text.length() > 200 ? text.substring(0, 200) : text);
            }
            execute(builder.build());
        } catch (TelegramApiException e) {
            log.warn("answerCallback failed", e);
        }
    }

    private void notifyUsersAboutDecision(QuestAssignmentEntity a, boolean approved) {
        String text =
                approved
                        ? BotMessages.QUEST_APPROVED_REWARD.formatted(a.getTask().getRewardCoins())
                        : BotMessages.QUEST_REJECTED;
        for (UserEntity u : List.of(a.getUserA(), a.getUserB())) {
            try {
                sendText(u.getTelegramChatId(), text);
            } catch (TelegramApiException e) {
                log.warn("Notify {}", u.getUsername(), e);
            }
        }
    }

    private void sendTasksList(long chatId, int page) throws TelegramApiException {
        Page<QuestTaskEntity> taskPage = adminService.getTasksPage(page, 10);
        if (taskPage.isEmpty()) {
            sendText(chatId, BotMessages.QUEST_LIST_EMPTY);
            return;
        }
        StringBuilder sb = new StringBuilder(BotMessages.QUEST_LIST_HEADER.formatted(page + 1));
        for (QuestTaskEntity task : taskPage) {
            sb.append(
                    BotMessages.QUEST_LIST_ITEM.formatted(
                            task.getId(), task.getRewardCoins(), task.getDescription()));
        }
        sendText(chatId, sb.toString());
    }

    private void sendAdminMenu(long chatId) throws TelegramApiException {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(
                List.of(
                        List.of(
                                btn(
                                        BotMessages.BTN_QUEUE.formatted(
                                                questService.countByStatus(
                                                        TaskStatus.COMPLETED_PENDING_REVIEW)),
                                        "admin:view_pending"),
                                btn(BotMessages.BTN_ADD_QUEST, "admin:start_add_task")),
                        List.of(btn(BotMessages.BTN_LIST_QUESTS, "admin:list_tasks:0"))));
        execute(
                SendMessage.builder()
                        .chatId(chatId)
                        .text(BotMessages.ADMIN_PANEL_TITLE)
                        .replyMarkup(markup)
                        .build());
    }

    private InlineKeyboardButton btn(String text, String data) {
        InlineKeyboardButton b = new InlineKeyboardButton(text);
        b.setCallbackData(data);
        return b;
    }

    private void sendWelcome(long chatId, boolean isAdmin) throws TelegramApiException {
        execute(
                SendMessage.builder()
                        .chatId(chatId)
                        .text(BotMessages.WELCOME)
                        .replyMarkup(mainMenu(isAdmin))
                        .build());
    }

    private void sendMainMenu(long chatId, boolean isAdmin) throws TelegramApiException {
        execute(
                SendMessage.builder()
                        .chatId(chatId)
                        .text(BotMessages.MENU_TITLE)
                        .replyMarkup(mainMenu(isAdmin))
                        .build());
    }

    private ReplyKeyboardMarkup mainMenu(boolean isAdmin) {
        List<KeyboardRow> rows = new ArrayList<>();
        if (!miniAppUrl.isBlank()) {
            KeyboardRow webAppRow = new KeyboardRow();
            KeyboardButton casinoBtn = new KeyboardButton(BotMessages.BTN_OPEN_CASINO);
            casinoBtn.setWebApp(new WebAppInfo(miniAppUrl));
            webAppRow.add(casinoBtn);
            rows.add(webAppRow);
        }
        KeyboardRow r1 = new KeyboardRow();
        r1.add(new KeyboardButton(BotMessages.BTN_QR));
        r1.add(new KeyboardButton(BotMessages.BTN_RANDOM));
        rows.add(r1);
        KeyboardRow r2 = new KeyboardRow();
        r2.add(new KeyboardButton(BotMessages.BTN_CANCEL));
        rows.add(r2);
        if (isAdmin) {
            KeyboardRow admin = new KeyboardRow();
            admin.add(new KeyboardButton(BotMessages.BTN_ADMIN));
            rows.add(admin);
        }
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(rows);
        markup.setResizeKeyboard(true);
        return markup;
    }

    private String questText(QuestAssignmentEntity a) {
        return BotMessages.questInfo(a.getTask().getDescription(), a.getTask().getRewardCoins());
    }

    private String decodeQr(String fileId) {
        try {
            URL url = fileFetcher.getFileUrl(this, fileId);
            try (InputStream is = url.openStream()) {
                BufferedImage image = ImageIO.read(is);
                if (image == null) {
                    return null;
                }
                BinaryBitmap bitmap =
                        new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
                Result result = new MultiFormatReader().decode(bitmap);
                return result.getText();
            }
        } catch (Exception e) {
            log.warn("QR decode failed", e);
            return null;
        }
    }

    private void clearInlineKeyboard(long chatId, int messageId) {
        try {
            execute(
                    EditMessageReplyMarkup.builder()
                            .chatId(chatId)
                            .messageId(messageId)
                            .replyMarkup(null)
                            .build());
        } catch (TelegramApiException e) {
            log.debug("clear keyboard", e);
        }
    }

    private void sendText(long chatId, String text) throws TelegramApiException {
        execute(SendMessage.builder().chatId(chatId).text(text).build());
    }
}
