package fr.gumsparis.gumsbleau;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static fr.gumsparis.gumsbleau.MainActivity.LIEU;
import static fr.gumsparis.gumsbleau.MainActivity.DATE;
import static fr.gumsparis.gumsbleau.MainActivity.ITIPARK;
import static fr.gumsparis.gumsbleau.MainActivity.ITIRDV;
import static fr.gumsparis.gumsbleau.MainActivity.FLAG;

public class ModelBleauInfo extends AndroidViewModel {

    static MutableLiveData<String[]> infosSortie = new MutableLiveData<>();
    static MutableLiveData<String>flagInfo = new MutableLiveData<>();
    static MutableLiveData<String>lieuSortie = new MutableLiveData<>();
    static MutableLiveData<String>dateSortie = new MutableLiveData<>();
    private static final String urlContact ="https://v2.gumsparis.asso.fr/index.php?option=com_gblo&view=prochsortie&format=json";


    // Constructeur du modèle ; récupère les infos auprès de gumsparis ou des Shared Preferences
    public ModelBleauInfo(@NonNull Application application) {
        super(application);
        Log.i("GUMSBLO", "constructeur");
// création de l'instance de MyHelper qui va stocker le contexte de l'application. Cela permettra de récupérer le context et donc
// en particulier les préférences de n'importe où en récupérant l'instance sans avoir à passer de contexte
        SharedPreferences mesPrefs = MyHelper.getInstance(application.getApplicationContext()).recupPrefs();

// s'il n'y a pas d'info ou si l'info est périmée on va la chercher sur gumsparis, sinon on la sort des SharedPrefs
        if (mesPrefs.getString(DATE,null) == null || datePast(mesPrefs.getString(DATE,null))) {
            if (isNetworkReachable()) {
                new PrendreInfosGums().execute(urlContact);
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
        dateSortie.setValue(mesPrefs.getString(DATE, null));
        iti[0] = mesPrefs.getString(ITIPARK, null);
        iti[1] = mesPrefs.getString(ITIRDV, null);
        infosSortie.setValue(iti);
        flagInfo.setValue(mesPrefs.getString(FLAG, null));
    }

// teste la disponibilité d'un accès réseau ; noter que NetworkInfo est déprécié dans l'API 29
    private boolean isNetworkReachable() {
        ConnectivityManager cM = (ConnectivityManager) getApplication().getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cM.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            Log.i("GUMSBLO", "nous sommes connectés");
            return true;
        }
        return false;
    }

// return true si uneDate est antérieure à la date du jour
    private boolean datePast(String uneDate) {
        Date date1 = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            date1 = sdf.parse(uneDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Date date2 = Calendar.getInstance().getTime();
        try {
            return (date2.compareTo(date1) >= 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return true;
    }

}