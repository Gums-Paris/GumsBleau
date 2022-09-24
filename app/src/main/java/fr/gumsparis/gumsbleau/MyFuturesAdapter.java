package fr.gumsparis.gumsbleau;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyFuturesAdapter extends RecyclerView.Adapter<MyFuturesAdapter.MyViewHolder> {

/* Adapteur quasi standard grace à l'utilisation du RecyclerViewClickListener. Outre le nom de classe, ce qui change est :
*   - le type de lesData et laListe
*   - l'id du layout affecté au ViewHolder de l'item et dans ce layout l'id du TextView qui affichera le nom de l'item
*   - l'instruction qui fabrique le nom de l'item à afficher dans le TestView  */

    final private ArrayList<Sortie> lesData;
    final LayoutInflater mInflater;
    final RecyclerViewClickListener listener;
    Resources resources;

    MyFuturesAdapter(Context context, ArrayList<Sortie> laListe, RecyclerViewClickListener mlistener) {
        mInflater = LayoutInflater.from(context);
        this.lesData = laListe;
        this.listener = mlistener;
        resources = MyHelper.getInstance().recupResources();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        final MyFuturesAdapter mAdapter;
        final RecyclerViewClickListener mListener;

        MyViewHolder(View view, MyFuturesAdapter adapter, RecyclerViewClickListener listener) {
            super(view);
            textView = view.findViewById(R.id.future_sortie);
            this.mAdapter = adapter;
            mListener = listener;
            textView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            int mPosition = getLayoutPosition();
            mListener.onClick(view, mPosition);
        }
    }

    @NonNull
    @Override
    public MyFuturesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.futuritem_liste, parent, false);
        return (new MyFuturesAdapter.MyViewHolder(v,this, listener));
    }

    @Override
    public void onBindViewHolder(@NonNull MyFuturesAdapter.MyViewHolder holder, int position) {
        Sortie mCurrent = lesData.get(position);
        String afficheSortie = resources.getString(R.string.affichesortie,mCurrent.getDateSortie(),mCurrent.getLieuSortie());
        holder.textView.setText(afficheSortie);
    }

    @Override
    public int getItemCount() {
        return lesData.size();
    }
}
