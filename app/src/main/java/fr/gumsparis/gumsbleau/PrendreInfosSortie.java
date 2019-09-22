package fr.gumsparis.gumsbleau;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static fr.gumsparis.gumsbleau.MainActivity.DATERV;
import static fr.gumsparis.gumsbleau.MainActivity.FLAG;
import static fr.gumsparis.gumsbleau.MainActivity.ITIPARK;
import static fr.gumsparis.gumsbleau.MainActivity.ITIRDV;
import static fr.gumsparis.gumsbleau.MainActivity.LATPARK;
import static fr.gumsparis.gumsbleau.MainActivity.LATRDV;
import static fr.gumsparis.gumsbleau.MainActivity.LIEU;
import static fr.gumsparis.gumsbleau.MainActivity.LONPARK;
import static fr.gumsparis.gumsbleau.MainActivity.LONRDV;

public class PrendreInfosSortie extends PrendreInfosGums {
    protected void onPostExecute (String result) {
        Log.i("GUMSBLO", "dans postexecute de sortie");
        String[] itis = new String[2];
        String lieu = "";
        String date = "";
        String flag = "2";

        SharedPreferences mesPrefs = MyHelper.getInstance().recupPrefs();
        SharedPreferences.Editor  editeur = mesPrefs.edit();

        if (result.equals("netOUT")) {
            ModelBleauInfo.flagInfo.setValue("3");
        } else {
            try {
                JSONObject jInfos = new JSONObject(result);
                if (jInfos.opt(ITIPARK) != null) {
                    itis[0] = (String) jInfos.opt(ITIPARK);
                    editeur.putString(ITIPARK, itis[0]);
                }
                if (jInfos.opt(ITIRDV) != null) {
                    itis[1] = (String) jInfos.opt(ITIRDV);
                    editeur.putString(ITIRDV, itis[1]);
                }
                if (jInfos.opt(LIEU) != null) {
                    lieu = (String) jInfos.opt(LIEU);
                    editeur.putString(LIEU, lieu);
                }
                if (jInfos.opt(DATERV) != null) {
                    date = (String) jInfos.opt(DATERV);
                    editeur.putString(DATERV, date);
                }
                if (jInfos.opt(LATPARK) != null) {
                    editeur.putString(LATPARK, (String) jInfos.opt(LATPARK));
                }
                if (jInfos.opt(LONPARK) != null) {
                    editeur.putString(LONPARK, (String) jInfos.opt(LONPARK));
                }
                if (jInfos.opt(LATRDV) != null) {
                    editeur.putString(LATRDV, (String) jInfos.opt(LATRDV));
                }
                if (jInfos.opt(LONRDV) != null) {
                    editeur.putString(LONRDV, (String) jInfos.opt(LONRDV));
                }
                if (jInfos.opt(FLAG) != null) {
                    flag = (String) jInfos.opt(FLAG);
                    editeur.putString(FLAG, flag);
                }
                editeur.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ModelBleauInfo.flagInfo.setValue(flag);
            ModelBleauInfo.infosSortie.setValue(itis);
            ModelBleauInfo.lieuSortie.setValue(lieu);
            ModelBleauInfo.dateSortie.setValue(date);
//                System.exit(0);
        }
    }

}
