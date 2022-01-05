package fr.gumsparis.gumsbleau;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import static fr.gumsparis.gumsbleau.MainActivity.DATELISTE;

public class ModelBleauListe extends AndroidViewModel {

    static MutableLiveData<ArrayList<String>> nomLieu = new MutableLiveData<>();
    static MutableLiveData<ArrayList<String>> idArticle = new MutableLiveData<>();
    static MutableLiveData<Boolean> flagListe = new MutableLiveData<>();


    public ModelBleauListe (Application application) {
        super(application);
        SharedPreferences mesPrefs = MyHelper.getInstance().recupPrefs();
        String urlContact = UrlsGblo.LISTE.getUrl();

// si la liste de sortie sauvegardée a plus d'une semaine on la redemande à gumsparis, sinon on sort la liste des prefs
        if (mesPrefs.getString(DATELISTE,null) == null || Aux.datePast(mesPrefs.getString(DATELISTE,null), 7)) {
            if (Variables.isNetworkConnected) {

  //              new PrendreInfosListe().execute(urlContact);
                Aux.recupInfo(urlContact, "liste");
            }
        } else {
            getListeFromPrefs();
        }
    }

    MutableLiveData<ArrayList<String>> getListeLieux() {
        return nomLieu;
    }

    MutableLiveData<ArrayList<String>> getListeArticles() {
        return idArticle;
    }

    MutableLiveData<Boolean> getFlagListe() {
        return flagListe;
    }

// récupérer le json de la liste sauvegardé dans les prefs et le décoder
    private void getListeFromPrefs() {
        SharedPreferences mesPrefs = MyHelper.getInstance().recupPrefs();
        if (mesPrefs.getString("jsonListe", null) != null) {
            Aux.getListe(mesPrefs.getString("jsonListe", null));
        }
    }
}
