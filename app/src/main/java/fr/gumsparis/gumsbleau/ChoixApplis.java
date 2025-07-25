package fr.gumsparis.gumsbleau;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
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
    RadioButton komt = null;
    Button terminer = null;
    View conteneur =null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_applis);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        conteneur = findViewById(R.id.choose);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            EdgeToEdge.enable(this);
            //     statusBarColor(conteneur,R.color.colorPrimaryDark);
            HandleInsets.placeInsets(this, conteneur);
        }

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
        komt = findViewById(R.id.komoot);
        terminer = findViewById(R.id.buttonfin);

// choix du comportement pour le choix de l'applide navigation
        choixnav.setText(Aux.fromHtml(getString(R.string.choixnav)));
        if ("yes".equals(mesPrefs.getString("chooser", null))){casenav.setChecked(true);}
        casenav.setOnClickListener(v -> {
            if (((CheckBox)v).isChecked()) {
                editeur.putString("chooser", "yes");
                editeur.apply();
            }else{
                editeur.putString("chooser", "no");
                editeur.apply();
            }
        });

// choix d'une appli pour la cartographie autour du rendez-vous
        choixcarto.setText(Aux.fromHtml(getString(R.string.choixcarto)));

        if(getString(R.string.ifi).equals(applicarto)) {ifi.setChecked(true);}
        else if(getString(R.string.vrg).equals(applicarto)) {vrg.setChecked(true);}
        else if(getString(R.string.mtr).equals(applicarto)) {mtr.setChecked(true);}
        else if(getString(R.string.orx).equals(applicarto)) {orx.setChecked(true);}
        else if(getString(R.string.komt).equals(applicarto)) {komt.setChecked(true);}

        rgCarto.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.iphi) {
                editeur.putString(APPLICARTO, getString(R.string.ifi));
/*            }else if (checkedId == R.id.vranger) {
                editeur.putString(APPLICARTO, getString(R.string.vrg));
            }else if (checkedId == R.id.mtrails) {
                editeur.putString(APPLICARTO, getString(R.string.mtr)); */
            }else if (checkedId == R.id.orux){
                editeur.putString(APPLICARTO, getString(R.string.orx));
            }else if (checkedId == R.id.komoot){
                editeur.putString(APPLICARTO, getString(R.string.komt));
            }
            editeur.apply();
        });

        terminer.setOnClickListener(v -> finish());

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

}
