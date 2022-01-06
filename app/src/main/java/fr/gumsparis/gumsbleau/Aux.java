package fr.gumsparis.gumsbleau;

import static fr.gumsparis.gumsbleau.MainActivity.DATELISTE;
import static fr.gumsparis.gumsbleau.MainActivity.DATERV;
import static fr.gumsparis.gumsbleau.MainActivity.FLAG;
import static fr.gumsparis.gumsbleau.MainActivity.ITIPARK;
import static fr.gumsparis.gumsbleau.MainActivity.ITIRDV;
import static fr.gumsparis.gumsbleau.MainActivity.LATPARK;
import static fr.gumsparis.gumsbleau.MainActivity.LATRDV;
import static fr.gumsparis.gumsbleau.MainActivity.LIEU;
import static fr.gumsparis.gumsbleau.MainActivity.LONPARK;
import static fr.gumsparis.gumsbleau.MainActivity.LONRDV;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
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
import java.util.HashMap;
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

/* Pour fonctionner avec API 23
* https://medium.com/dsc-alexandria/implementing-internet-connectivity-checker-in-android-apps-bf28230c4e86 */
    public static boolean isInternetOK() {
        ConnectivityManager cm = MyHelper.getInstance().conMan();
        if (cm == null) return false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Log.i("GUMSBLO", "SDK < Q" );
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
            return ( activeNetworkInfo != null && activeNetworkInfo.isConnected());
        }else {
            Network[] networks = cm.getAllNetworks();
            for (Network n : networks) {
                NetworkCapabilities cap = cm.getNetworkCapabilities(n);
                return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
        }
        return false;
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

// démarrer la tâche de récup des données sur gumsparis ; ici on ne passe qu'un seul paramètre, l'URL mais
// dans GumsSki la classe RecupInfosGums a besoin de 6 paramètres   d'où le tableau taskParams.
    static void recupInfo (String url, String motif) {
        TaskRunner taskRunner = TaskRunner.getInstance();
        final String[] taskParams = new String[6];
        taskParams[0] = url;
        Log.i("GUMSBLO", "taskParams[0] = "+taskParams[0]);
        if (BuildConfig.DEBUG){
            Log.i("GUMSBLO", "network ? "+Variables.isNetworkConnected);}
        if (Variables.isNetworkConnected)  {
            if ("liste".equals(motif)) {
    // pour récupérer la lliste de sorties
                taskRunner.executeAsync(new RecupInfosGums(taskParams), Aux::decodeInfosListe);
                }else{
    //pour récupérer les infos d'une sortie
                taskRunner.executeAsync(new RecupInfosGums(taskParams), Aux::decodeInfosSortie);
                }
        }else{
            if ("liste".equals(motif)) {
                ModelBleauListe.flagListe.setValue(false);
            }else{
                ModelBleauInfo.flagInfo.setValue("3");
            }
        }
    }

//extraire infos de sortie du json
    static void decodeInfosSortie(String result){
        if (BuildConfig.DEBUG){
            Log.i("GUMSBLO", "dans decode infos sortie");}
        String[] itis = new String[2];
        String lieu = "";
        String date = "";
        String flag = "2";
        SharedPreferences mesPrefs = MyHelper.getInstance().recupPrefs();
        SharedPreferences.Editor  editeur = mesPrefs.edit();

        if ("netOUT".equals(result)) {
            ModelBleauInfo.flagInfo.setValue("3");
        } else {
            try {
                JSONObject jInfos = new JSONObject(result);
                if (jInfos.opt(ITIPARK) != null) {
                    itis[0] = (String) jInfos.opt(ITIPARK);
                    editeur.putString(ITIPARK, itis[0]);
                }
                if (jInfos.opt(ITIRDV) != null) {
                    itis[1] = (String) jInfos.opt(ITIRDV);
                    editeur.putString(ITIRDV, itis[1]);
                }
                if (jInfos.opt(LIEU) != null) {
                    lieu = (String) jInfos.opt(LIEU);
                    editeur.putString(LIEU, lieu);
                }
                if (jInfos.opt(DATERV) != null) {
                    date = (String) jInfos.opt(DATERV);
                    if (mesPrefs.getBoolean("auto", true)) {
                        editeur.putString(DATERV, date); }
                }
                if (jInfos.opt(LATPARK) != null) {
                    editeur.putString(LATPARK, (String) jInfos.opt(LATPARK));
                }
                if (jInfos.opt(LONPARK) != null) {
                    editeur.putString(LONPARK, (String) jInfos.opt(LONPARK));
                }
                if (jInfos.opt(LATRDV) != null) {
                    editeur.putString(LATRDV, (String) jInfos.opt(LATRDV));
                }
                if (jInfos.opt(LONRDV) != null) {
                    editeur.putString(LONRDV, (String) jInfos.opt(LONRDV));
                }
                if (jInfos.opt(FLAG) != null) {
                    flag = (String) jInfos.opt(FLAG);
                    editeur.putString(FLAG, flag);
                }
                editeur.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ModelBleauInfo.flagInfo.setValue(flag);
            ModelBleauInfo.infosSortie.setValue(itis);
            ModelBleauInfo.lieuSortie.setValue(lieu);
            ModelBleauInfo.dateSortie.setValue(date);
//                System.exit(0);
        }
    }

//extraire la liste du json
    static void decodeInfosListe(String result){
        if (BuildConfig.DEBUG){
            Log.i("GUMSBLO", "decode infos liste"+result);}
        SharedPreferences mesPrefs = MyHelper.getInstance().recupPrefs();
        SharedPreferences.Editor  editeur = mesPrefs.edit();
        if (result.equals("netOUT")) {
            ModelBleauListe.flagListe.setValue(false);
        } else {
            editeur.putString("jsonListe", result);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date today = Calendar.getInstance().getTime();
                editeur.putString(DATELISTE, sdf.format(today));
                editeur.apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Aux.getListe(result);
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

    // test égalité de chaînes. Cette version considère que (null == null) est false
    static boolean egaliteChaines(String ch1, String ch2) {
        return (ch1 != null && ch1.equals(ch2));
    }

    // teste si la chaîne str est vide ou null
    public static boolean isEmptyString(String str) {
        return (str == null || str.isEmpty());
    }

}
