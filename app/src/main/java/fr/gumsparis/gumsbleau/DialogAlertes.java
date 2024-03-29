package fr.gumsparis.gumsbleau;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class DialogAlertes extends DialogFragment {

// cette classe doit être déclarée public
/* copie de https://guides.codepath.com/android/using-dialogfragment
* cette version du AlertDialog dans un DialogFragment a la particularité de pouvoir recevoir des valeurs
* de paramètres lors de la création de l'instance. Ici c'est le message de l'alerte
* */

    public DialogAlertes() {
        // Empty constructor required for DialogFragment
    }

    static DialogAlertes newInstance(String flag, String message) {
        DialogAlertes frag = new DialogAlertes();
        Bundle args = new Bundle();
        args.putString("flag", flag);
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String message = "";
        if (getArguments() != null) {
            message = getArguments().getString("message", ""); }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireActivity());
        alertDialogBuilder.setMessage(message);

//        alertDialogBuilder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        alertDialogBuilder.setPositiveButton("OK", (dialogInterface, i) -> {
            if("2".equals(getArguments().getString("flag", ""))
                    || "3".equals(getArguments().getString("flag", ""))
                    || "false".equals(getArguments().getString("flag", ""))) {
                requireActivity().finish();
            }
            Objects.requireNonNull(getDialog()).dismiss();
        });

        return alertDialogBuilder.create();

    }
}
