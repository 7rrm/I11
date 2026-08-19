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

        // الطريقة الأضمن: استخدام AlertDialog مع setPositiveButton
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(MeeroStrings.s(268))
                .setMessage(MeeroStrings.s(textKey))
                .setPositiveButton(MeeroStrings.s(269), null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            try {
                // الحصول على الزر
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (positiveButton != null) {
                    // تغيير شكل الزر
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
                    
                    // توسيط الزر في النافذة
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                    if (params != null) {
                        params.gravity = Gravity.CENTER_HORIZONTAL;
                        params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        params.topMargin = AndroidUtilities.dp(8);
                        params.bottomMargin = AndroidUtilities.dp(8);
                        positiveButton.setLayoutParams(params);
                    }
                }
            } catch (Throwable ignored) {}
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
            try {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (positiveButton != null) {
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
                    
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                    if (params != null) {
                        params.gravity = Gravity.CENTER_HORIZONTAL;
                        params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        params.topMargin = AndroidUtilities.dp(8);
                        params.bottomMargin = AndroidUtilities.dp(8);
                        positiveButton.setLayoutParams(params);
                    }
                }
            } catch (Throwable ignored) {}
        });

        dialog.show();
    }
}
