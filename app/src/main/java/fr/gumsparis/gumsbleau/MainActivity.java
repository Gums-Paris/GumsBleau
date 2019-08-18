package fr.gumsparis.gumsbleau;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    ModelBleauInfo manipsInfo = null;
    TextView lieuSortie = null;
    TextView dateSortie = null;
    TextView parking = null;
    TextView rendezvous = null;
    TextView commentNav = null;
    TextView commentRdv = null;
    Button boutonPark = null;
    Button boutonRdV = null;
    SharedPreferences mesPrefs;
    final static String PREF_FILE = "mesInfos";
    ProgressBar patience = null;
    static final String LIEU = "title";
    static final String DATE = "dates";
    static final String ITIPARK = "itiPark";
    static final String LATPARK = "latPark";
    static final String LONPARK = "lonPark";
    static final String ITIRDV = "itiRdV";
    static final String LATRDV = "latRdV";
    static final String LONRDV = "lonRdV";
    static final String FLAG = "flag";
    static final String APPLINAV = "appnav";
    static final String APPLICARTO = "appcarto";
    boolean flagGPX = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lieuSortie = findViewById(R.id.lieusortie);
        dateSortie = findViewById(R.id.datesortie);
        parking = findViewById(R.id.itipark);
        commentNav = findViewById(R.id.infonav);
        commentNav.setText(getString(R.string.info_nav));
        rendezvous = findViewById(R.id.itirdv);
        commentRdv = findViewById(R.id.infordv);
        commentRdv.setText(getString(R.string.info_rdv));
        boutonPark = findViewById(R.id.buttonnav);
        boutonRdV = findViewById(R.id.buttonrdv);
        patience = findViewById(R.id.indeterminateBar);

// Mettre les applis par défaut pour nav et carto dans les préférences s'il n'y en a pas
        mesPrefs = getApplicationContext().getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editeur = mesPrefs.edit();
        if (mesPrefs.getString(APPLINAV, null) == null) {
            editeur.putString(APPLINAV, getString(R.string.gmp));
            editeur.apply();
        }
        if (mesPrefs.getString(APPLICARTO, null) == null) {
            editeur.putString(APPLICARTO, getString(R.string.ifi));
            editeur.apply();
        }

// patience est un cercle qui tourne jusqu'à disponibilité des infos pour faire patienter le client...
        patience.setVisibility(View.VISIBLE);

// instanciation ou récupération du ViewModel qui gère les données
        manipsInfo = ViewModelProviders.of(this).get(ModelBleauInfo.class);
        Log.i("GUMSBLO", "modèle créé");

// création de l'observateur et établissement du lien de l'observateur avec la LiveData du flag
        final Observer<String> flagObserver = new Observer<String>() {
            @Override
            public void onChanged(String newFlag) {
//                patience.setVisibility(View.GONE);
                if (!newFlag.equals("0")) {
                    alerte(newFlag);
                }
            }
        };
        manipsInfo.getFlagInfos().observe(MainActivity.this, flagObserver);

// puis l'observateur des itinéraires parking et RdV
        final Observer<String[]> valueObserver = new Observer<String[]>() {
            @Override
            public void onChanged(String[] newVal) {
                String iP = newVal[0];
                String iR = newVal[1];
// on fait dsparaître la roue de patience et on affiche les itinéraires
                patience.setVisibility(View.GONE);
                if (iP != null) {
                    String itp = getString(R.string.iti_park)+iP;
                    parking.setBackgroundColor(getResources().getColor(R.color.colorItiPark));
                    parking.setText(Html.fromHtml(itp));
                }
                if (iR != null) {
                    rendezvous.setBackgroundColor(getResources().getColor(R.color.colorItiRdV));
                    rendezvous.setText(Html.fromHtml(getString(R.string.iti_rdv)+iR));
                }
            }
        };
        manipsInfo.getInfosSortie().observe(MainActivity.this, valueObserver);

// puis l'observateur du lieu de lasortie
        final Observer<String> lieuObserver = new Observer<String>() {
            @Override
            public void onChanged(String newLieu) {
                if (newLieu != null) {
                    String sl = "<b><big>"+newLieu+"</big></b>";
                    lieuSortie.setTextColor(getResources().getColor(R.color.rougeGums));
                    lieuSortie.setText(Html.fromHtml(sl));
                }
            }
        };
        manipsInfo.getLieuSortie().observe(MainActivity.this, lieuObserver);

// puis l'observateur de la date de la sortie
        final Observer<String> dateObserver = new Observer<String>() {
            @Override
            public void onChanged(String newDate) {
                if (newDate != null) {
                    String sd = "<b><big>"+newDate+"</big></b>";
                    dateSortie.setTextColor(getResources().getColor(R.color.rougeGums));
                    dateSortie.setText(Html.fromHtml(sd));
                }
            }
        };
        manipsInfo.getDateSortie().observe(MainActivity.this, dateObserver);

// enfin, les deux boutons

        boutonPark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* pour envoi intent à l'appli nav : itinéraire jusqu'au parking*/
                String appli = mesPrefs.getString(APPLINAV, getString(R.string.gmp));
                String laP = mesPrefs.getString(LATPARK, null);
                String LoP = mesPrefs.getString(LONPARK, null);
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + laP + "," + LoP);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage(appli);
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Appli de navigation non disponible", Toast.LENGTH_LONG).show();
                }
            }
        });

        boutonRdV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* pour envoi intent à l'appli carto : position du rendez-vous*/
                String appli = mesPrefs.getString(APPLICARTO, getString(R.string.ifi));
                String laR = mesPrefs.getString(LATRDV, null);
                String LoR = mesPrefs.getString(LONRDV, null);
                Uri cartoIntentUri = null;
                Intent cartoIntent = null;
                cartoIntentUri = Uri.parse("geo:" + laR + "," + LoR);
                cartoIntent = new Intent(Intent.ACTION_VIEW, cartoIntentUri);
                Log.i("GUMSBLO", "Intent= " + cartoIntent.toString());
                cartoIntent.setPackage(appli);
                if (cartoIntent.resolveActivity(getPackageManager()) != null) {
                    Log.i("GUMSBLO", "6 lancement carto");
                    startActivity(cartoIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Appli de carte topo non disponible", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

//affichage dialogue d'alerte si problème de disponibilité des infos
    protected void alerte(String flag) {
        String message = "";
        switch (flag) {
            case "1" :
                message = getString(R.string.infos_partielles);
                break;
            case "2" :
                message = getString(R.string.pas_infos);
                break;
            case "3" :
                message = getString(R.string.pas_contact);
        }
        DialogAlertes infoUtilisateur = DialogAlertes.newInstance(message);
        infoUtilisateur.show(getSupportFragmentManager(), "infoutilisateur");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.help) {
            Intent lireAide = new Intent(MainActivity.this, Aide.class);
            startActivity(lireAide);
            return true;
        }
        if (id == R.id.action_settings) {
            Intent choisirPrefs = new Intent(MainActivity.this, ChoixApplis.class);
            startActivity(choisirPrefs);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
