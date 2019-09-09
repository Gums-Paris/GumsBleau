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

// pour sélectionner l'appli de  carte topographique pour le RdV et stocker le nom dans les préférences

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

        choixcarto = findViewById(R.id.choixcarto);
        rgCarto = findViewById(R.id.groupcarto);
        ifi = findViewById(R.id.iphi);
        vrg = findViewById(R.id.vranger);
        mtr =  findViewById(R.id.mtrails);
        orx = findViewById(R.id.orux);
        terminer = findViewById(R.id.buttonfin);

        choixcarto.setText(Html.fromHtml(getString(R.string.choixcarto)));

        if(getString(R.string.ifi).equals(applicarto)) {ifi.setChecked(true);}
        else if(getString(R.string.vrg).equals(applicarto)) {vrg.setChecked(true);}
        else if(getString(R.string.mtr).equals(applicarto)) {mtr.setChecked(true);}
        else if(getString(R.string.orx).equals(applicarto)) {orx.setChecked(true);}

        rgCarto.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener()
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
                    case R.id.orux:
                        editeur.putString(APPLICARTO, getString(R.string.orx));
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
