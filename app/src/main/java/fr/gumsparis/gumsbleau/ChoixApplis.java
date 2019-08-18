package fr.gumsparis.gumsbleau;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import static fr.gumsparis.gumsbleau.MainActivity.APPLINAV;
import static fr.gumsparis.gumsbleau.MainActivity.APPLICARTO;


public class ChoixApplis extends AppCompatActivity {

// pour sélectionner l'appli de guidage vers le parking et celle de carte topographique pour le RdV
    // et stocker les noms de paquets dans les préférences

    TextView choixnav = null;
    TextView choixcarto = null;
    RadioGroup rgNav = null;
    RadioButton gmp = null;
    RadioButton waz = null;
    RadioButton her = null;
    RadioGroup rgCarto = null;
    RadioButton ifi = null;
    RadioButton vrg = null;
    RadioButton mtr = null;
    Button terminer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_applis);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences mesPrefs = MyHelper.getInstance().recupPrefs();
        final SharedPreferences.Editor  editeur = mesPrefs.edit();
        String applinav = mesPrefs.getString(APPLINAV, getString(R.string.gmp));
        String applicarto = mesPrefs.getString(APPLICARTO, getString(R.string.ifi));

        choixnav = findViewById(R.id.choixnav);
        rgNav = findViewById(R.id.groupnav);
        gmp = findViewById(R.id.gmaps);
        waz = findViewById(R.id.waze);
        her = findViewById(R.id.herewg);
        choixcarto = findViewById(R.id.choixcarto);
        rgCarto = findViewById(R.id.groupcarto);
        ifi = findViewById(R.id.iphi);
        vrg = findViewById(R.id.vranger);
        mtr =  findViewById(R.id.mtrails);
        terminer = findViewById(R.id.buttonfin);

//        choixnav.setText(getString(R.string.choixnav));
        choixnav.setText(Html.fromHtml(getString(R.string.choixnav)));
//        choixcarto.setText(getString(R.string.choixcarto));
        choixcarto.setText(Html.fromHtml(getString(R.string.choixcarto)));

        if(getString(R.string.gmp).equals(applinav)) {gmp.setChecked(true);}
        else if(getString(R.string.waz).equals(applinav)) {waz.setChecked(true);}
        else if(getString(R.string.her).equals(applinav)) {her.setChecked(true);}

        rgNav.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.gmaps:
                        editeur.putString(APPLINAV, getString(R.string.gmp));
 //                       gmp.setChecked(true);
                        break;
                    case R.id.waze:
                        editeur.putString(APPLINAV, getString(R.string.waz));
 //                       gmp.setChecked(true);
                        break;
                    case R.id.herewg:
                        editeur.putString(APPLINAV, getString(R.string.her));
//                        gmp.setChecked(true);
                        break;
                }
                editeur.apply();
            }
        });

        if(getString(R.string.ifi).equals(applicarto)) {ifi.setChecked(true);}
        else if(getString(R.string.vrg).equals(applicarto)) {vrg.setChecked(true);}
        else if(getString(R.string.mtr).equals(applicarto)) {mtr.setChecked(true);}

        rgCarto.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.iphi:
                        editeur.putString(APPLICARTO, getString(R.string.ifi));
                        break;
                    case R.id.vranger:
                        editeur.putString(APPLICARTO, getString(R.string.vrg));
                        break;
                    case R.id.mtrails:
                        editeur.putString(APPLICARTO, getString(R.string.mtr));
                        break;
                }
                editeur.apply();
            }
        });

        terminer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

}
