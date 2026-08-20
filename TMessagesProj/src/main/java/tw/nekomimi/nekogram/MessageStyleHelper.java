package tw.nekomimi.nekogram;

import android.text.TextUtils;

import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;

/**
 * مساعد أنماط الرسائل
 * يحتوي على ثوابت أنماط الخطوط وأسمائها وطرق تطبيقها
 */
public class MessageStyleHelper {

    // ============================================================
    // ثوابت الأنماط (Style Constants)
    // ============================================================
    public static final int STYLE_DEFAULT = 0;
    public static final int STYLE_BOLD = 1;
    public static final int STYLE_ITALIC = 2;
    public static final int STYLE_STRIKE = 3;
    public static final int STYLE_UNDERLINE = 4;
    public static final int STYLE_SPOILER = 5;
    public static final int STYLE_QUOTE = 6;
    public static final int STYLE_CODE = 7;
    public static final int STYLE_MONO = 8;
    
    // ============================================================
    // مفاتيح الترجمة (مرتبطة بـ MeeroStrings)
    // ============================================================
    public static final String[] STYLE_KEYS = {
        "MessageStyleDefault",    // 474
        "MessageStyleBold",       // 475
        "MessageStyleItalic",     // 476
        "MessageStyleStrike",     // 477
        "MessageStyleUnderline",  // 478
        "MessageStyleSpoiler",    // 479
        "MessageStyleQuote",      // 480
        "MessageStyleCode",       // 481
        "MessageStyleMono"        // 482
    };
    
    // ============================================================
    // أنماط التنسيق (علامات Markdown)
    // %s هو مكان النص الأصلي
    // ============================================================
    public static final String[] STYLE_FORMATS = {
        "%s",           // default
        "**%s**",       // bold
        "__%s__",       // italic
        "~~%s~~",       // strikethrough
        "--%s--",       // underline
        "||%s||",       // spoiler
        "> %s",         // quote
        "`%s`",         // code
        "```\n%s\n```"  // mono
    };
    
    // ============================================================
    // الدوال (Methods)
    // ============================================================
    
    /**
     * تطبيق النمط على النص
     * @param text النص الأصلي
     * @param styleIndex رقم النمط المختار
     * @return النص بعد تطبيق التنسيق
     */
    public static String applyStyle(String text, int styleIndex) {
        if (TextUtils.isEmpty(text)) {
            return text;
        }
        
        // التأكد من أن الفهرس ضمن النطاق
        if (styleIndex < 0 || styleIndex >= STYLE_FORMATS.length) {
            return text;
        }
        
        // إذا كان النمط افتراضي، إرجاع النص كما هو
        if (styleIndex == STYLE_DEFAULT) {
            return text;
        }
        
        // تطبيق التنسيق
        return String.format(STYLE_FORMATS[styleIndex], text);
    }
    
    /**
     * تطبيق النمط مع التحقق من التنسيق الموجود
     * @param text النص الأصلي
     * @param styleIndex رقم النمط المختار
     * @return النص بعد تطبيق التنسيق
     */
    public static String applyStyleWithCheck(String text, int styleIndex) {
        if (TextUtils.isEmpty(text)) {
            return text;
        }
        
        if (styleIndex == STYLE_DEFAULT) {
            return text;
        }
        
        // التحقق إذا كان النص يحتوي بالفعل على تنسيق Markdown
        // حتى لا نطبق التنسيق مرتين
        if (text.contains("**") || text.contains("__") || text.contains("~~") || 
            text.contains("||") || text.contains("`") || text.contains("```") ||
            text.startsWith("> ")) {
            
            // التحقق إذا كان النص مغلفاً بالفعل بنفس النمط
            String formatted = applyStyle(text, styleIndex);
            if (formatted.equals(text)) {
                // إذا لم يتغير النص، يعني أن التنسيق موجود بالفعل
                return text;
            }
            return formatted;
        }
        
        return applyStyle(text, styleIndex);
    }
    
    /**
     * الحصول على اسم النمط مترجماً من MeeroStrings
     * @param styleIndex رقم النمط
     * @return الاسم المترجم
     */
    public static String getStyleName(int styleIndex) {
        if (styleIndex < 0 || styleIndex >= STYLE_KEYS.length) {
            return "Default";
        }
        return MeeroStrings.s(STYLE_KEYS[styleIndex]);
    }
    
