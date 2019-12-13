package fr.gumsparis.gumsbleau;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AuxGPX {

    static private String LATLON_RV = "latlon.gpx";
    static private String LOCATION = "location";
    private String CONTENU = "<?xml version=\"1.0\" ?><gpx><trkpt lat=\"44.9801\" lon=\"6.5030\"></trkpt></gpx>";
    static private String GPX1 ="<?xml version=\"1.0\" ?><gpx><trk><trkseg><trkpt lat=\"";
    static private String GPX2 ="\" lon=\"";
    static private String GPX3 ="\"></trkpt><trkpt lat=\"";
    static private String GPX4 ="\"></trkpt></trkseg></trk></gpx>";

    private static File mFile = null;

    static boolean faitFichierGPX() {
        if(isExternalStorageWritable()){
            File fichiers = MyHelper.getInstance().recupStorageDir();
            if (BuildConfig.DEBUG){
            Log.i("GUMSBLO", "0  chemin OK "+fichiers.toString());}
            mFile = new File(fichiers, LATLON_RV);
            if (BuildConfig.DEBUG){
            Log.i("GUMSBLO", "1  fichier créé ");}
            return true;
        }else{ return false;}
    }

    static Uri faitURI(String lat, String lon, String laP, String LoP) {
// fabrique le texte du fichier GPX
        String texteGPX = faitGPXTexte(lat,lon);
// écrit le fichier GPX dans le stockage de Documents
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {

                FileOutputStream output = new FileOutputStream(mFile);
                output.write(texteGPX.getBytes());
                output.close();
            }
            if (BuildConfig.DEBUG){
            Log.i("GUMSBLO", "3 fichier GPX a été écrit ");}
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
// fabrique l'URI du fichier
        return MyHelper.getInstance().recupURI(mFile);
    }

    private static String faitGPXTexte (String lat, String lon){
        String latBis = String.valueOf(Double.parseDouble(lat)+0.0003);
        String texteFichier = GPX1+lat+GPX2+lon+GPX4;
        if (BuildConfig.DEBUG){
//        texteFichier = "<?xml version=\"1.0\" ?><gpx><trk><trkseg><trkpt lat=\"48.44596\" lon=\"2.63768\"></trkpt><trkpt lat=\"48.447261\" lon=\"2.640046\"></trkpt></trkseg></trk></gpx>";
        Log.i("GUMSBLO", "2 fichier GPX "+texteFichier);}
        return texteFichier;
    }
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    static File getPrivateDocStorageDir(Context context, String position) {
        // Get the directory for the app's private documents directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS), position);
        if (BuildConfig.DEBUG){
        Log.i("GUMSBLO", "chemin fichier GPX "+file.getPath());}
        if (!file.mkdir()) {
            Log.e("GUMSBLO", "Directory pas created");
        }
        return file;
    }


}