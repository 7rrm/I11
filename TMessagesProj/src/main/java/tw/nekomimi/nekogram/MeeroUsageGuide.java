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
            // استخدام View ثم التحقق من النوع
            View view = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (view instanceof Button) {
                Button positiveButton = (Button) view;
                
                // توسيط الزر في النافذة
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                if (params != null) {
                    params.gravity = Gravity.CENTER_HORIZONTAL;
                    params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    params.topMargin = AndroidUtilities.dp(8);
                    params.bottomMargin = AndroidUtilities.dp(8);
                    positiveButton.setLayoutParams(params);
                }
                
                // توسيط النص داخل الزر
                positiveButton.setGravity(Gravity.CENTER);
                positiveButton.setTextColor(Color.WHITE);
                positiveButton.setTextSize(16);
                
                // شكل بيضاوي - خلفية زرقاء
                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setCornerRadius(AndroidUtilities.dp(25));
                drawable.setColor(Color.parseColor("#007AFF"));
                positiveButton.setBackground(drawable);
                positiveButton.setPadding(
                    AndroidUtilities.dp(32),
                    AndroidUtilities.dp(12),
                    AndroidUtilities.dp(32),
                    AndroidUtilities.dp(12)
                );
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
            View view = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (view instanceof Button) {
                Button positiveButton = (Button) view;
                
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                if (params != null) {
                    params.gravity = Gravity.CENTER_HORIZONTAL;
                    params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    params.topMargin = AndroidUtilities.dp(8);
                    params.bottomMargin = AndroidUtilities.dp(8);
                    positiveButton.setLayoutParams(params);
                }
                
                positiveButton.setGravity(Gravity.CENTER);
                positiveButton.setTextColor(Color.WHITE);
                positiveButton.setTextSize(16);
                
                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setCornerRadius(AndroidUtilities.dp(25));
                drawable.setColor(Color.parseColor("#007AFF"));
                positiveButton.setBackground(drawable);
                positiveButton.setPadding(
                    AndroidUtilities.dp(32),
                    AndroidUtilities.dp(12),
                    AndroidUtilities.dp(32),
                    AndroidUtilities.dp(12)
                );
            }
        });

        dialog.show();
    }
}
