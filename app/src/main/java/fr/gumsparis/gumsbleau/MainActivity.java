package fr.gumsparis.gumsbleau;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

import java.io.File;


public class MainActivity extends AppCompatActivity  {

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
    SharedPreferences.Editor editeur;
    final static String PREF_FILE = "mesInfos";
    ProgressBar patience = null;
    static final String LIEU = "title";
    static final String DATERV = "dates";
    static final String ITIPARK = "itiPark";
    static final String LATPARK = "latPark";
    static final String LONPARK = "lonPark";
    static final String ITIRDV = "itiRdV";
    static final String LATRDV = "latRdV";
    static final String LONRDV = "lonRdV";
    static final String FLAG = "flag";
    static final String APPLICARTO = "appcarto";
    static final String DATELISTE = "dateliste";
    boolean flagGPX = true;
    String sortieChoisie = "";
    NetworkConnectionMonitor connectionMonitor;
    File mFile = null;
    static final private String LATLON_RV = "latlon.gpx";
    View conteneur =null;


// TODO
//  rien en cours

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
 //Adaptation au fonctionnement EdgeToEdge
        conteneur = findViewById(R.id.home);
                      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            EdgeToEdge.enable(this);
       //     statusBarColor(conteneur,R.color.colorPrimaryDark);
            HandleInsets.placeInsets(this, conteneur);
        }

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

// verif internet OK et mise en place de la surveillance réseau qui sera activée dans onResume
// avec la bidouille "conman" pour avoir l'état du réseau avant la création du modèle (qui va charger les données)
// sinon la vérif n'a lieu que dans onResume
        connectionMonitor = NetworkConnectionMonitor.getInstance(getApplicationContext());
        ConnectivityManager conMan = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        Variables.isNetworkConnected = connectionMonitor.checkConnection(conMan);
        connectionMonitor.observe(this, isConnected -> {
            if (BuildConfig.DEBUG) Log.i("GUMSBLO", "main conmon observe "+isConnected);
            Variables.isNetworkConnected = isConnected;
        });

// au passage, création de l'instance de MyHelper qui va stocker le contexte de l'application. Cela permettra de
// récupérer le context et donc en particulier les préférences de n'importe où en récupérant l'instance sans avoir
//   à passer de contexte
        mesPrefs =  MyHelper.getInstance(getApplicationContext()).recupPrefs();
        editeur = mesPrefs.edit();
// initialiser le choix d'appli de navigation à "ne pas offrir le choix"
        if(!mesPrefs.contains("chooser")){
            editeur.putString("chooser", "no");
            editeur.apply();
        }

// initialiser le paramètre de choix appli de navigation si nécessaire
        if (mesPrefs.getString("chooser", null) == null) {
            editeur.putString("chooser", "no");
            editeur.apply();
        }

// Mettre l'appli par défaut pour carto dans les préférences s'il n'y en a pas
        if (mesPrefs.getString(APPLICARTO, null) == null) {
            editeur.putString(APPLICARTO, getString(R.string.ifi));
            editeur.apply();
        }

//initialise le flag "automatique" our choix de sortie
        editeur.putBoolean("auto", true);
        editeur.putString("sortiechoisie", null);
        editeur.apply();

// récup intent de démarrage
        Intent intent = getIntent();
        handleIntent(intent);

// patience est un cercle qui tourne jusqu'à disponibilité des infos pour faire patienter le client...
        patience.setVisibility(View.VISIBLE);

// instanciation ou récupération du ViewModel qui gère les données
        manipsInfo = new ViewModelProvider(this).get(ModelBleauInfo.class);
//        if (BuildConfig.DEBUG) Log.i("GUMSBLO", "getinfo 1 =");
        manipsInfo.trouverInfos();

// création de l'observateur et établissement du lien de l'observateur avec la LiveData du flag
        final Observer<String> flagObserver = newFlag -> {
            if (BuildConfig.DEBUG) Log.i("GUMSBLO", "obsflag =  "+newFlag);
            if (!"0".equals(newFlag)) {
                alerte(newFlag);
            }
        };
        manipsInfo.getFlagInfos().observe(MainActivity.this, flagObserver);

