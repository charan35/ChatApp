package com.example.altachatapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.altachatapp.model.FetchContacts;
import com.example.altachatapp.model.ListContact;

import java.util.ArrayList;
import java.util.Collection;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    public ListContact cont;
    FetchContacts list;
    private ArrayList<FetchContacts> arraylist;
    boolean checked = false;
    View vv;


    public RecyclerAdapter(LayoutInflater inflater, ListContact listContact) {
        this.layoutInflater = inflater;
        this.cont = listContact;
        this.arraylist = new ArrayList<FetchContacts>();
        this.arraylist.addAll((Collection<? extends FetchContacts>) cont);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.contactlist, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String name=cont.getListContact().get(position).name;

        holder.title.setText(name);
        holder.phone.setText(cont.getListContact().get(position).phone);

    }

    @Override
    public int getItemCount() {
        return cont.getListContact().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView phone;
        //public ImageView image;
        public LinearLayout contact_select_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.setIsRecyclable(false);
            title = (TextView) itemView.findViewById(R.id.txtName);
            phone = (TextView) itemView.findViewById(R.id.txtPhone);
            //image=(ImageView) itemView.findViewById(R.id.icon_avata);


        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}