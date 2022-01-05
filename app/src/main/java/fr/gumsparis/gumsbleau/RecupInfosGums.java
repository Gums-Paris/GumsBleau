package fr.gumsparis.gumsbleau;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

public class RecupInfosGums implements Callable<String> {

/* Copié de la class éponyme de GumsSki d'où le passage d'un array de Strings nécessaire à GumsSki pour
*  la construction de la requête; ici la requ$ête est fournie comme unique paramètre. */

    private String[] strings = new String[6];
    public RecupInfosGums(String[] strings) {this.strings = strings;}
    @Override
    public String call() throws Exception {
        HttpURLConnection conn = null;
        String resultat;
        StringBuilder result = new StringBuilder();
        if (Variables.isNetworkConnected) {
            try {
                if (BuildConfig.DEBUG){
                    Log.i("GUMSBLO", "recupInfosGums entre dans connect");}
                URL urlObject = new URL(strings[0]);
                conn = (HttpURLConnection) urlObject.openConnection();
                conn.setRequestMethod("GET");

                if (BuildConfig.DEBUG){
                    Log.i("GUMSBLO", "ouvre InputStream ");}
                InputStream in = new BufferedInputStream(conn.getInputStream());
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                if (BuildConfig.DEBUG){
                    Log.i("GUMSBLO", "buffered reader ");}
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

            } catch (ConnectException e) {
                if (BuildConfig.DEBUG){
                    Log.e("GUMSBLO erreur connexion", e.getMessage());}
                result.append("netOUT");
            } catch (MalformedURLException e){
                if (BuildConfig.DEBUG){
                    Log.e("GUMSBLO erreur URL", e.getMessage());}
                result.append("netOUT");
            } catch (UnknownHostException e) {
                if (BuildConfig.DEBUG){
                    Log.e("GUMSBLO erreur url hôte", e.getMessage());}
                result.append("netOUT");
            } catch (IOException e) {
                e.printStackTrace();
                result.append("netOUT");
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }else{
            if (BuildConfig.DEBUG){
                Log.i("GUMSBLO", "recupInfo netOUT ");}
            result.append("netOUT");
        }
        resultat = String.valueOf(result);
        if (BuildConfig.DEBUG){
            Log.i("GUMSBLO", "reçu info "+resultat);}
        return resultat;
    }
}