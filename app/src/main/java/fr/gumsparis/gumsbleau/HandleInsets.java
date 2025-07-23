package fr.gumsparis.gumsbleau;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public final class HandleInsets {
   static Insets myInsets;
    private HandleInsets() {};
    public static void placeInsets(Activity activity, View conteneur) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Window window = activity.getWindow();
            window.setDecorFitsSystemWindows(false);
            ViewCompat.setOnApplyWindowInsetsListener(conteneur, new OnApplyWindowInsetsListener() {
                @NonNull
                @Override
                public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                    myInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(
                            myInsets.left,
                            myInsets.top,
                            myInsets.right,
                            myInsets.bottom);
                    return (insets);
                }
            });
        }
        ViewCompat.requestApplyInsets(conteneur);
    }
}
