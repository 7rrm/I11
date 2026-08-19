package tw.nekomimi.nekogram;

import tw.nekomimi.nekogram.MeeroStrings;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

/**
 * MeeroX v111 (user-requested): one shared "طريقة الاستخدام" popup.
 *
 * The long info footers at the bottom of the Meero feature sections grew
 * into screens of their own ("شرح كبير مخرب الشكل" - his words). Each of
 * those sections now ends with a tidy button instead; pressing it opens
 * this dialog with the SAME full explanation - no information is lost, the
 * screen just stops wearing it. Single "فهمت" button, reopenable anytime.
 */
public final class MeeroUsageGuide {

    private MeeroUsageGuide() {}

    /** Shows the usage dialog. Safe no-op without a live context. */
    public static void show(BaseFragment fragment, String textKey) {
        if (fragment == null || textKey == null) return;
        show(fragment.getParentActivity(), textKey);
    }

    public static void show(Context context, String textKey) {
        if (context == null || textKey == null) return;
        showDialog(context, MeeroStrings.s(textKey));
    }

    /* v186 (batch 2D): numeric vault-id form - call sites no longer carry
     * the guide key as a readable DEX literal. */
    public static void show(BaseFragment fragment, int textId) {
        if (fragment == null) return;
        show(fragment.getParentActivity(), textId);
    }

    public static void show(Context context, int textId) {
        if (context == null) return;
        showDialog(context, MeeroStrings.s(textId));
    }

    private static void showDialog(Context context, String message) {
        if (context == null || message == null) return;

        // Custom view with centered iOS-style button
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(
                AndroidUtilities.dp(20),
                AndroidUtilities.dp(16),
                AndroidUtilities.dp(20),
                AndroidUtilities.dp(8)
        );

        // Message text
        TextView messageView = new TextView(context);
        messageView.setText(message);
        messageView.setTextSize(16);
        messageView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        messageView.setGravity(Gravity.CENTER);
        messageView.setPadding(0, 0, 0, AndroidUtilities.dp(24));
        layout.addView(messageView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // iOS-style button container (centered)
        LinearLayout buttonContainer = new LinearLayout(context);
        buttonContainer.setGravity(Gravity.CENTER);
        buttonContainer.setPadding(0, AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8));

        Button button = new Button(context);
        button.setText(MeeroStrings.s(269)); // "Got it" / "فهمت"
        button.setTextSize(16);
        button.setTextColor(Color.WHITE);
        button.setAllCaps(false);
        button.setPadding(
                AndroidUtilities.dp(40),
                AndroidUtilities.dp(12),
                AndroidUtilities.dp(40),
                AndroidUtilities.dp(12)
        );

        // Blue rounded oval background
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(Theme.getColor(Theme.key_dialogButton));
        drawable.setCornerRadius(AndroidUtilities.dp(30));
        button.setBackground(drawable);

        // Click to dismiss
        button.setOnClickListener(v -> {
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).finish();
            }
        });

        buttonContainer.addView(button);
        layout.addView(buttonContainer, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // Build alert
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(MeeroStrings.s(268))
                .setView(layout)
                .setPositiveButton(null, null);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
