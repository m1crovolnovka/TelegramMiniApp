package com.casino.quests.bot;

public final class BotMessages {

    private BotMessages() {}

    public static final String BTN_OPEN_CASINO = "🎰 Открыть Casino";
    public static final String BTN_QR = "📷 Отправить QR";
    public static final String BTN_RANDOM = "🎲 Задание со случайным человеком";
    public static final String BTN_CANCEL = "❌ Отказаться от задания";
    public static final String BTN_ADMIN = "👑 Админ-панель";

    public static final String CMD_START_DESC = "Начало";
    public static final String CMD_MENU_DESC = "Меню";
    public static final String CMD_ADMIN_DESC = "Админ";

    public static final String NEED_USERNAME =
            "Задайте Telegram username в настройках и повторите /start.";
    public static final String NO_ADMIN_RIGHTS = "Нет прав администратора.";
    public static final String FINISH_OR_CANCEL_FIRST =
            "Сначала завершите текущее задание или откажитесь.";
    public static final String SEND_QR_PHOTO =
            "Отправьте фото QR с username участника (например @someuser).";
    public static final String ENTER_REWARD_COINS =
            "Укажите награду в коинах (число) за это задание:";
    public static final String QUEST_ADDED = "✅ Квест добавлен. Награда: %d 🪙";
    public static final String ENTER_NUMBER = "Введите число коинов.";
    public static final String SEND_PROOF_MEDIA =
            "Пришлите фото или видео как доказательство.";
    public static final String USE_MENU = "Используйте /menu.";
    public static final String ALREADY_HAS_QUEST = "У вас уже есть активное задание.";
    public static final String NO_FREE_PARTNERS = "Нет свободных участников.";
    public static final String PARTNER_ASSIGNED = "Вам назначено задание!\n\n%s";
    public static final String QUEST_WITH_PARTNER = "Партнёр: @%s\n\n%s";
    public static final String PARTNER_ASSIGNED_DETAILED =
            "Инициатор @%s назначил вам квест!\nПартнёр: @%s\n\n%s";
    public static final String PARTNER_PROOF_FORWARD = "Партнёр @%s отправил доказательство:";
    public static final String NO_ACTIVE_QUEST = "Нет активного задания.";
    public static final String QUEST_CANCELLED = "Задание отменено.";
    public static final String PARTNER_CANCELLED = "Партнёр @%s отменил задание.";
    public static final String QR_DECODE_FAIL = "Не удалось распознать QR.";
    public static final String CANNOT_WITH_SELF = "Нельзя с собой.";
    public static final String PARTNER_NOT_STARTED = "Пользователь @%s ещё не запускал бота.";
    public static final String PROOF_SENT =
            "Доказательство отправлено. Ожидайте проверки админом.";
    public static final String PARTNER_SENT_PROOF =
            "Партнёр @%s отправил доказательство. Ожидайте проверки админом.";
    public static final String PENDING_REVIEW =
            "Задание на проверку #%d\n@%s + @%s\nНаграда: %d 🪙\n%s";
    public static final String BTN_APPROVE = "✅ Подтвердить";
    public static final String BTN_REJECT = "❌ Отклонить";
    public static final String NO_RIGHTS = "Нет прав.";
    public static final String ENTER_NEW_TASK = "Введите текст нового задания:";
    public static final String PENDING_INFO = "Ожидает #%d: @%s + @%s";
    public static final String ALL_REVIEWED = "✅ Все задания проверены.";
    public static final String ALREADY_HANDLED = "Задание #%d уже обработано.";
    public static final String APPROVED = "✅ Задание #%d подтверждено.";
    public static final String REJECTED = "❌ Задание #%d отклонено.";
    public static final String QUEST_APPROVED_REWARD = "🎉 Задание одобрено! +%d 🪙";
    public static final String QUEST_REJECTED = "Задание отклонено.";
    public static final String QUEST_LIST_EMPTY = "Список квестов пуст.";
    public static final String QUEST_LIST_HEADER = "📋 Квесты (стр. %d)\n\n";
    public static final String QUEST_LIST_ITEM = "#%d — %d 🪙\n%s\n\n";
    public static final String BTN_QUEUE = "🔍 Очередь (%d)";
    public static final String BTN_ADD_QUEST = "➕ Добавить квест";
    public static final String BTN_LIST_QUESTS = "📋 Список квестов";
    public static final String ADMIN_PANEL_TITLE = "Админ-панель квестов";
    public static final String WELCOME =
            """
            Привет! Квест-бот Casino.

            📷 QR — задание с другом по username
            🎲 Случайный партнёр
            После выполнения — фото/видео доказательство

            🪙 Монеты — за каждый одобренный квест
            🃏 Карточки партнёра (между одной парой):
              • 1-й квест — обычная
              • 7-й — редкая
              • 15-й — легендарная""";
    public static final String MENU_TITLE = "Меню:";
    public static final String QUEST_INFO = "Задание:\n%s\n\nНаграда: %d 🪙";

    public static String questInfo(String description, long reward) {
        return QUEST_INFO.formatted(description, reward);
    }
}
