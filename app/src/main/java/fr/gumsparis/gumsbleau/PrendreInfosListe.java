package fr.gumsparis.gumsbleau;

import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static fr.gumsparis.gumsbleau.MainActivity.DATELISTE;

public class PrendreInfosListe extends PrendreInfosGums {

// cette classe rajoute le onPostExecute pour la tâche asynchrone de récupération de la liste des sorties auprès de
// gumsparis. On range le json dans les prefs puis on appelle getListe pour le décoder et nourrir les LiveData.

    @Override
    protected void onPostExecute (String result) {
        if (BuildConfig.DEBUG){
        Log.i("GUMSBLO", "onPostde liste"+result);}
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
}
