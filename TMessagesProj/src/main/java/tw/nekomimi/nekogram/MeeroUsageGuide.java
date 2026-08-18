package tw.nekomimi.nekogram;

import tw.nekomimi.nekogram.MeeroStrings;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

public final class MeeroUsageGuide {

    private MeeroUsageGuide() {}

    public static void show(BaseFragment fragment, String textKey) {
        if (fragment == null || textKey == null) return;
        show(fragment.getParentActivity(), textKey);
    }

    public static void show(Context context, String textKey) {
        if (context == null || textKey == null) return;

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(MeeroStrings.s(268))
                .setMessage(MeeroStrings.s(textKey))
                .setPositiveButton(MeeroStrings.s(269), null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (positiveButton != null) {
                // 1. توسيط الزر
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                params.gravity = Gravity.CENTER_HORIZONTAL;
                params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                params.topMargin = AndroidUtilities.dp(8);
                params.bottomMargin = AndroidUtilities.dp(8);
                positiveButton.setLayoutParams(params);
                positiveButton.setGravity(Gravity.CENTER);

                // 2. شكل بيضاوي (iOS style) - خلفية زرقاء
                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setCornerRadius(AndroidUtilities.dp(25)); // بيضاوي
                drawable.setColor(Color.parseColor("#007AFF")); // خلفية زرقاء فاتحة

                positiveButton.setBackground(drawable);
                positiveButton.setTextColor(Color.WHITE); // نص أبيض
                positiveButton.setTextSize(16);
                positiveButton.setPadding(
                    AndroidUtilities.dp(32),
                    AndroidUtilities.dp(12),
                    AndroidUtilities.dp(32),
                    AndroidUtilities.dp(12)
                );

                // 3. تأثير الضغط (يخف الزر عند الضغط)
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

    public static void show(BaseFragment fragment, int textId) {
        if (fragment == null) return;
        show(fragment.getParentActivity(), textId);
    }

    public static void show(Context context, int textId) {
        if (context == null) return;

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(MeeroStrings.s(268))
                .setMessage(MeeroStrings.s(textId))
                .setPositiveButton(MeeroStrings.s(269), null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (positiveButton != null) {
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
                drawable.setColor(Color.parseColor("#007AFF")); // خلفية زرقاء

                positiveButton.setBackground(drawable);
                positiveButton.setTextColor(Color.WHITE); // نص أبيض
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
