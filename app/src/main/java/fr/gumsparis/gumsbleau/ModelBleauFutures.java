package fr.gumsparis.gumsbleau;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class ModelBleauFutures extends AndroidViewModel {

    static MutableLiveData<ArrayList<Sortie>> listeFutures = new MutableLiveData<>();
    static SingleLiveEvent<Boolean> flagFutures = new SingleLiveEvent<>();

/* On récupère la liste des sorties futures par internet. On ne la range pas en prefs
   pour pouvoir l'afficher en cas d'absence de réseau parce que ça n'aurait guère de sens
   de ne pas afficher la liste en cours de validité */
    public ModelBleauFutures (Application application) {
        super(application);
         String urlContact = UrlsGblo.LISTEFUTURES.getUrl();
        Aux.recupInfo(urlContact, "listefutures");
    }

    MutableLiveData<ArrayList<Sortie>> getListeFutures() {
        return listeFutures;
    }
    MutableLiveData<Boolean> getflagFutures() {
        return flagFutures;
    }
}
