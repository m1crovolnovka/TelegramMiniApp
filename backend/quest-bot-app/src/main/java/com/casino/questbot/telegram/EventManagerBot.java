package com.casino.questbot.telegram;

import com.casino.questbot.entity.ProofType;
import com.casino.questbot.entity.QuestAssignmentEntity;
import com.casino.questbot.entity.QuestTaskEntity;
import com.casino.questbot.entity.TaskStatus;
import com.casino.questbot.entity.UserEntity;
import com.casino.questbot.service.AdminService;
import com.casino.questbot.service.QuestService;
import com.casino.questbot.service.UserService;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class EventManagerBot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(EventManagerBot.class);

    private final String token;
    private final String username;
    private final UserService userService;
    private final QuestService questService;
    private final AdminService adminService;
    private final TelegramFileFetcher fileFetcher;
    private final Map<Long, UserSession> sessions = new ConcurrentHashMap<>();

    public EventManagerBot(
            String token,
            String username,
            UserService userService,
            QuestService questService,
            AdminService adminService,
            TelegramFileFetcher fileFetcher) {
        super(token);
        this.token = token;
        this.username = username;
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
                                            new BotCommand("/start", "Начало"),
                                            new BotCommand("/menu", "Меню"),
                                            new BotCommand("/admin", "Админ")))
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
            sendText(chatId, "Задайте Telegram username в настройках и повторите /start.");
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
        if ("/admin".equalsIgnoreCase(text) || "👑 Админ-панель".equals(text)) {
            if (adminService.isAdmin(from.getId(), tgUsername)) {
                sendAdminMenu(chatId);
            } else {
                sendText(chatId, "Нет прав администратора.");
            }
            return;
        }

        switch (text) {
            case "📷 Отправить QR" -> {
                if (sess.isAwaitingProof()) {
                    sendText(chatId, "Сначала завершите текущее задание или откажитесь.");
                    return;
                }
                sess.setAwaitingQr(true);
                sendText(chatId, "Отправьте фото QR с username участника (например @someuser).");
            }
            case "🎲 Задание со случайным человеком" -> startRandomQuest(me, chatId);
            case "❌ Отказаться от задания" -> cancelAssignment(me, chatId);
            default -> {
                if (sess.isAwaitingNewTaskText()) {
                    sess.setNewTaskText(text);
                    sess.setAwaitingNewTaskText(false);
                    sess.setAwaitingNewTaskReward(true);
                    sendText(chatId, "Укажите награду в коинах (число) за это задание:");
                } else if (sess.isAwaitingNewTaskReward()) {
                    try {
                        long reward = Long.parseLong(text.trim());
                        adminService.addNewTask(sess.getNewTaskText(), reward);
                        sess.setAwaitingNewTaskReward(false);
                        sess.setNewTaskText(null);
                        sendText(chatId, "✅ Квест добавлен. Награда: " + reward + " 🪙");
                    } catch (NumberFormatException e) {
                        sendText(chatId, "Введите число коинов.");
                    }
                } else if (sess.isAwaitingProof()) {
                    sendText(chatId, "Пришлите фото или видео как доказательство.");
                } else {
                    sendText(chatId, "Используйте /menu.");
                }
            }
        }
    }

    private void startRandomQuest(UserEntity me, long chatId) throws TelegramApiException {
        if (questService.findActiveForUser(me).isPresent()) {
            sendText(chatId, "У вас уже есть активное задание.");
            return;
        }
        List<UserEntity> candidates =
                userService.all().stream()
                        .filter(u -> !u.getId().equals(me.getId()))
                        .filter(u -> questService.findActiveForUser(u).isEmpty())
                        .toList();
        if (candidates.isEmpty()) {
            sendText(chatId, "Нет свободных участников.");
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
        sendText(chatId, info);
        sendText(partner.getTelegramChatId(), "Вам назначено задание!\n\n" + info);
    }

    private void cancelAssignment(UserEntity me, long chatId) throws TelegramApiException {
        Optional<QuestAssignmentEntity> cancelled = questService.cancelActiveForUser(me);
        if (cancelled.isEmpty()) {
            sendText(chatId, "Нет активного задания.");
            return;
        }
        UserEntity other = cancelled.get().other(me);
        sendText(chatId, "Задание отменено.");
        sendText(other.getTelegramChatId(), "Партнёр @" + me.getUsername() + " отменил задание.");
    }

    private void handlePhotoQr(Message message, UserEntity me, UserSession sess, long chatId)
            throws Exception {
        PhotoSize best =
                message.getPhoto().stream()
                        .max(Comparator.comparing(PhotoSize::getFileSize))
                        .orElse(message.getPhoto().get(0));
        String decoded = decodeQr(best.getFileId());
        if (decoded == null || decoded.isBlank()) {
            sendText(chatId, "Не удалось распознать QR.");
            return;
        }
        String target = decoded.trim().replace("@", "");
        if (target.equalsIgnoreCase(me.getUsername())) {
            sendText(chatId, "Нельзя с собой.");
            return;
        }
        Optional<UserEntity> partner = userService.findByUsername(target);
        if (partner.isEmpty()) {
            sendText(chatId, "Пользователь @" + target + " ещё не запускал бота.");
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
        sendText(chatId, "Доказательство отправлено. Ожидайте проверки админом.");
        sendText(
                a.other(me).getTelegramChatId(),
                "Партнёр @" + me.getUsername() + " отправил доказательство.");
        if (!a.isAdminNotified()) {
            notifyAdminsAboutPending(a);
            questService.markAdminNotified(a);
        }
    }

    private void notifyAdminsAboutPending(QuestAssignmentEntity a) {
        String text =
                "Задание на проверку #%d\n@%s + @%s\nНаграда: %d 🪙\n%s"
                        .formatted(
                                a.getId(),
                                a.getUserA().getUsername(),
                                a.getUserB().getUsername(),
                                a.getTask().getRewardCoins(),
                                a.getTask().getDescription());
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton approve = new InlineKeyboardButton("✅ Подтвердить");
        approve.setCallbackData("admin:approve:" + a.getId());
        InlineKeyboardButton reject = new InlineKeyboardButton("❌ Отклонить");
        reject.setCallbackData("admin:reject:" + a.getId());
        markup.setKeyboard(List.of(List.of(approve, reject)));

        for (Long adminChatId : userService.adminChatIds()) {
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
        if (!(callback.getMessage() instanceof Message message)) {
            return;
        }
        long chatId = message.getChatId();
        User from = callback.getFrom();
        String data = callback.getData();
        if (!data.startsWith("admin:")) {
            return;
        }
        if (!adminService.isAdmin(from.getId(), from.getUserName())) {
            sendText(chatId, "Нет прав.");
            return;
        }
        String[] parts = data.split(":");
        String action = parts[1];
        if ("start_add_task".equals(action)) {
            session(chatId).setAwaitingNewTaskText(true);
            sendText(chatId, "Введите текст нового задания:");
            clearInlineKeyboard(chatId, message.getMessageId());
            return;
        }
        if ("view_pending".equals(action)) {
            Optional<QuestAssignmentEntity> pending = questService.findFirstPending();
            if (pending.isPresent()) {
                sendText(
                        chatId,
                        "Ожидает #%d: @%s + @%s"
                                .formatted(
                                        pending.get().getId(),
                                        pending.get().getUserA().getUsername(),
                                        pending.get().getUserB().getUsername()));
            } else {
                sendText(chatId, "✅ Все задания проверены.");
            }
            return;
        }
        if ("list_tasks".equals(action)) {
            sendTasksList(chatId, parts.length > 2 ? Integer.parseInt(parts[2]) : 0);
            return;
        }
        if (parts.length < 3) {
            return;
        }
        long assignmentId = Long.parseLong(parts[2]);
        processAdminDecision(action, assignmentId, chatId, message);
    }

    private void processAdminDecision(String action, long assignmentId, long chatId, Message message)
            throws TelegramApiException {
        Optional<QuestAssignmentEntity> opt =
                "approve".equals(action)
                        ? questService.approveAssignment(assignmentId)
                        : questService.reject(assignmentId);
        clearInlineKeyboard(chatId, message.getMessageId());
        if (opt.isEmpty()) {
            execute(
                    EditMessageText.builder()
                            .chatId(chatId)
                            .messageId(message.getMessageId())
                            .text("Задание #" + assignmentId + " уже обработано.")
                            .build());
            return;
        }
        QuestAssignmentEntity a = opt.get();
        boolean approved = a.getStatus() == TaskStatus.APPROVED;
        execute(
                EditMessageText.builder()
                        .chatId(chatId)
                        .messageId(message.getMessageId())
                        .text(
                                approved
                                        ? "✅ Задание #" + assignmentId + " подтверждено."
                                        : "❌ Задание #" + assignmentId + " отклонено.")
                        .build());
        notifyUsersAboutDecision(a, approved);
    }

    private void notifyUsersAboutDecision(QuestAssignmentEntity a, boolean approved) {
        String text =
                approved
                        ? "🎉 Задание одобрено! +" + a.getTask().getRewardCoins() + " 🪙"
                        : "Задание отклонено.";
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
            sendText(chatId, "Список квестов пуст.");
            return;
        }
        StringBuilder sb = new StringBuilder("📋 Квесты (стр. ").append(page + 1).append(")\n\n");
        for (QuestTaskEntity task : taskPage) {
            sb.append("#").append(task.getId()).append(" — ").append(task.getRewardCoins()).append(" 🪙\n");
            sb.append(task.getDescription()).append("\n\n");
        }
        sendText(chatId, sb.toString());
    }

    private void sendAdminMenu(long chatId) throws TelegramApiException {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(
                List.of(
                        List.of(
                                btn(
                                        "🔍 Очередь ("
                                                + questService.countByStatus(
                                                        TaskStatus.COMPLETED_PENDING_REVIEW)
                                                + ")",
                                        "admin:view_pending"),
                                btn("➕ Добавить квест", "admin:start_add_task")),
                        List.of(btn("📋 Список квестов", "admin:list_tasks:0"))));
        execute(
                SendMessage.builder()
                        .chatId(chatId)
                        .text("Админ-панель квестов")
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
                        .text(
                                """
                                Привет! Квест-бот Casino.

                                📷 QR — задание с другом по его username
                                🎲 Случайный партнёр
                                После выполнения — фото/видео доказательство
                                Админ подтверждает, коины и карточки начисляются в Casino.""")
                        .replyMarkup(mainMenu(isAdmin))
                        .build());
    }

    private void sendMainMenu(long chatId, boolean isAdmin) throws TelegramApiException {
        execute(
                SendMessage.builder()
                        .chatId(chatId)
                        .text("Меню:")
                        .replyMarkup(mainMenu(isAdmin))
                        .build());
    }

    private ReplyKeyboardMarkup mainMenu(boolean isAdmin) {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow r1 = new KeyboardRow();
        r1.add(new KeyboardButton("📷 Отправить QR"));
        r1.add(new KeyboardButton("🎲 Задание со случайным человеком"));
        rows.add(r1);
        KeyboardRow r2 = new KeyboardRow();
        r2.add(new KeyboardButton("❌ Отказаться от задания"));
        rows.add(r2);
        if (isAdmin) {
            KeyboardRow admin = new KeyboardRow();
            admin.add(new KeyboardButton("👑 Админ-панель"));
            rows.add(admin);
        }
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(rows);
        markup.setResizeKeyboard(true);
        return markup;
    }

    private String questText(QuestAssignmentEntity a) {
        return "Задание:\n" + a.getTask().getDescription() + "\n\nНаграда: " + a.getTask().getRewardCoins() + " 🪙";
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
