package fr.gumsparis.gumsbleau;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.CONNECTIVITY_SERVICE;

class Aux {

    // teste la disponibilité d'un accès réseau ; noter que NetworkInfo est déprécié dans l'API 29
    static  boolean isNetworkReachable() {
        ConnectivityManager cM = MyHelper.getInstance().conMan();
        NetworkInfo networkInfo = cM.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            Log.i("GUMSBLO", "nous sommes connectés");
            return true;
        }
        return false;
    }

    // return true si uneDate est antérieure à la date du jour diminuée de ageMax (en jour)
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
            return (date2.compareTo(date1) >= 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return true;
    }

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
