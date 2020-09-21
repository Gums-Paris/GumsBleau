package fr.gumsparis.gumsbleau;

import android.os.Bundle;
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
    ArrayList<String> listeNomLieu = new ArrayList<>();
    ArrayList<String> listeIdArticle = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<MyAdapter.MyViewHolder> monAdapter;
    ProgressBar attente = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_sortie);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        attente = findViewById(R.id.listIndeterminateBar);
        attente.setVisibility(View.VISIBLE);

    // le model qui gère les données de la liste de sorties
        manipsListe = new ViewModelProvider(this).get(ModelBleauListe.class);

    // création de l'adapteur
        monAdapter = new MyAdapter();
    // et mise en place de la RecyclerView
        recyclerView = findViewById(R.id.listechoix);
    // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    // création de l'observateur et établissement du lien de l'observateur avec la LiveData du flag
        final Observer<Boolean> flagObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean newFlag) {
                flagDeListe = newFlag;
            }
        };
        manipsListe.getFlagListe().observe(this, flagObserver);

    // puis l'observateur de la liste de lieus, affecter la liste à l'adapteur et l'adapteur à la recyclerView
        final Observer<ArrayList<String>> listeLieuObserver = new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> newListeLieu) {
                attente.setVisibility(View.GONE);
                if (newListeLieu != null) {
                    listeNomLieu = newListeLieu;
                    ((MyAdapter) monAdapter).setData(newListeLieu);
                    recyclerView.setAdapter(monAdapter);
               }
            }
        };
        manipsListe.getListeLieux().observe(this, listeLieuObserver);

    // puis l'observateur de la liste des id d'article et la confier à l'adapteur
        final Observer<ArrayList<String>> listeArticleObserver = new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> newListeArticle) {
                if (newListeArticle != null) {
                    listeIdArticle = newListeArticle;
                    ((MyAdapter) monAdapter).setArticles(newListeArticle);
                }
            }
        };
        manipsListe.getListeArticles().observe(this, listeArticleObserver);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }
}
