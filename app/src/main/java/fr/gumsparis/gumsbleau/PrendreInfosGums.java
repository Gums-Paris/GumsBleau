package fr.gumsparis.gumsbleau;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import static fr.gumsparis.gumsbleau.MainActivity.LIEU;
import static fr.gumsparis.gumsbleau.MainActivity.DATE;
import static fr.gumsparis.gumsbleau.MainActivity.ITIPARK;
import static fr.gumsparis.gumsbleau.MainActivity.ITIRDV;
import static fr.gumsparis.gumsbleau.MainActivity.FLAG;
import static fr.gumsparis.gumsbleau.MainActivity.LATPARK;
import static fr.gumsparis.gumsbleau.MainActivity.LONPARK;
import static fr.gumsparis.gumsbleau.MainActivity.LATRDV;
import static fr.gumsparis.gumsbleau.MainActivity.LONRDV;


public  class PrendreInfosGums extends AsyncTask<String,Void,String> {

    // Une tâche asynchrone réalisant la transaction internet en arrière-plan pour aller chercher les infos
    // sur le serveur de gumsparis, les mettre en LiveData et les sauvegarder dans SharedPreferences

    @Override
    protected String doInBackground(String... urls) {

        String resultat = null;
        BufferedInputStream input = null;
        try {
            Log.i("GUMSBLO", "nous allons ouvrir "+urls[0]);
            URL urlGUMS = new URL(urls[0]);
            input = new BufferedInputStream(urlGUMS.openStream());
            Log.i("GUMSBLO", "nous avons un stream");
            byte[] buffer = new byte[1024];
            StringBuilder sb = new StringBuilder();
            int octetsLus = 0;
            while ((octetsLus = input.read(buffer)) > 0) {
                String str = new String(buffer, 0, octetsLus );
                sb.append(str); }
            resultat = sb.toString();
            Log.i("GUMSBLO", "nous avons un résultat "+resultat);
        }catch (ConnectException e){
            Log.e("erreur connexion", e.getMessage());
            resultat = "netOUT";
        }catch (MalformedURLException e) {
            Log.e("erreur URL", e.getMessage());
            resultat = "netOUT";
        }catch (UnknownHostException e) {
            Log.e("erreur url hôte", e.getMessage());
            resultat = "netOUT";
        }catch (IOException e) {
            e.printStackTrace();
            resultat = "netOUT";
        }finally{
            try { if(input!=null) input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultat;
// resultat est le json envoyé par gumsparis
    }

    // pour ranger le résultat dans la LiveData et les Shared Preferences
    protected void onPostExecute (String result) {
        Log.i("GUMSBLO", "dans postexecute");
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
                if (jInfos.opt(DATE) != null) {
                    date = (String) jInfos.opt(DATE);
                    editeur.putString(DATE, date);
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