// puis l'observateur des itinéraires parking et RdV
        final Observer<String[]> valueObserver = newVal -> {
            String iP = newVal[0];
            String iR = newVal[1];
// on fait dsparaître la roue de patience et on affiche les itinéraires
            patience.setVisibility(View.GONE);
            if (iP != null) {
                String itp = getString(R.string.iti_park)+iP;
                parking.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorItiPark));
                parking.setText(Aux.fromHtml(itp));
            }
            if (iR != null) {
                rendezvous.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorItiRdV));
                rendezvous.setText(Aux.fromHtml(getString(R.string.iti_rdv)+iR));
                rendezvous.setMovementMethod(LinkMovementMethod.getInstance());
            }
        };
        manipsInfo.getInfosSortie().observe(MainActivity.this, valueObserver);

// puis l'observateur du lieu de lasortie
        final Observer<String> lieuObserver = newLieu -> {
            if (newLieu != null) {
                String sl = "<b><big>"+newLieu+"</big></b>";
                lieuSortie.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.rougeGums));
                lieuSortie.setText(Aux.fromHtml(sl));
            }
        };
        manipsInfo.getLieuSortie().observe(MainActivity.this, lieuObserver);

// puis l'observateur de la date de la sortie
        final Observer<String> dateObserver = newDate -> {
            if (newDate != null) {
                String sd = "<b><big>"+newDate+"</big></b>";
                dateSortie.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.rougeGums));
                dateSortie.setText(Aux.fromHtml(sd));
            }
        };
        manipsInfo.getDateSortie().observe(MainActivity.this, dateObserver);

// créer fichier latlon.gpx pour les besoins de Iphigénie (août 2019)
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            mFile = new File(this.getExternalFilesDir(null), LATLON_RV);
            if (BuildConfig.DEBUG){
                Log.i("GUMSBLO", "1  fichier "+mFile);}
        }else{flagGPX = false;}

