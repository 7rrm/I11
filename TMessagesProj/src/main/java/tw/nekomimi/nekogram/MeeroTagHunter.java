package tw.nekomimi.nekogram;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LaunchActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class MeeroTagHunter {

    private static final String CHANNEL_ID = "meero_tag_hunter";
    private static final long THROTTLE_MS = 5000;
    private static volatile boolean started;
    private static final ConcurrentHashMap<Long, Long> lastNotifyAt = new ConcurrentHashMap<>();

    // ============================================================
    // هيكل البيانات
    // ============================================================

    public static class TagEntry {
        public String tag;
        public boolean trackReplies;
        public long dialogId;
        public String chatName;
        public String lastMessage;
        public long lastMessageId;
        public long lastMessageDate;
        public String lastSenderName;
    }

    public static class TagGroup {
        public long dialogId;
        public String chatName;
        public ArrayList<TagEntry> tags = new ArrayList<>();
    }

    // ============================================================
    // بدء التشغيل
    // ============================================================

    public static void start() {
        if (started) return;
        synchronized (MeeroTagHunter.class) {
            if (started) return;
            started = true;
            for (int account = 0; account < UserConfig.MAX_ACCOUNT_COUNT; account++) {
                NotificationCenter.getInstance(account).addObserver((id, account1, args) -> {
                    if (id == NotificationCenter.didReceiveNewMessages) {
                        onNewMessages(account1, args);
                    }
                }, NotificationCenter.didReceiveNewMessages);
            }
        }
    }

    // ============================================================
    // معالجة الرسائل
    // ============================================================

    private static void onNewMessages(int account, Object[] args) {
        if (!NekoConfig.meeroTagHunter.Bool()) return;
        if (!UserConfig.getInstance(account).isClientActivated()) return;
        if (args == null || args.length < 3) return;

        long now = System.currentTimeMillis();
        long dialogId = (Long) args[0];
        @SuppressWarnings("unchecked")
        ArrayList<MessageObject> messages = (ArrayList<MessageObject>) args[1];
        boolean scheduled = (Boolean) args[2];
        if (scheduled || messages == null) return;

        if (!DialogObject.isChatDialog(dialogId)) return;

        ArrayList<TagEntry> tags = getTags();
        if (tags.isEmpty()) return;

        long selfId = UserConfig.getInstance(account).getClientUserId();

        for (MessageObject msg : messages) {
            if (msg == null || msg.isOut()) continue;
            if (msg.messageOwner == null || msg.messageOwner.action != null) continue;
            if (now - msg.messageOwner.date * 1000L > 120000L) continue;

            String text = msg.messageText != null ? msg.messageText.toString() : "";
            if (TextUtils.isEmpty(text)) continue;
            String lowerText = text.toLowerCase(Locale.ROOT);

            for (TagEntry entry : tags) {
                if (entry.tag == null) continue;
                if (entry.dialogId != 0 && entry.dialogId != dialogId) continue;

                String tagLower = entry.tag.toLowerCase(Locale.ROOT);
                if (!lowerText.contains(tagLower)) continue;

                // ✅ تصحيح: استخدام getFromId() بدلاً من getFromUserId()
                if (entry.trackReplies && msg.replyMessageObject != null) {
                    long replyFromId = msg.replyMessageObject.getFromId();
                    if (replyFromId != selfId) continue;
                }

                String senderName = getSenderName(msg, account);
                entry.lastMessage = text;
                entry.lastMessageId = msg.getId();
                entry.lastMessageDate = msg.messageOwner.date;
                entry.lastSenderName = senderName;
                updateTagMessage(entry.tag, entry.dialogId, text, msg.getId(), msg.messageOwner.date, senderName);

                Long last = lastNotifyAt.get(dialogId);
                if (last != null && now - last < THROTTLE_MS) break;
                lastNotifyAt.put(dialogId, now);

                notifyTag(account, dialogId, msg.getId(), entry.tag, msg.messageText, msg, senderName);
                break;
            }
        }
    }

    private static String getSenderName(MessageObject msg, int account) {
        try {
            if (msg.messageOwner.from_id != null && msg.messageOwner.from_id.user_id != 0) {
                TLRPC.User user = MessagesController.getInstance(account).getUser(msg.messageOwner.from_id.user_id);
                if (user != null) return UserObject.getUserName(user);
            }
        } catch (Throwable e) {}
        return MeeroStrings.s(127);
    }

    // ============================================================
    // الإشعارات
    // ============================================================

    private static void notifyTag(int account, long dialogId, int msgId, String tag, CharSequence messageText, MessageObject msg, String senderName) {
        try {
            Context ctx = ApplicationLoader.applicationContext;
            NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        MeeroStrings.s(485), NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
            }

            Intent intent = new Intent(ctx, LaunchActivity.class);
            intent.setAction("open_message");
            intent.putExtra("account", account);
            intent.putExtra("dialogId", dialogId);
            intent.putExtra("messageId", msgId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(ctx, msgId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            String snippet = messageText != null ? messageText.toString().replace('\n', ' ').trim() : "";
            if (snippet.length() > 100) snippet = snippet.substring(0, 100) + "…";

            String chatTitle = getChatTitle(dialogId);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                    .setSmallIcon(R.drawable.nagram_notification)
                    .setContentTitle("🔔 " + senderName + " • " + chatTitle)
                    .setContentText(snippet)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat.from(ctx).notify(("tag:" + System.currentTimeMillis()).hashCode(), builder.build());
        } catch (Throwable t) {}
    }

    private static String getChatTitle(long dialogId) {
        try {
            if (DialogObject.isUserDialog(dialogId)) {
                TLRPC.User user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(dialogId);
                return user != null ? UserObject.getUserName(user) : "Unknown";
            } else {
                TLRPC.Chat chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(-dialogId);
                return chat != null ? chat.title : "Unknown";
            }
        } catch (Throwable e) {
            return "Chat";
        }
    }

    // ============================================================
    // إدارة التاكات (JSON)
    // ============================================================

    private static JSONArray readTags() {
        try {
            String raw = NekoConfig.meeroTagHunterList.String();
            if (!TextUtils.isEmpty(raw)) return new JSONArray(raw);
        } catch (Throwable ignore) {}
        return new JSONArray();
    }

    private static synchronized void writeTags(JSONArray array) {
        NekoConfig.meeroTagHunterList.setConfigString(array == null ? "" : array.toString());
    }

    public static synchronized ArrayList<TagEntry> getTags() {
        ArrayList<TagEntry> out = new ArrayList<>();
        JSONArray array = readTags();
        for (int i = 0; i < array.length(); i++) {
            JSONObject o = array.optJSONObject(i);
            if (o == null) continue;
            TagEntry e = new TagEntry();
            e.tag = o.optString("tag", "");
            e.trackReplies = o.optBoolean("replies", false);
            e.dialogId = o.optLong("dialogId", 0);
            e.chatName = o.optString("chatName", "");
            e.lastMessage = o.optString("lastMessage", "");
            e.lastMessageId = o.optLong("lastMessageId", 0);
            e.lastMessageDate = o.optLong("lastMessageDate", 0);
            e.lastSenderName = o.optString("lastSenderName", "");
            if (!TextUtils.isEmpty(e.tag)) out.add(e);
        }
        return out;
    }

    public static synchronized ArrayList<TagGroup> getTagGroups() {
        ArrayList<TagGroup> groups = new ArrayList<>();
        HashMap<Long, TagGroup> map = new HashMap<>();

        for (TagEntry entry : getTags()) {
            TagGroup group = map.get(entry.dialogId);
            if (group == null) {
                group = new TagGroup();
                group.dialogId = entry.dialogId;
                group.chatName = entry.chatName;
                if (entry.dialogId == 0) {
                    group.chatName = MeeroStrings.s(489);
                }
                map.put(entry.dialogId, group);
                groups.add(group);
            }
            group.tags.add(entry);
        }

        groups.sort((a, b) -> {
            if (a.dialogId == 0) return -1;
            if (b.dialogId == 0) return 1;
            return a.chatName.compareToIgnoreCase(b.chatName);
        });

        return groups;
    }

    private static synchronized void updateTagMessage(String tag, long dialogId, String message, long msgId, long date, String sender) {
        JSONArray array = readTags();
        for (int i = 0; i < array.length(); i++) {
            JSONObject o = array.optJSONObject(i);
            if (o == null) continue;
            if (tag.equals(o.optString("tag", "")) && dialogId == o.optLong("dialogId", 0)) {
                try {
                    o.put("lastMessage", message);
                    o.put("lastMessageId", msgId);
                    o.put("lastMessageDate", date);
                    o.put("lastSenderName", sender);
                } catch (Throwable ignore) {}
                break;
            }
        }
        writeTags(array);
    }

    public static synchronized void addTag(String tag, boolean trackReplies, long dialogId, String chatName) {
        if (TextUtils.isEmpty(tag)) return;
        if (dialogId != 0 && DialogObject.isUserDialog(dialogId)) return;

        JSONArray array = readTags();
        for (int i = 0; i < array.length(); i++) {
            JSONObject o = array.optJSONObject(i);
            if (o != null && tag.equals(o.optString("tag", "")) && dialogId == o.optLong("dialogId", 0)) {
                return;
            }
        }
        try {
            JSONObject o = new JSONObject();
            o.put("tag", tag);
            o.put("replies", trackReplies);
            o.put("dialogId", dialogId);
            o.put("chatName", chatName != null ? chatName : "");
            o.put("lastMessage", "");
            o.put("lastMessageId", 0);
            o.put("lastMessageDate", 0);
            o.put("lastSenderName", "");
            array.put(o);
            writeTags(array);
        } catch (Throwable ignore) {}
    }

    public static synchronized void removeTag(String tag, long dialogId) {
        if (TextUtils.isEmpty(tag)) return;
        JSONArray array = readTags();
        JSONArray out = new JSONArray();
        for (int i = 0; i < array.length(); i++) {
            JSONObject o = array.optJSONObject(i);
            if (o != null && tag.equals(o.optString("tag", "")) && dialogId == o.optLong("dialogId", 0)) {
                continue;
            }
            if (o != null) out.put(o);
        }
        writeTags(out);
    }

    public static synchronized void clearAllTags() {
        writeTags(new JSONArray());
    }

    public static boolean isTagExists(String tag, long dialogId) {
        for (TagEntry entry : getTags()) {
            if (entry.tag.equals(tag) && entry.dialogId == dialogId) {
                return true;
            }
        }
        return false;
    }
                }
