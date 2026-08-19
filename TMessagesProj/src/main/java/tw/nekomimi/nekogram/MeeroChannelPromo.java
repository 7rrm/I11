package tw.nekomimi.nekogram;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

/**
 * MeeroX v194: one-time subscribe prompt for the owner's main channel.
 */
public final class MeeroChannelPromo {

    private MeeroChannelPromo() {
    }

    private static final String PREF_DONE = "meerox_channel_promo_done";
    private static final String CHANNEL = "InaRaS5";

    private static AlertDialog showing;

    public static void maybeShow(BaseFragment fragment) {
        try {
            if (fragment == null) {
                return;
            }
            final Activity activity = fragment.getParentActivity();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            if (MessagesController.getGlobalMainSettings().getBoolean(PREF_DONE, false)) {
                return;
            }
            if (showing != null && showing.isShowing()) {
                return;
            }

            // تصميم iOS
            LinearLayout layout = new LinearLayout(activity);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(
                    AndroidUtilities.dp(24),
                    AndroidUtilities.dp(20),
                    AndroidUtilities.dp(24),
                    AndroidUtilities.dp(16)
            );

            // النص
            TextView messageView = new TextView(activity);
            messageView.setText(MeeroStrings.s(464));
            messageView.setTextSize(15);
            messageView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            messageView.setGravity(Gravity.CENTER);
            messageView.setPadding(0, 0, 0, AndroidUtilities.dp(28));
            layout.addView(messageView, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            // حاوية الزر (في المنتصف)
            LinearLayout buttonContainer = new LinearLayout(activity);
            buttonContainer.setGravity(Gravity.CENTER);
            buttonContainer.setPadding(0, AndroidUtilities.dp(4), 0, AndroidUtilities.dp(8));

            Button button = new Button(activity);
            button.setText(MeeroStrings.s(465)); // "اشتراك في القناة"
            button.setTextSize(15);
            button.setTextColor(Color.WHITE);
            button.setAllCaps(false);
            
            // ضبط ارتفاع الزر
            int buttonHeight = AndroidUtilities.dp(44);
            
            // شكل بيضاوي رفيع (مثل iOS)
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setColor(Theme.getColor(Theme.key_dialogButton));
            drawable.setCornerRadius(AndroidUtilities.dp(22)); // نصف الارتفاع = بيضاوي
            button.setBackground(drawable);

            // Padding للزر
            button.setPadding(
                    AndroidUtilities.dp(32),
                    0,
                    AndroidUtilities.dp(32),
                    0
            );

            // ضبط عرض وارتفاع الزر
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    buttonHeight
            );
            button.setLayoutParams(params);

            // عند الضغط
            button.setOnClickListener(v -> {
                try {
                    MessagesController.getGlobalMainSettings().edit()
                            .putBoolean(PREF_DONE, true).apply();
                } catch (Throwable ignore) {
                }
                try {
                    MessagesController.getInstance(fragment.getCurrentAccount())
                            .openByUserName(CHANNEL, fragment, 1);
                } catch (Throwable ignore) {
                }
                if (showing != null) {
                    showing.dismiss();
                }
            });

            buttonContainer.addView(button);
            layout.addView(buttonContainer, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(MeeroStrings.s(463)) // "قناتي الاساسيه"
                    .setView(layout)
                    .setPositiveButton(null, null);

            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnDismissListener(d -> showing = null);
            showing = dialog;
            dialog.show();

        } catch (Throwable ignore) {
            showing = null;
        }
    }
}
