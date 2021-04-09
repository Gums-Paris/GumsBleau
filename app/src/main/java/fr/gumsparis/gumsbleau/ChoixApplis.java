package fr.gumsparis.gumsbleau;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import static fr.gumsparis.gumsbleau.MainActivity.APPLICARTO;

public class ChoixApplis extends AppCompatActivity {

// pour sélectionner l'appli de  carte topographique pour le RdV et stocker le nom dans les préférences

    TextView choixnav = null;
    CheckBox casenav = null;
    TextView choixcarto = null;
    RadioGroup rgCarto = null;
    RadioButton ifi = null;
    RadioButton vrg = null;
    RadioButton mtr = null;
    RadioButton orx = null;
    Button terminer = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_applis);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences mesPrefs = MyHelper.getInstance().recupPrefs();
        final SharedPreferences.Editor  editeur = mesPrefs.edit();
        String applicarto = mesPrefs.getString(APPLICARTO, getString(R.string.ifi));

        choixnav = findViewById(R.id.choixnav);
        casenav = findViewById(R.id.chkChooser);
        choixcarto = findViewById(R.id.choixcarto);
        rgCarto = findViewById(R.id.groupcarto);
        ifi = findViewById(R.id.iphi);
        vrg = findViewById(R.id.vranger);
        mtr =  findViewById(R.id.mtrails);
        orx = findViewById(R.id.orux);
        terminer = findViewById(R.id.buttonfin);

// choix du comportement pour le choix de l'applide navigation
        choixnav.setText(Aux.fromHtml(getString(R.string.choixnav)));
        editeur.putString("chooser", "no");
        editeur.apply();
        casenav.setOnClickListener(v -> {
            if (((CheckBox)v).isChecked()) {
                editeur.putString("chooser", "yes");
                editeur.apply();
            }
        });

// choix d'une appli pour la cartographie autour du rendez-vous
        choixcarto.setText(Aux.fromHtml(getString(R.string.choixcarto)));

        if(getString(R.string.ifi).equals(applicarto)) {ifi.setChecked(true);}
        else if(getString(R.string.vrg).equals(applicarto)) {vrg.setChecked(true);}
        else if(getString(R.string.mtr).equals(applicarto)) {mtr.setChecked(true);}
        else if(getString(R.string.orx).equals(applicarto)) {orx.setChecked(true);}

        rgCarto.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.iphi) {
                editeur.putString(APPLICARTO, getString(R.string.ifi));
            }else if (checkedId == R.id.vranger) {
                editeur.putString(APPLICARTO, getString(R.string.vrg));
            }else if (checkedId == R.id.mtrails) {
                editeur.putString(APPLICARTO, getString(R.string.mtr));
            }else if (checkedId == R.id.orux){
                editeur.putString(APPLICARTO, getString(R.string.orx));
            }
            editeur.apply();
        });

        terminer.setOnClickListener(v -> finish());

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

}