// enfin, les deux boutons

        boutonPark.setOnClickListener((View v) -> {
            // pour envoi intent à l'appli nav : itinéraire jusqu'au parking si coordonnées OK
            String laP = mesPrefs.getString(LATPARK, null);
            String LoP = mesPrefs.getString(LONPARK, null);
            if (!("".equals(laP)) && !("".equals(LoP))) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + laP + "," + LoP);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                String choisir = mesPrefs.getString("chooser", null);
                if ("yes".equals(choisir)) {
                    String titre = "Choisir une appli de guidage routier";
                    Intent chooser = Intent.createChooser(mapIntent, titre);
                   if (chooser.resolveActivity(getPackageManager()) != null) {
                            startActivity(chooser);
                        }
                } else if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Appli de navigation non disponible", Toast.LENGTH_LONG).show();
                }
            }else{
                alerte("noPK");
            }
        });

        boutonRdV.setOnClickListener((View v) -> {
            // pour envoi intent à l'appli carto : position du rendez-vous si coordonnées OK
            String appli = mesPrefs.getString(APPLICARTO, getString(R.string.ifi));
            if (BuildConfig.DEBUG) {
                Log.i("GUMSBLO", "appli carto " + appli);
            }
            String laR = mesPrefs.getString(LATRDV, null);
            String LoR = mesPrefs.getString(LONRDV, null);
            String laP = mesPrefs.getString(LATPARK, null);
            String LoP = mesPrefs.getString(LONPARK, null);
            if (BuildConfig.DEBUG) {
                Log.i("GUMSBLO", "coord RV " + laR + "  " + LoR);
            }
            if (!("".equals(laR)) && !("".equals(LoR))) {
                Uri cartoIntentUri;
                Intent cartoIntent = null;
                if ("com.iphigenie".equals(appli)) {
                    boolean ecriture = AuxGPX.ecritFichier(laR, LoR, laP, LoP, mFile);
/*                    if (BuildConfig.DEBUG) {
                        Log.i("GUMSBLO", "flag, ecriture = "+flagGPX+", "+ecriture); }  */
                    if (flagGPX && ecriture)  {
                        cartoIntentUri = FileProvider.getUriForFile(this,"fr.gumsparis.gumsbleau.fileprovider", mFile);
                        if (BuildConfig.DEBUG) {
                            Log.i("GUMSBLO", "4 URI carto= "+cartoIntentUri);
                        }
                        cartoIntent = new Intent(Intent.ACTION_VIEW, cartoIntentUri);
                        cartoIntent.setPackage(appli);
                        cartoIntent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                    } else {
                        Toast.makeText(MainActivity.this, "Cette appli ne peut pas être utilisée", Toast.LENGTH_LONG).show();
                    }
                }else if("de.komoot.android".equals(appli)){
                    cartoIntentUri = Uri.parse("google.navigation:q=" + laR + "," + LoR);
                    cartoIntent = new Intent(Intent.ACTION_VIEW, cartoIntentUri);
                    cartoIntent.setPackage(appli);
                }
                else {
                    cartoIntentUri = Uri.parse("geo:" + laR + "," + LoR);
                    cartoIntent = new Intent(Intent.ACTION_VIEW, cartoIntentUri);
                    cartoIntent.setPackage(appli);
                }
                if (cartoIntent != null) {
                    if (cartoIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(cartoIntent);
                    } else {
                        Toast.makeText(MainActivity.this, "Appli de carte topo non disponible", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Signaler problème aux développeurs", Toast.LENGTH_LONG).show();
                }
            }else{
                alerte("noRV");
            }
        });

    }  // end onCreate
   // la méthode statusBarColor ne fonctionne pas. Ne fait rien
    void statusBarColor(View conteneur, int color){
        ViewCompat.setOnApplyWindowInsetsListener(conteneur, new OnApplyWindowInsetsListener() {
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                 insets.getInsets(WindowInsetsCompat.Type.statusBars());
                v.setBackgroundColor(color);
                return(insets);
            }
        } );


     }

    @Override
    protected void onPause(){
        if (Variables.isRegistered){
            if (BuildConfig.DEBUG) Log.i("GUMSBLO", "onpause unregister");
            connectionMonitor.unregisterDefaultNetworkCallback();
            Variables.isRegistered=false;
        }
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (BuildConfig.DEBUG) Log.i("GUMSBLO", "in onNewIntent");
        // getIntent() should always return the most recent
        handleIntent(intent);
    }

    // à partir de là on surveille la disponibilité d'Internet
    @Override
    protected void onResume(){
        super.onResume();
        if (BuildConfig.DEBUG) Log.i("GUMSBLO", "onresume register");
        connectionMonitor.registerDefaultNetworkCallback();
        Variables.isRegistered=true;
    }

    protected void handleIntent(Intent intent) {
        if (intent != null) {
//            if (BuildConfig.DEBUG) Log.i("GUMSBLO", "main intent reçu "+intent);
            if (intent.hasExtra("sortie")){
                sortieChoisie = intent.getStringExtra("sortie");
                if (BuildConfig.DEBUG) Log.i("GUMSBLO", "main intent extra "+sortieChoisie);
                editeur.putBoolean("auto", sortieChoisie.isEmpty());
                editeur.putString(DATERV, "2000-01-01");
                editeur.putString("sortiechoisie", sortieChoisie);
                editeur.commit();
//                if (BuildConfig.DEBUG) Log.i("GUMSBLO", "getinfo 2 =");
                manipsInfo.trouverInfos();
            }
        }
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
                break;
            case "noPK":
                message = "Coordonnées parking inutilisables";
                break;
            case "noRV":
                message = "Coordonnées rendez-vous inutilisables";
        }
        DialogAlertes infoUtilisateur = DialogAlertes.newInstance(flag, message);
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
        if (id == R.id.sorties_futures) {
            Intent lesFutures = new Intent(MainActivity.this, ListFutures.class);
            startActivity(lesFutures);
            return true;
        }
        if (id == R.id.choix_sortie) {
            Intent choisirSortie = new Intent(MainActivity.this, ChoixSortie.class);
            startActivity(choisirSortie);
            return true;
        }
        if (id == R.id.apropos) {
            Intent lireAPropos = new Intent(MainActivity.this, APropos.class);
            startActivity(lireAPropos);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

//Toast.makeText(getActivity(), "Pas d'accès aux prefs", Toast.LENGTH_LONG).show();
