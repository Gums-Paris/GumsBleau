package fr.gumsparis.gumsbleau;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

// l'adapteur connecte les données à la RecyclerView

    private ArrayList<String> mDataset;
    private ArrayList<String> mArticlesset;

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
// ici se trouve l'info pour pouvoir présenter l'un des items des données dans une vue spécifique
        TextView textView;
        final MyAdapter mAdapter;
// constructeur du ViewHolder pour initialiser la vue et installer l'adapteur
        MyViewHolder(View v, MyAdapter adapter){
            super(v);
            textView = v.findViewById(R.id.unlieu);
            this.mAdapter = adapter;
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
// pour donner la position de la vue portant l'item cliqué et envoyer la commande à MainActivity
            int mPosition = getLayoutPosition();
            String element = mArticlesset.get(mPosition);
            Log.i("GUMSBLO","idArticle = "+element);
            Intent choisi = new Intent(v.getContext(), MainActivity.class);
            choisi.putExtra("sortie", element);
            v.getContext().startActivity(choisi);

        }
    }

    void setData(ArrayList<String> newData) {
        this.mDataset = newData;
        notifyDataSetChanged();
    }

    void setArticles(ArrayList<String> newData) {
        this.mArticlesset = newData;
        notifyDataSetChanged();
    }

// les 3 méthodes de l'adapteur : onCreateViewHolder, onBindViewHolder, getItemCount

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        // fournit le ViewHolder avec le layout et l'adapteur
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        View v = mInflater.inflate(R.layout.item_liste, parent, false);
        return (new MyViewHolder(v,this));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // connecte la donnée au ViewHolder
        String mCurrent = mDataset.get(position);
        holder.textView.setText(mCurrent);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
