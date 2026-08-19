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

        // إنشاء AlertDialog بدون setPositiveButton
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(MeeroStrings.s(268));
        builder.setMessage(MeeroStrings.s(textKey));
        
        final AlertDialog dialog = builder.create();
        
        // إنشاء الزر المخصص وإضافته
        dialog.setOnShowListener(dialogInterface -> {
            try {
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
                
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.gravity = Gravity.CENTER_HORIZONTAL;
                params.topMargin = AndroidUtilities.dp(8);
                params.bottomMargin = AndroidUtilities.dp(8);
                positiveButton.setLayoutParams(params);
                
                // إضافة الزر إلى Layout
                buttonLayout.addView(positiveButton);
                
                // إضافة الـ Layout إلى الحوار
                // نبحث عن الـ Layout الرئيسي للحوار ونضيف الزر تحته
                View decorView = dialog.getWindow().getDecorView();
                if (decorView instanceof ViewGroup) {
                    // نبحث عن الـ FrameLayout الذي يحتوي على محتوى الحوار
                    ViewGroup root = (ViewGroup) decorView;
                    for (int i = 0; i < root.getChildCount(); i++) {
                        View child = root.getChildAt(i);
                        if (child instanceof ViewGroup) {
                            // نضيف الزر إلى نهاية الـ Layout
                            ViewGroup content = (ViewGroup) child;
                            content.addView(buttonLayout);
                        }
                    }
                }
                
                // إغلاق الحوار عند الضغط على الزر
                positiveButton.setOnClickListener(v -> dialog.dismiss());
                
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
                LinearLayout buttonLayout = new LinearLayout(context);
                buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
                buttonLayout.setGravity(Gravity.CENTER_HORIZONTAL);
                buttonLayout.setPadding(
                    AndroidUtilities.dp(16),
                    AndroidUtilities.dp(8),
                    AndroidUtilities.dp(16),
                    AndroidUtilities.dp(16)
                );
                
                Button positiveButton = new Button(context);
                positiveButton.setText(MeeroStrings.s(269));
                positiveButton.setTextSize(16);
                positiveButton.setTextColor(Color.WHITE);
                positiveButton.setGravity(Gravity.CENTER);
                
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
                
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.gravity = Gravity.CENTER_HORIZONTAL;
                params.topMargin = AndroidUtilities.dp(8);
                params.bottomMargin = AndroidUtilities.dp(8);
                positiveButton.setLayoutParams(params);
                
                buttonLayout.addView(positiveButton);
                
                View decorView = dialog.getWindow().getDecorView();
                if (decorView instanceof ViewGroup) {
                    ViewGroup root = (ViewGroup) decorView;
                    for (int i = 0; i < root.getChildCount(); i++) {
                        View child = root.getChildAt(i);
                        if (child instanceof ViewGroup) {
                            ViewGroup content = (ViewGroup) child;
                            content.addView(buttonLayout);
                        }
                    }
                }
                
                positiveButton.setOnClickListener(v -> dialog.dismiss());
                
            } catch (Throwable ignored) {}
        });
        
        dialog.show();
    }
}
