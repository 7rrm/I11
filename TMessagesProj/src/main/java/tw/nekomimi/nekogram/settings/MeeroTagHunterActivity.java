package tw.nekomimi.nekogram.settings;

import tw.nekomimi.nekogram.MeeroStrings;
import tw.nekomimi.nekogram.MeeroTagHunter;
import tw.nekomimi.nekogram.NekoConfig;

import static org.telegram.messenger.LocaleController.getString;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.DialogsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class MeeroTagHunterActivity extends BaseNekoSettingsActivity {

    private int masterRow;
    private int addChatsRow;
    private int groupStartRow;
    private int groupEndRow;
    private int clearLogRow;
    private int infoRow;

    private final ArrayList<MeeroTagHunter.TagGroup> groups = new ArrayList<>();
    private final HashSet<Long> expandedGroups = new HashSet<>();

    @Override
    protected void updateRows() {
        super.updateRows();
        reloadGroups();

        masterRow = addRow();
        addChatsRow = addRow();
        groupStartRow = rowCount;

        for (MeeroTagHunter.TagGroup group : groups) {
            addRow(); // اسم المجموعة
            if (expandedGroups.contains(group.dialogId)) {
                for (int i = 0; i < group.tags.size(); i++) {
                    addRow(); // التاك + الرسالة
                }
                addRow(); // "➕ إضافة تاك"
            }
        }
        groupEndRow = rowCount;
        clearLogRow = addRow();
        infoRow = addRow();
    }

    private void reloadGroups() {
        groups.clear();
        groups.addAll(MeeroTagHunter.getTagGroups());
    }

    @Override
    protected boolean meeroGlassScreen() {
        return true;
    }

    @Override
    protected String getActionBarTitle() {
        return MeeroStrings.s(485);
    }

    @Override
    protected BaseListAdapter createAdapter(Context context) {
        return new ListAdapter(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRows();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private String getChatName(long dialogId) {
        if (dialogId == 0) return MeeroStrings.s(489);
        MessagesController mc = MessagesController.getInstance(UserConfig.selectedAccount);
        if (DialogObject.isUserDialog(dialogId)) {
            TLRPC.User user = mc.getUser(dialogId);
            return user != null ? UserObject.getUserName(user) : "Unknown";
        } else {
            TLRPC.Chat chat = mc.getChat(-dialogId);
            return chat != null ? chat.title : "Unknown";
        }
    }

    private String timeOf(long sec) {
        return new SimpleDateFormat("dd/MM HH:mm", Locale.US).format(new Date(sec * 1000L));
    }

    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (position == masterRow) {
            NekoConfig.meeroTagHunter.toggleConfigBool();
            ((TextCheckCell) view).setChecked(NekoConfig.meeroTagHunter.Bool());
            return;
        }

        if (position == addChatsRow) {
            showAddChatsDialog();
            return;
        }

        if (position == clearLogRow) {
            showClearConfirm();
            return;
        }

        if (position == infoRow) {
            tw.nekomimi.nekogram.MeeroUsageGuide.show(this, 486);
            return;
        }

        int groupIndex = getGroupIndexAtPosition(position);
        if (groupIndex >= 0 && groupIndex < groups.size()) {
            toggleGroup(groups.get(groupIndex).dialogId);
            return;
        }

        TagItem tagItem = getTagItemAtPosition(position);
        if (tagItem != null) {
            if (tagItem.isAddTag) {
                showAddTagDialog(tagItem.group);
            } else if (tagItem.entry.lastMessageId != 0) {
                openMessage(tagItem.entry.dialogId, tagItem.entry.lastMessageId);
            }
            return;
        }
    }

    @Override
    protected boolean onItemLongClick(View view, int position, float x, float y) {
        TagItem tagItem = getTagItemAtPosition(position);
        if (tagItem != null && !tagItem.isAddTag) {
            showTagOptions(tagItem.group, tagItem.entry);
            return true;
        }
        return false;
    }

    // ✅ فتح الرسالة مباشرة
    private void openMessage(long dialogId, long msgId) {
        if (dialogId == 0 || msgId == 0) return;
        Bundle args = new Bundle();
        if (dialogId < 0) {
            args.putLong("chat_id", -dialogId);
        } else {
            args.putLong("user_id", dialogId);
        }
        args.putLong("message_id", msgId);
        org.telegram.ui.ChatActivity chatActivity = new org.telegram.ui.ChatActivity(args);
        presentFragment(chatActivity);
    }

    private int getGroupIndexAtPosition(int position) {
        int currentPos = groupStartRow;
        for (int i = 0; i < groups.size(); i++) {
            if (position == currentPos) return i;
            currentPos++;
            if (expandedGroups.contains(groups.get(i).dialogId)) {
                currentPos += groups.get(i).tags.size() + 1;
            }
        }
        return -1;
    }

    private static class TagItem {
        MeeroTagHunter.TagGroup group;
        MeeroTagHunter.TagEntry entry;
        boolean isAddTag;
        int index;
    }

    private TagItem getTagItemAtPosition(int position) {
        int currentPos = groupStartRow;
        for (MeeroTagHunter.TagGroup group : groups) {
            currentPos++;
            if (expandedGroups.contains(group.dialogId)) {
                for (int i = 0; i < group.tags.size(); i++) {
                    if (position == currentPos) {
                        TagItem item = new TagItem();
                        item.group = group;
                        item.entry = group.tags.get(i);
                        item.isAddTag = false;
                        item.index = i;
                        return item;
                    }
                    currentPos++;
                }
                if (position == currentPos) {
                    TagItem item = new TagItem();
                    item.group = group;
                    item.isAddTag = true;
                    return item;
                }
                currentPos++;
            }
        }
        return null;
    }

    private void toggleGroup(long dialogId) {
        if (expandedGroups.contains(dialogId)) {
            expandedGroups.remove(dialogId);
        } else {
            expandedGroups.add(dialogId);
        }
        updateRows();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    // ============================================================
    // إضافة دردشات
    // ============================================================

    private void showAddChatsDialog() {
        Context context = getParentActivity();
        if (context == null) return;

        String[] options = {
                MeeroStrings.s(489),
                MeeroStrings.s(490)
        };

        new AlertDialog.Builder(context)
                .setTitle(MeeroStrings.s(488))
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showAddTagsDialog(0, MeeroStrings.s(489));
                    } else {
                        pickChat();
                    }
                })
                .setNegativeButton(getString(R.string.Cancel), null)
                .show();
    }

    private void pickChat() {
        Bundle args = new Bundle();
        args.putBoolean("onlySelect", true);
        args.putBoolean("allowGlobalSearch", false);
        args.putBoolean("onlyChats", true);
        args.putBoolean("onlyGroups", true);

        DialogsActivity activity = new DialogsActivity(args);
        activity.setDelegate((fragment, dids, message, param, notify, scheduleDate, scheduleRepeatPeriod, topicsFragment) -> {
            if (dids != null && !dids.isEmpty()) {
                long dialogId = dids.get(0).dialogId;

                if (DialogObject.isUserDialog(dialogId)) {
                    BulletinFactory.of(MeeroTagHunterActivity.this)
                            .createSimpleBulletin(R.raw.chats_infotip, MeeroStrings.s(505))
                            .show();
                    return true;
                }

                String name = getChatName(dialogId);
                if (parentLayout != null) parentLayout.removeFragmentFromStack(fragment, true);
                showAddTagsDialog(dialogId, name);
                return true;
            }
            return false;
        });
        presentFragment(activity);
    }

    // ============================================================
    // إضافة تاكات لمجموعة
    // ============================================================

    private void showAddTagsDialog(long dialogId, String chatName) {
        Context context = getParentActivity();
        if (context == null) return;

        // ✅ عرض خيارين: إضافة تاك أو عرض السجل
        String[] options = {
                MeeroStrings.s(491), // "إضافة تاك"
                "📋 السجل"          // عرض جميع التاكات
        };

        new AlertDialog.Builder(context)
                .setTitle(chatName)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showTagInput(dialogId, chatName);
                    } else {
                        // عرض سجل التاكات لهذه المجموعة
                        showTagLog(dialogId, chatName);
                    }
                })
                .setNegativeButton(getString(R.string.Cancel), null)
                .show();
    }

    private void showTagInput(long dialogId, String chatName) {
        Context context = getParentActivity();
        if (context == null) return;

        View content = createTagInputView(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(chatName + " - " + MeeroStrings.s(491));
        builder.setView(content);
        builder.setPositiveButton(getString(R.string.Add), (dialog, which) -> {
            EditText editText = content.findViewById(1001);
            if (editText == null) return;
            String tag = editText.getText().toString().trim();
            if (TextUtils.isEmpty(tag)) {
                BulletinFactory.of(MeeroTagHunterActivity.this)
                        .createSimpleBulletin(R.raw.chats_infotip, MeeroStrings.s(499))
                        .show();
                return;
            }

            if (MeeroTagHunter.isTagExists(tag, dialogId)) {
                BulletinFactory.of(MeeroTagHunterActivity.this)
                        .createSimpleBulletin(R.raw.chats_infotip, MeeroStrings.s(498))
                        .show();
                return;
            }

            MeeroTagHunter.addTag(tag, false, dialogId, chatName);
            updateRows();
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }

            String msg = String.format(MeeroStrings.s(497), tag);
            BulletinFactory.of(MeeroTagHunterActivity.this)
                    .createSimpleBulletin(R.raw.chats_infotip, msg)
                    .show();
        });
        builder.setNegativeButton(getString(R.string.Cancel), null);
        AlertDialog dialog = builder.create();
        dialog.show();

        EditText editText = content.findViewById(1001);
        if (editText != null) {
            editText.post(() -> {
                editText.requestFocus();
                AndroidUtilities.showKeyboard(editText);
            });
        }
    }

    // ✅ عرض سجل التاكات
    private void showTagLog(long dialogId, String chatName) {
        // جمع جميع التاكات لهذه المجموعة
        StringBuilder sb = new StringBuilder();
        sb.append("📋 سجل التاكات - ").append(chatName).append("\n\n");
        
        for (MeeroTagHunter.TagGroup group : groups) {
            if (group.dialogId == dialogId) {
                for (MeeroTagHunter.TagEntry entry : group.tags) {
                    sb.append("• ").append(entry.tag);
                    if (!TextUtils.isEmpty(entry.lastMessage)) {
                        String sender = !TextUtils.isEmpty(entry.lastSenderName) ? entry.lastSenderName : "شخص ما";
                        sb.append("\n  ").append(sender).append(": ").append(entry.lastMessage);
                        sb.append("\n  ").append(timeOf(entry.lastMessageDate));
                    }
                    sb.append("\n\n");
                }
                break;
            }
        }

        if (sb.toString().equals("📋 سجل التاكات - " + chatName + "\n\n")) {
            sb.append(MeeroStrings.s(506)); // "لا توجد رسائل بعد"
        }

        new AlertDialog.Builder(getParentActivity())
                .setTitle(chatName + " - السجل")
                .setMessage(sb.toString())
                .setPositiveButton(getString(R.string.OK), null)
                .show();
    }

    private View createTagInputView(Context context) {
        FrameLayout container = new FrameLayout(context);

        EditText editText = new EditText(context);
        editText.setId(1001);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        editText.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        editText.setHintTextColor(getThemedColor(Theme.key_windowBackgroundWhiteHintText));
        editText.setHint(MeeroStrings.s(492));
        editText.setPadding(0, AndroidUtilities.dp(8), 0, 0);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(AndroidUtilities.dp(24), AndroidUtilities.dp(8), AndroidUtilities.dp(24), AndroidUtilities.dp(8));
        container.addView(editText, lp);

        return container;
    }

    private void showAddTagDialog(MeeroTagHunter.TagGroup group) {
        showAddTagsDialog(group.dialogId, group.chatName);
    }

    // ============================================================
    // خيارات التاك
    // ============================================================

    private void showTagOptions(MeeroTagHunter.TagGroup group, MeeroTagHunter.TagEntry entry) {
        new AlertDialog.Builder(getParentActivity())
                .setTitle(entry.tag)
                .setItems(new CharSequence[]{
                        MeeroStrings.s(496),
                        MeeroStrings.s(495)
                }, (dialog, which) -> {
                    if (which == 0) {
                        showEditTag(group, entry);
                    } else {
                        MeeroTagHunter.removeTag(entry.tag, group.dialogId);
                        updateRows();
                        if (listAdapter != null) {
                            listAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.Cancel), null)
                .show();
    }

    private void showEditTag(MeeroTagHunter.TagGroup group, MeeroTagHunter.TagEntry entry) {
        Context context = getParentActivity();
        if (context == null) return;

        View content = createTagInputView(context);
        EditText editText = content.findViewById(1001);
        if (editText != null) {
            editText.setText(entry.tag);
            editText.setSelection(editText.getText().length());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(group.chatName + " - " + MeeroStrings.s(496));
        builder.setView(content);
        builder.setPositiveButton(getString(R.string.Save), (dialog, which) -> {
            if (editText == null) return;
            String newTag = editText.getText().toString().trim();
            if (TextUtils.isEmpty(newTag)) {
                BulletinFactory.of(MeeroTagHunterActivity.this)
                        .createSimpleBulletin(R.raw.chats_infotip, MeeroStrings.s(499))
                        .show();
                return;
            }

            if (!newTag.equals(entry.tag) && MeeroTagHunter.isTagExists(newTag, group.dialogId)) {
                BulletinFactory.of(MeeroTagHunterActivity.this)
                        .createSimpleBulletin(R.raw.chats_infotip, MeeroStrings.s(498))
                        .show();
                return;
            }

            MeeroTagHunter.removeTag(entry.tag, group.dialogId);
            MeeroTagHunter.addTag(newTag, entry.trackReplies, group.dialogId, group.chatName);
            updateRows();
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }

            String msg = String.format(MeeroStrings.s(497), newTag);
            BulletinFactory.of(MeeroTagHunterActivity.this)
                    .createSimpleBulletin(R.raw.chats_infotip, msg)
                    .show();
        });
        builder.setNegativeButton(getString(R.string.Cancel), null);
        AlertDialog dialog = builder.create();
        dialog.show();

        if (editText != null) {
            editText.post(() -> {
                editText.requestFocus();
                AndroidUtilities.showKeyboard(editText);
            });
        }
    }

    // ============================================================
    // مسح جميع التاكات
    // ============================================================

    private void showClearConfirm() {
        new AlertDialog.Builder(getParentActivity())
                .setTitle(MeeroStrings.s(500))
                .setMessage(MeeroStrings.s(500))
                .setPositiveButton(getString(R.string.Delete), (dialog, which) -> {
                    MeeroTagHunter.clearAllTags();
                    updateRows();
                    if (listAdapter != null) {
                        listAdapter.notifyDataSetChanged();
                    }
                    BulletinFactory.of(MeeroTagHunterActivity.this)
                            .createSimpleBulletin(R.raw.chats_infotip, MeeroStrings.s(501))
                            .show();
                })
                .setNegativeButton(getString(R.string.Cancel), null)
                .show();
    }

    // ============================================================
    // Adapter
    // ============================================================

    private class ListAdapter extends BaseListAdapter {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == TYPE_CHECK || type == TYPE_TEXT || type == TYPE_DETAIL_SETTINGS;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, boolean payload) {
            switch (holder.getItemViewType()) {
                case TYPE_CHECK:
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (position == masterRow) {
                        checkCell.setTextAndCheck(MeeroStrings.s(487), NekoConfig.meeroTagHunter.Bool(), true);
                    }
                    break;

                case TYPE_TEXT:
                    TextCell textCell = (TextCell) holder.itemView;

                    int groupIndex = getGroupIndexAtPosition(position);
                    if (groupIndex >= 0 && groupIndex < groups.size()) {
                        MeeroTagHunter.TagGroup group = groups.get(groupIndex);
                        boolean isExpanded = expandedGroups.contains(group.dialogId);
                        int count = group.tags.size();
                        String arrow = isExpanded ? " ▼" : " ▶";
                        String value = count > 0 ? count + " " + arrow : arrow;
                        textCell.setTextAndValue(group.chatName, value, true);
                        return;
                    }

                    TagItem tagItem = getTagItemAtPosition(position);
                    if (tagItem != null && tagItem.isAddTag) {
                        textCell.setTextAndValue("➕ " + MeeroStrings.s(491), "", true);
                        return;
                    }

                    if (position == addChatsRow) {
                        textCell.setTextAndValue("➕ " + MeeroStrings.s(488), "", true);
                        return;
                    }
                    if (position == clearLogRow) {
                        textCell.setTextAndValue("🗑 " + MeeroStrings.s(502), "", true);
                        return;
                    }
                    if (position == infoRow) {
                        textCell.setTextAndValue("📖 " + MeeroStrings.s(505), "", true);
                        return;
                    }
                    break;

                case TYPE_DETAIL_SETTINGS:
                    TextDetailSettingsCell detailCell = (TextDetailSettingsCell) holder.itemView;
                    TagItem tagItem2 = getTagItemAtPosition(position);
                    if (tagItem2 != null && !tagItem2.isAddTag) {
                        MeeroTagHunter.TagEntry entry = tagItem2.entry;
                        String sender = entry.lastSenderName != null ? entry.lastSenderName : "";
                        String msg = entry.lastMessage != null ? entry.lastMessage : MeeroStrings.s(506);

                        if (msg.length() > 60) {
                            msg = msg.substring(0, 60) + "…";
                        }

                        // ✅ عرض: ذكرك (اسم الشخص) والرسالة بخط ناعم
                        String title = MeeroStrings.s(503) + " ( " + sender + " )";
                        String detail = msg + "  •  " + timeOf(entry.lastMessageDate);
                        detailCell.setTextAndValue(title, detail, position + 1 < groupEndRow);
                        detailCell.setMultilineDetail(true);
                    }
                    break;

                case TYPE_INFO_PRIVACY:
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    cell.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    if (position == groupEndRow && groups.isEmpty()) {
                        cell.setText(MeeroStrings.s(494));
                    }
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == masterRow) {
                return TYPE_CHECK;
            } else if (position == addChatsRow || position == clearLogRow || position == infoRow) {
                return TYPE_TEXT;
            } else if (position == groupEndRow && groups.isEmpty()) {
                return TYPE_INFO_PRIVACY;
            }

            int groupIndex = getGroupIndexAtPosition(position);
            if (groupIndex >= 0 && groupIndex < groups.size()) {
                return TYPE_TEXT;
            }

            TagItem tagItem = getTagItemAtPosition(position);
            if (tagItem != null && tagItem.isAddTag) {
                return TYPE_TEXT;
            }

            if (tagItem != null && !tagItem.isAddTag) {
                return TYPE_DETAIL_SETTINGS;
            }

            return TYPE_TEXT;
        }
    }
}
