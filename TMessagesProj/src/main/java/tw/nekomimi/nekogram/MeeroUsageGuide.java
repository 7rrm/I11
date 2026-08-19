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

        // إنشاء AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(MeeroStrings.s(268));
        builder.setMessage(MeeroStrings.s(textKey));
        
        // إنشاء زر مخصص بدلاً من استخدام setPositiveButton
        final AlertDialog dialog = builder.create();
        
        // إنشاء LinearLayout للزر
        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        buttonLayout.setPadding(
            AndroidUtilities.dp(16),
            AndroidUtilities.dp(8),
            AndroidUtilities.dp(16),
            AndroidUtilities.dp(16)
        );
        
        // إنشاء الزر المخصص
        Button positiveButton = new Button(context);
        positiveButton.setText(MeeroStrings.s(269));
        positiveButton.setTextSize(16);
        positiveButton.setTextColor(Color.WHITE);
        positiveButton.setGravity(Gravity.CENTER);
        
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
        
        // إضافة الزر إلى Layout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER_HORIZONTAL;
        positiveButton.setLayoutParams(params);
        buttonLayout.addView(positiveButton);
        
        // إضافة الـ Layout إلى الحوار
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, MeeroStrings.s(269), (d, which) -> d.dismiss());
        
        // استبدال الزر الأصلي بالزر المخصص
        dialog.setOnShowListener(dialogInterface -> {
            try {
                // إزالة الزر الأصلي وإضافة الزر المخصص
                View originalButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (originalButton != null) {
                    View parent = (View) originalButton.getParent();
                    if (parent instanceof LinearLayout) {
                        // نضيف الزر المخصص إلى نفس المكان
                        LinearLayout parentLayout = (LinearLayout) parent;
                        int index = parentLayout.indexOfChild(originalButton);
                        parentLayout.removeView(originalButton);
                        
                        // إنشاء زر جديد بتنسيق iOS
                        Button newButton = new Button(context);
                        newButton.setText(MeeroStrings.s(269));
                        newButton.setTextSize(16);
                        newButton.setTextColor(Color.WHITE);
                        newButton.setGravity(Gravity.CENTER);
                        
                        GradientDrawable newDrawable = new GradientDrawable();
                        newDrawable.setShape(GradientDrawable.RECTANGLE);
                        newDrawable.setCornerRadius(AndroidUtilities.dp(25));
                        newDrawable.setColor(Color.parseColor("#007AFF"));
                        newButton.setBackground(newDrawable);
                        newButton.setPadding(
                            AndroidUtilities.dp(32),
                            AndroidUtilities.dp(12),
                            AndroidUtilities.dp(32),
                            AndroidUtilities.dp(12)
                        );
                        
                        LinearLayout.LayoutParams newParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        newParams.gravity = Gravity.CENTER_HORIZONTAL;
                        newParams.topMargin = AndroidUtilities.dp(8);
                        newParams.bottomMargin = AndroidUtilities.dp(8);
                        newButton.setLayoutParams(newParams);
                        
                        newButton.setOnClickListener(v -> dialog.dismiss());
                        parentLayout.addView(newButton, index);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(MeeroStrings.s(268));
        builder.setMessage(MeeroStrings.s(textId));
        
        final AlertDialog dialog = builder.create();
        
        dialog.setOnShowListener(dialogInterface -> {
            try {
                View originalButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (originalButton != null) {
                    View parent = (View) originalButton.getParent();
                    if (parent instanceof LinearLayout) {
                        LinearLayout parentLayout = (LinearLayout) parent;
                        int index = parentLayout.indexOfChild(originalButton);
                        parentLayout.removeView(originalButton);
                        
                        Button newButton = new Button(context);
                        newButton.setText(MeeroStrings.s(269));
                        newButton.setTextSize(16);
                        newButton.setTextColor(Color.WHITE);
                        newButton.setGravity(Gravity.CENTER);
                        
                        GradientDrawable newDrawable = new GradientDrawable();
                        newDrawable.setShape(GradientDrawable.RECTANGLE);
                        newDrawable.setCornerRadius(AndroidUtilities.dp(25));
                        newDrawable.setColor(Color.parseColor("#007AFF"));
                        newButton.setBackground(newDrawable);
                        newButton.setPadding(
                            AndroidUtilities.dp(32),
                            AndroidUtilities.dp(12),
                            AndroidUtilities.dp(32),
                            AndroidUtilities.dp(12)
                        );
                        
                        LinearLayout.LayoutParams newParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        newParams.gravity = Gravity.CENTER_HORIZONTAL;
                        newParams.topMargin = AndroidUtilities.dp(8);
                        newParams.bottomMargin = AndroidUtilities.dp(8);
                        newButton.setLayoutParams(newParams);
                        
                        newButton.setOnClickListener(v -> dialog.dismiss());
                        parentLayout.addView(newButton, index);
                    }
                }
            } catch (Throwable ignored) {}
        });
        
        dialog.show();
    }
}
