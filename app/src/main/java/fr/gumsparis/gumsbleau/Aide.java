package fr.gumsparis.gumsbleau;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class Aide extends AppCompatActivity {

    TextView affichage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aide);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        affichage = findViewById(R.id.texthelp);
        affichage.setText(Html.fromHtml(getString(R.string.helptext)));
/* pour pouvoir mettre un texte de lien http différent de la cible du lien il faut rajouter la ligne qui suit
*  si on veut bien avoir un texte identique à la cible, on peut ne pas mettre cette ligne mais il faut alors rajouter
*  android:autoLink="email" ou "web" selon le cas dans le layout du TextView*/
        affichage.setMovementMethod(LinkMovementMethod.getInstance());

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
