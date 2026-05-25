import WebApp from '@twa-dev/sdk';

const DEV_INIT = 'dev-local-login-token-2026';

export function initTelegram() {
  try {
    WebApp.ready();
    WebApp.expand();
    WebApp.setHeaderColor('#0f0f14');
    WebApp.setBackgroundColor('#0f0f14');
  } catch {
    /* outside Telegram */
  }
}

export function getInitData(): string {
  const fromTg = WebApp.initData;
  if (fromTg && fromTg.length > 10) return fromTg;

  const tgUser = WebApp.initDataUnsafe?.user;
  if (tgUser?.id) {
    throw new Error(
      'Не удалось получить данные Telegram. Закройте Mini App и откройте снова из бота.',
    );
  }

  return import.meta.env.VITE_DEV_INIT_DATA ?? DEV_INIT;
}

export function haptic(type: 'light' | 'medium' | 'heavy' | 'success' | 'error' = 'light') {
  try {
    if (type === 'success' || type === 'error') {
      WebApp.HapticFeedback.notificationOccurred(type);
    } else {
      WebApp.HapticFeedback.impactOccurred(type);
    }
  } catch {
    /* noop */
  }
}

export function getTelegramUser() {
  return WebApp.initDataUnsafe?.user ?? null;
}
