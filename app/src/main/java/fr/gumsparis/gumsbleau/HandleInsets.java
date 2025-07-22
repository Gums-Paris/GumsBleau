package fr.gumsparis.gumsbleau;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public final class HandleInsets {
    static int topInset, bottomInset = 30;
    private HandleInsets() {}
    public static void placeInsets(Activity activity, Toolbar toolbar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Window window = activity.getWindow();
            window.setDecorFitsSystemWindows(false);
            ViewCompat.setOnApplyWindowInsetsListener(toolbar, new OnApplyWindowInsetsListener() {
                @NonNull
                @Override
                public WindowInsetsCompat onApplyWindowInsets(@NonNull View toolbar, @NonNull WindowInsetsCompat insets) {
                    topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
                    toolbar.setPadding(10,topInset,10,20);
                    return (insets);
                }
            });
            //        toolbar.setPadding(0,topInset, 0,0);
            //       return insets;
            ViewCompat.requestApplyInsets(toolbar);
        }
    }

}
