package fr.gumsparis.gumsbleau;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChoixSortie extends AppCompatActivity {

    ModelBleauListe manipsListe = null;
    Boolean flagDeListe;
    String flag;
    ArrayList<String> listeNomLieu = new ArrayList<>();
    ArrayList<String> listeIdArticle = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<MyAdapter.MyViewHolder> monAdapter;
    ProgressBar attente = null;
    NetworkConnectionMonitor connectionMonitor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_sortie);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        attente = findViewById(R.id.listIndeterminateBar);
        attente.setVisibility(View.VISIBLE);

// verif internet OK et mise en place de la surveillance réseau
// on arrive ici de puis Main, donc isNetworkConnected a déjà été positionné, mais comme ça coûte pas cher,
// on remet la surveillance réseau en marche pendant le temps où l'utilisateur choisit sa sorte
        connectionMonitor = NetworkConnectionMonitor.getInstance();
        connectionMonitor.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isConnected) {
                if (BuildConfig.DEBUG) Log.i("GUMSBLO", "choix conmon observe "+isConnected);
                Variables.isNetworkConnected = isConnected;
            }
        });

        // le model qui gère les données de la liste de sorties
        manipsListe = new ViewModelProvider(this).get(ModelBleauListe.class);

    // création de l'adapteur
        monAdapter = new MyAdapter();
    // et mise en place de la RecyclerView
        recyclerView = findViewById(R.id.listechoix);
    // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    // création de l'observateur et établissement du lien de l'observateur avec la LiveData du flag
        final Observer<Boolean> flagObserver = newFlag -> {
            flagDeListe = newFlag;
            flag = newFlag.toString();
            if (!newFlag) {
                String message = getString(R.string.pas_infos);
                DialogAlertes infoListe = DialogAlertes.newInstance(flag, message);
                infoListe.show(getSupportFragmentManager(), "infoliste");
            }
        };
        manipsListe.getFlagListe().observe(this, flagObserver);

    // puis l'observateur de la liste de lieus, affecter la liste à l'adapteur et l'adapteur à la recyclerView
        final Observer<ArrayList<String>> listeLieuObserver = newListeLieu -> {
            attente.setVisibility(View.GONE);
            if (newListeLieu != null) {
                listeNomLieu = newListeLieu;
                ((MyAdapter) monAdapter).setData(newListeLieu);
                recyclerView.setAdapter(monAdapter);
           }
        };
        manipsListe.getListeLieux().observe(this, listeLieuObserver);

    // puis l'observateur de la liste des id d'article et la confier à l'adapteur
        final Observer<ArrayList<String>> listeArticleObserver = newListeArticle -> {
            if (newListeArticle != null) {
                listeIdArticle = newListeArticle;
                ((MyAdapter) monAdapter).setArticles(newListeArticle);
            }
        };
        manipsListe.getListeArticles().observe(this, listeArticleObserver);

    } // end onCreate

    @Override
    protected void onPause(){
        if (Variables.isRegistered){
            if (BuildConfig.DEBUG) Log.i("GUMSBLO", "onpause unregister");
            connectionMonitor.unregisterDefaultNetworkCallback();
            Variables.isRegistered=false;
        }
        super.onPause();
    }
    // à partir de là on surveille la disponibilité d'Internet
    @Override
    protected void onResume(){
        super.onResume();
        if (BuildConfig.DEBUG) Log.i("GUMSBLO", "onresume register");
        connectionMonitor.registerDefaultNetworkCallback();
        Variables.isRegistered=true;
    }


}
