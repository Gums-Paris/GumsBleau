package fr.gumsparis.gumsbleau;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


class Aux {

    @SuppressWarnings("deprecation")
    static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= 24) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    /*    static boolean isNetworkReachable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }  */

    static void isNetworkReachable() {
        ConnectivityManager connectivityManager
                = MyHelper.getInstance().conMan();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Log.i("GUMSBLO", "SDK < Q" );
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            Variables.isNetworkConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }else {
            try {
                connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                       @Override
                       public void onAvailable(@NonNull Network network) {
                           Log.i("GUMSBLO", "on available " );
                           Variables.isNetworkConnected = true; // Global Static Variable
                       }
                       @Override
                       public void onLost(@NonNull Network network) {
                           Log.i("GUMSBLO", "on lost " );
                           Variables.isNetworkConnected = false; // Global Static Variable
                       }
                   }
                );
            } catch (Exception e) {
                Variables.isNetworkConnected = false;
            }
        }
    }

    // return true si uneDate est antérieure à la date du jour diminuée de ageMax (en jours)
    static boolean datePast(String uneDate, int ageMax) {
        Date date1 = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            date1 = sdf.parse(uneDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, (ageMax * -1));
        Date date2 = c.getTime();
        try {
            if (BuildConfig.DEBUG){
            Log.i("GUMSBLO", "date info = "+uneDate);
            Log.i("GUMSBLO", "date2/date1 = "+date2.compareTo(date1));
            Log.i("GUMSBLO", "date jour = "+date2);}
            return (date2.compareTo(date1) >= 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return true;
    }

 // pour remplir les LiveData nomLieu et idArticle en décodant le json jsListe contenant la liste de sorties
    static void getListe (String jsListe) {
        ArrayList<String> listeLieu = new ArrayList<>();
        ArrayList<String> listeArticle = new ArrayList<>();
        listeLieu.add("Automatique");
        listeArticle.add("");

        try {
            JSONObject jsonGums = new JSONObject(jsListe);
            JSONArray arrayGums = jsonGums.getJSONArray("liste");
            for (int i = 0; i < arrayGums.length(); i++) {
                JSONArray uneSortie = arrayGums.getJSONArray(i);
                listeLieu.add(uneSortie.optString(0));
                listeArticle.add(uneSortie.optString(1));
            }
            ModelBleauListe.nomLieu.setValue(listeLieu);
            ModelBleauListe.idArticle.setValue(listeArticle);
            ModelBleauListe.flagListe.setValue(true);
        } catch (JSONException e) {
            ModelBleauListe.flagListe.setValue(false);
            e.printStackTrace();
        }
    }

}
