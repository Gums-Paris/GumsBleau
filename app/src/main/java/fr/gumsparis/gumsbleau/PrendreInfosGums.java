package fr.gumsparis.gumsbleau;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public  class PrendreInfosGums extends AsyncTask<String,Void,String> {

    // Une tâche asynchrone réalisant la transaction internet en arrière-plan pour aller chercher les infos
    // sur le serveur de gumsparis, les mettre en LiveData et les sauvegarder dans SharedPreferences

    @Override
    protected String doInBackground(String... urls) {

        String resultat ;
        BufferedInputStream input = null;

        try {
            Log.i("GUMSBLO", "nous allons ouvrir "+urls[0]);
            URL urlGUMS = new URL(urls[0]);
            input = new BufferedInputStream(urlGUMS.openStream());
            Log.i("GUMSBLO", "nous avons un stream");
            byte[] buffer = new byte[1024];
            StringBuilder sb = new StringBuilder();
            int octetsLus ;
            while ((octetsLus = input.read(buffer)) > 0) {
                String str = new String(buffer, 0, octetsLus );
                sb.append(str); }
            resultat = sb.toString();
            if (BuildConfig.DEBUG){
                Log.i("GUMSBLO", "nous avons un résultat ");}
        }catch (ConnectException e){
            if (BuildConfig.DEBUG){
            Log.e("erreur connexion", e.getMessage());}
            resultat = "netOUT";
        }catch (MalformedURLException e) {
            if (BuildConfig.DEBUG){
                Log.e("erreur URL", e.getMessage());}
            resultat = "netOUT";
        }catch (UnknownHostException e) {
            if (BuildConfig.DEBUG){
            Log.e("erreur url hôte", e.getMessage());}
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
        // le traitement dépend de l'URL accédée, il est défini dans les classes dérivées
    }

}