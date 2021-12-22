package fr.gumsparis.gumsbleau;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import android.util.Log;

import static fr.gumsparis.gumsbleau.MainActivity.LIEU;
import static fr.gumsparis.gumsbleau.MainActivity.DATERV;
import static fr.gumsparis.gumsbleau.MainActivity.ITIPARK;
import static fr.gumsparis.gumsbleau.MainActivity.ITIRDV;
import static fr.gumsparis.gumsbleau.MainActivity.FLAG;

public class ModelBleauInfo extends AndroidViewModel {

    static MutableLiveData<String[]> infosSortie = new MutableLiveData<>();
    static MutableLiveData<String>flagInfo = new MutableLiveData<>();
    static MutableLiveData<String>lieuSortie = new MutableLiveData<>();
    static MutableLiveData<String>dateSortie = new MutableLiveData<>();

    // Constructeur du modèle ; récupère les infos auprès de gumsparis ou des Shared Preferences
    public ModelBleauInfo(@NonNull Application application) {
        super(application);
        if (BuildConfig.DEBUG){
        Log.i("GUMSBLO", "constructeur");}

        SharedPreferences mesPrefs = MyHelper.getInstance().recupPrefs();
        SharedPreferences.Editor editeur = mesPrefs.edit();
        boolean choixSortie = false;

// s'il y a eu choix de sortie ou n'y a pas d'info ou si l'info est périmée on va la chercher sur gumsparis,
// sinon on la sort des SharedPrefs
        String urlContact = UrlsGblo.SORTIE.getUrl();
        String sortieChoisie = mesPrefs.getString("sortiechoisie", null);
        if (BuildConfig.DEBUG){
            Log.i("GUMSBLO", "sortie choisie "+sortieChoisie);}
        if ((sortieChoisie!=null)&&!(sortieChoisie).isEmpty()) {
            choixSortie = true;
            urlContact = urlContact + "&idarticle=" + mesPrefs.getString("sortiechoisie", null);
            editeur.putString("sortiechoisie", "");
            editeur.apply();
         }
        if (BuildConfig.DEBUG){
        Log.i("GUMSBLO", "choixSortie = "+choixSortie);
        Log.i("GUMSBLO", "DATERV = "+mesPrefs.getString(DATERV, ""));
        Log.i("GUMSBLO", "peremption =  "+Aux.datePast(mesPrefs.getString(DATERV,""), 1));}

        if (choixSortie || mesPrefs.getString(DATERV,null) == null || Aux.datePast(mesPrefs.getString(DATERV,null), 1)) {
            if (Variables.isNetworkConnected) {
                new PrendreInfosSortie().execute(urlContact);
            }
        } else {
            getInfosFromPrefs();
        }
    }

// getter pour renvoyer la valeur de la LiveData infos
    MutableLiveData<String[]> getInfosSortie() {
        return infosSortie;
    }

// getter pour le flag
    MutableLiveData<String> getFlagInfos() {
        return flagInfo;
    }

    // getter pour le lieu
    MutableLiveData<String> getLieuSortie() {
        return lieuSortie;
    }

    // getter pour la date de la sortie
    MutableLiveData<String> getDateSortie() {
        return dateSortie;
    }


// pour extraire les données qui étaient sauvegardées dans SharedPreferences et les mettre en LiveData
    private void getInfosFromPrefs() {
        SharedPreferences mesPrefs = MyHelper.getInstance().recupPrefs();
        String[] iti = new String[2];
        lieuSortie.setValue(mesPrefs.getString(LIEU, null));
        dateSortie.setValue(mesPrefs.getString(DATERV, null));
        iti[0] = mesPrefs.getString(ITIPARK, null);
        iti[1] = mesPrefs.getString(ITIRDV, null);
        infosSortie.setValue(iti);
        flagInfo.setValue(mesPrefs.getString(FLAG, null));
    }


}