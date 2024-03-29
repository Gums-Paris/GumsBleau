package fr.gumsparis.gumsbleau;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import static fr.gumsparis.gumsbleau.MainActivity.DATELISTE;

public class ModelBleauListe extends AndroidViewModel {

    static MutableLiveData<ArrayList<String>> nomLieu = new MutableLiveData<>();
    static MutableLiveData<ArrayList<String>> idArticle = new MutableLiveData<>();
    static SingleLiveEvent<Boolean> flagListe = new SingleLiveEvent<>();


    public ModelBleauListe (Application application) {
        super(application);
        SharedPreferences mesPrefs = MyHelper.getInstance().recupPrefs();
        String urlContact = UrlsGblo.LISTE.getUrl();

// si la liste de sortie sauvegardée a plus d'une semaine on la redemande à gumsparis, sinon on sort la liste des prefs
        if (mesPrefs.getString(DATELISTE,null) == null || Aux.datePast(mesPrefs.getString(DATELISTE,null), 7)) {
                Aux.recupInfo(urlContact, "liste");
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
        }else{
            flagListe.setValue(false);
        }
    }
}
