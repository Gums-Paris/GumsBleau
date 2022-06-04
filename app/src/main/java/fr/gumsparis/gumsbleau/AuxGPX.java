package fr.gumsparis.gumsbleau;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AuxGPX {

    static final private String LATLON_RV = "latlon.gpx";
    static final private String LOCATION = "location";
    final private String CONTENU = "<?xml version=\"1.0\" ?><gpx><trkpt lat=\"44.9801\" lon=\"6.5030\"></trkpt></gpx>";
    static final private String GPX1 ="<?xml version=\"1.0\" ?><gpx><trk><trkseg><trkpt lat=\"";
    static final private String GPX2 ="\" lon=\"";
    static final private String GPX3 ="\"></trkpt><trkpt lat=\"";
    static final private String GPX4 ="\"></trkpt></trkseg></trk></gpx>";

    static boolean ecritFichier(String lat, String lon, String laP, String LoP, File mFile) {
        String texteGPX = faitGPXTexte(lat,lon);
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
                FileOutputStream output = new FileOutputStream(mFile);
                output.write(texteGPX.getBytes());
                output.close();
                if (BuildConfig.DEBUG){
                    Log.i("GUMSBLO", "3 fichier GPX a été écrit ");}
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String faitGPXTexte (String lat, String lon){
        String texteFichier = GPX1+lat+GPX2+lon+GPX4;
        if (BuildConfig.DEBUG){
        Log.i("GUMSBLO", "2 fichier GPX "+texteFichier);}
        return texteFichier;
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

}