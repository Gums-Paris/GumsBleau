package fr.gumsparis.gumsbleau;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListFutures extends AppCompatActivity {

    ModelBleauFutures model = null;
    private RecyclerView recyclerView;
    ProgressBar attente = null;
    NetworkConnectionMonitor connectionMonitor;
    TextView affichage = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_futures);
        Toolbar toolbar = findViewById(R.id.toolbarFutur);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        affichage = findViewById(R.id.vuelist);
        affichage.setText(Aux.fromHtml(getString(R.string.futures)));
        /* pour pouvoir mettre un texte de lien http différent de la cible du lien il faut rajouter la ligne qui suit
         *  si on veut bien avoir un texte identique à la cible, on peut ne pas mettre cette ligne mais il faut alors rajouter
         *  android:autoLink="email" ou "web" selon le cas dans le layout du TextView*/
        affichage.setMovementMethod(LinkMovementMethod.getInstance());

        attente = findViewById(R.id.listIndeterminateBar);
        attente.setVisibility(View.VISIBLE);

        connectionMonitor = NetworkConnectionMonitor.getInstance();
        connectionMonitor.observe(this, isConnected -> {
            if (BuildConfig.DEBUG) Log.i("GUMSBLO", "choix conmon observe "+isConnected);
            Variables.isNetworkConnected = isConnected;
        });

/* On récupère la liste des sorties futures à travers le constructeur du modèle  et on la met dans la recyclerview
*  de l'observer de ladite liste*/
        model = new ViewModelProvider(this).get(ModelBleauFutures.class);

        recyclerView = findViewById(R.id.listefutur);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final Observer<Boolean> flagObserver = newFlag -> {
            String flag = newFlag.toString();
            if (!newFlag) {
                String message = getString(R.string.pas_infos);
                DialogAlertes infoFutures = DialogAlertes.newInstance(flag, message);
                infoFutures.show(getSupportFragmentManager(), "infoFutures");
            }
        };
        model.getflagFutures().observe(this, flagObserver);

/* RecyclerViewClickListener est une Interface qui permet de sortir le clickListener de l'Adapter pour le mettre dans l'activité
*  ce qui permet de l'avoir hors d'un contexte static et aussi de standardiser l'adapteur*/
        final Observer<ArrayList<Sortie>> listeFuturesObserver = newListe -> {
            attente.setVisibility(View.GONE);
            if (newListe != null) {
                RecyclerViewClickListener listener = (view, position) -> {
                    String  idSortie = newListe.get(position).getNumArticle();
                    Intent choisi = new Intent(ListFutures.this, MainActivity.class);
                    choisi.putExtra("sortie", idSortie);
                    if (BuildConfig.DEBUG) Log.i("GUMSBLO", "intent envoyé futur "+choisi);
                    ListFutures.this.startActivity(choisi);
                };
                MyFuturesAdapter adapter = new MyFuturesAdapter(ListFutures.this, newListe, listener);
                recyclerView.setAdapter(adapter);
            }
        };
        model.getListeFutures().observe(this, listeFuturesObserver);
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
    // à partir de là on surveille la disponibilité d'Internet
    @Override
    protected void onResume(){
        super.onResume();
        if (BuildConfig.DEBUG) Log.i("GUMSBLO", "onresume register");
        connectionMonitor.registerDefaultNetworkCallback();
        Variables.isRegistered=true;
    }


}
