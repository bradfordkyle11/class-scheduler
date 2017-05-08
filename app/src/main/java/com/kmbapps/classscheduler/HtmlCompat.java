package com.kmbapps.classscheduler;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

/**
 * Created by Kyle on 5/8/2017.
 */

public class HtmlCompat {
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }
}
