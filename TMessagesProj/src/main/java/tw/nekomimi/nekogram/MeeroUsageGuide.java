package tw.nekomimi.nekogram;

import tw.nekomimi.nekogram.MeeroStrings;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(MeeroStrings.s(268))
                .setMessage(MeeroStrings.s(textKey));

        AlertDialog dialog = builder.create();
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, MeeroStrings.s(269), (di, which) -> di.dismiss());

        dialog.setOnShowListener(dialogInterface -> {
            // التصحيح: استخدام (View) ثم التحويل إلى (Button)
            View view = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (view instanceof Button) {
                Button positiveButton = (Button) view;
                
                // 1. توسيط الزر
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                params.gravity = Gravity.CENTER_HORIZONTAL;
                params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                params.topMargin = AndroidUtilities.dp(8);
                params.bottomMargin = AndroidUtilities.dp(8);
                positiveButton.setLayoutParams(params);
                positiveButton.setGravity(Gravity.CENTER);

                // 2. تنسيق الزر بشكل بيضاوي (iOS style)
                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setCornerRadius(AndroidUtilities.dp(25));
                drawable.setColor(Color.TRANSPARENT);
                drawable.setStroke(AndroidUtilities.dp(2), Color.parseColor("#007AFF"));

                positiveButton.setBackground(drawable);
                positiveButton.setTextColor(Color.parseColor("#007AFF"));
                positiveButton.setTextSize(16);
                positiveButton.setPadding(
                    AndroidUtilities.dp(32),
                    AndroidUtilities.dp(12),
                    AndroidUtilities.dp(32),
                    AndroidUtilities.dp(12)
                );

                // 3. تأثير الضغط
                positiveButton.setOnTouchListener((v, event) -> {
                    switch (event.getAction()) {
                        case android.view.MotionEvent.ACTION_DOWN:
                            v.setAlpha(0.7f);
                            break;
                        case android.view.MotionEvent.ACTION_UP:
                        case android.view.MotionEvent.ACTION_CANCEL:
                            v.setAlpha(1.0f);
                            break;
                    }
                    return false;
                });
            }
        });

        dialog.show();
    }

    /* v186 (batch 2D): numeric vault-id form - call sites no longer carry
     * the guide key as a readable DEX literal. */
    public static void show(BaseFragment fragment, int textId) {
        if (fragment == null) return;
        show(fragment.getParentActivity(), textId);
    }

    public static void show(Context context, int textId) {
        if (context == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(MeeroStrings.s(268))
                .setMessage(MeeroStrings.s(textId));

        AlertDialog dialog = builder.create();
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, MeeroStrings.s(269), (di, which) -> di.dismiss());

        dialog.setOnShowListener(dialogInterface -> {
            View view = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (view instanceof Button) {
                Button positiveButton = (Button) view;
                
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                params.gravity = Gravity.CENTER_HORIZONTAL;
                params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                params.topMargin = AndroidUtilities.dp(8);
                params.bottomMargin = AndroidUtilities.dp(8);
                positiveButton.setLayoutParams(params);
                positiveButton.setGravity(Gravity.CENTER);

                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setCornerRadius(AndroidUtilities.dp(25));
                drawable.setColor(Color.TRANSPARENT);
                drawable.setStroke(AndroidUtilities.dp(2), Color.parseColor("#007AFF"));

                positiveButton.setBackground(drawable);
                positiveButton.setTextColor(Color.parseColor("#007AFF"));
                positiveButton.setTextSize(16);
                positiveButton.setPadding(
                    AndroidUtilities.dp(32),
                    AndroidUtilities.dp(12),
                    AndroidUtilities.dp(32),
                    AndroidUtilities.dp(12)
                );

                positiveButton.setOnTouchListener((v, event) -> {
                    switch (event.getAction()) {
                        case android.view.MotionEvent.ACTION_DOWN:
                            v.setAlpha(0.7f);
                            break;
                        case android.view.MotionEvent.ACTION_UP:
                        case android.view.MotionEvent.ACTION_CANCEL:
                            v.setAlpha(1.0f);
                            break;
                    }
                    return false;
                });
            }
        });

        dialog.show();
    }
}