    /**
     * الحصول على جميع أسماء الأنماط مترجمة
     * @return مصفوفة بأسماء الأنماط المترجمة
     */
    public static String[] getStyleNames() {
        String[] names = new String[STYLE_KEYS.length];
        for (int i = 0; i < STYLE_KEYS.length; i++) {
            names[i] = MeeroStrings.s(STYLE_KEYS[i]);
        }
        return names;
    }
    
    /**
     * الحصول على عدد الأنماط المتاحة
     * @return عدد الأنماط
     */
    public static int getStyleCount() {
        return STYLE_KEYS.length;
    }
    
    /**
     * التحقق مما إذا كان النمط مدعوماً للرسائل النصية فقط
     * @param styleIndex رقم النمط
     * @return true إذا كان النمط للرسائل النصية فقط
     */
    public static boolean isTextOnlyStyle(int styleIndex) {
        return styleIndex == STYLE_QUOTE || styleIndex == STYLE_CODE || styleIndex == STYLE_MONO;
    }

    // ============================================================
    // 🆕 دوال إنشاء الكيانات (Entities) للتنسيق
    // ============================================================

    /**
     * إنشاء كيان تنسيق حسب النمط المختار
     * @param styleIndex رقم النمط
     * @param start بداية النص
     * @param length طول النص
     * @return كيان التنسيق المناسب
     */
    public static TLRPC.MessageEntity createEntity(int styleIndex, int start, int length) {
        switch (styleIndex) {
            case STYLE_BOLD:
                TLRPC.TL_messageEntityBold bold = new TLRPC.TL_messageEntityBold();
                bold.offset = start;
                bold.length = length;
                return bold;
            case STYLE_ITALIC:
                TLRPC.TL_messageEntityItalic italic = new TLRPC.TL_messageEntityItalic();
                italic.offset = start;
                italic.length = length;
                return italic;
            case STYLE_STRIKE:
                TLRPC.TL_messageEntityStrike strike = new TLRPC.TL_messageEntityStrike();
                strike.offset = start;
                strike.length = length;
                return strike;
            case STYLE_UNDERLINE:
                TLRPC.TL_messageEntityUnderline underline = new TLRPC.TL_messageEntityUnderline();
                underline.offset = start;
                underline.length = length;
                return underline;
            case STYLE_SPOILER:
                TLRPC.TL_messageEntitySpoiler spoiler = new TLRPC.TL_messageEntitySpoiler();
                spoiler.offset = start;
                spoiler.length = length;
                return spoiler;
            case STYLE_QUOTE:
                TLRPC.TL_messageEntityBlockquote quote = new TLRPC.TL_messageEntityBlockquote();
                quote.offset = start;
                quote.length = length;
                quote.flags |= 1;  // collapsed = true
                return quote;
            case STYLE_CODE:
                TLRPC.TL_messageEntityCode code = new TLRPC.TL_messageEntityCode();
                code.offset = start;
                code.length = length;
                return code;
            case STYLE_MONO:
                TLRPC.TL_messageEntityPre pre = new TLRPC.TL_messageEntityPre();
                pre.offset = start;
                pre.length = length;
                pre.language = "";
                return pre;
            default:
                return null;
        }
    }

    /**
     * تطبيق النمط باستخدام الكيانات (بدون تعديل النص)
     * @param entities قائمة الكيانات الموجودة
     * @param styleIndex رقم النمط
     * @param textLength طول النص
     * @return قائمة الكيانات بعد الإضافة
     */
    public static ArrayList<TLRPC.MessageEntity> applyStyleWithEntity(ArrayList<TLRPC.MessageEntity> entities, int styleIndex, int textLength) {
        if (styleIndex == STYLE_DEFAULT || textLength <= 0) {
            return entities;
        }
        
        if (entities == null) {
            entities = new ArrayList<>();
        }
        
        TLRPC.MessageEntity entity = createEntity(styleIndex, 0, textLength);
        if (entity != null) {
            entities.add(entity);
        }
        
        return entities;
    }
}
