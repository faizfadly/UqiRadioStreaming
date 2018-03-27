package id.fdev.example.uqifm.data;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import id.fdev.example.uqifm.ItemClickListener;
import id.fdev.example.uqifm.R;


/**
 * Created by faiz on 22/09/17.
 */
public class RecyclerAdapterDjs extends  RecyclerView.Adapter<RecyclerAdapterDjs.ViewHolder> {
    private ArrayList<Data> listdata;
    private Activity activity;
    private Context context;

    private ItemClickListener clickListener;

    public RecyclerAdapterDjs(Activity activity, ArrayList<Data> listdata) {
        this.listdata = listdata;
        this.activity = activity;
    }

    @Override
    public RecyclerAdapterDjs.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_djs, parent, false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //holder.mImage.setImageResource(listdata.get(position).getThubnail());

        holder.id.setText(listdata.get(position).getId());
        holder.nama.setText(listdata.get(position).getNama());
        holder.bio.setText(listdata.get(position).getBio());
        final ViewHolder x=holder;
        Glide.with(activity)
                .load(listdata.get(position).getThubnail())
                .into(holder.thumbnail);
        holder.id.setVisibility(View.GONE);
    }
    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView cv;
        private TextView id,nama,bio;
        private ImageView thumbnail;

        public ViewHolder(View v) {
            super(v);
            cv=(CardView)v.findViewById(R.id.card_view);
            id=(TextView)v.findViewById(R.id.id);
            nama=(TextView)v.findViewById(R.id.nama);
            bio=(TextView)v.findViewById(R.id.bio);
            thumbnail=(ImageView)v.findViewById(R.id.thumbnail);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }


    }
}
