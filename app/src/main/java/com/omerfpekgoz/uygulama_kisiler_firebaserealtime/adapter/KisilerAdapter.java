package com.omerfpekgoz.uygulama_kisiler_firebaserealtime.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.omerfpekgoz.uygulama_kisiler_firebaserealtime.R;
import com.omerfpekgoz.uygulama_kisiler_firebaserealtime.model.Kisiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class KisilerAdapter extends RecyclerView.Adapter<KisilerAdapter.cardViewHolder> {

    private Context mContext;
    private List<Kisiler> kisilerList;

    private FirebaseDatabase database;
    private DatabaseReference myRef;


    public KisilerAdapter() {
    }

    public KisilerAdapter(Context mContext, List<Kisiler> kisilerList,DatabaseReference myRef) {
        this.mContext = mContext;
        this.kisilerList = kisilerList;
        this.myRef=myRef;                           //Buaraya myRef ekledik

    }

    @NonNull
    @Override
    public cardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kisi_card_tasarim, parent, false);

        return new cardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final cardViewHolder holder, int position) {

        final Kisiler kisi = kisilerList.get(position);


        holder.txtKisiBilgi.setText(kisi.getKisi_ad() + " - " + kisi.getKisi_tel());

        holder.imageSecenek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(mContext, holder.imageSecenek);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {


                        switch (item.getItemId()) {

                            case R.id.menuSil:
                                Snackbar.make(holder.imageSecenek, "Kisi Silinsin Mi?", Snackbar.LENGTH_LONG)
                                        .setAction("Evet", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                kisiSil(kisi);

                                            }
                                        }).show();

                                return true;
                            case R.id.menuDuzenle:
                                alertView(kisi);

                                return true;
                            default:
                                return false;


                        }
                    }
                });
                popupMenu.show();


            }
        });


    }

    @Override
    public int getItemCount() {
        return kisilerList.size();
    }


    public class cardViewHolder extends RecyclerView.ViewHolder {

        private CardView cardViewKisiler;
        private TextView txtKisiBilgi;
        private ImageView imageSecenek;


        public cardViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewKisiler = itemView.findViewById(R.id.cardViewKisiler);
            txtKisiBilgi = itemView.findViewById(R.id.txtKisiBilgi);
            imageSecenek = itemView.findViewById(R.id.imageSecenek);

        }
    }

    public void kisiSil(final Kisiler kisi) {

        myRef.child(kisi.getKisi_id()).removeValue();

    }


    public void kisiDüzenle(final String kisiId, final String kisiAd, final String kisiTel) {

        Map<String, Object> kisiBilgi = new HashMap<>();

        kisiBilgi.put("kisi_id",kisiId);
        kisiBilgi.put("kisi_ad", kisiAd);
        kisiBilgi.put("kisi_tel", kisiTel);


        myRef.child(kisiId).updateChildren(kisiBilgi);


    }

    public void alertView(Kisiler kisi) {


        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final View alertView = layoutInflater.inflate(R.layout.alertview_tasarim, null);


        final EditText txtKisiAd = alertView.findViewById(R.id.txtKisiAd);
        final EditText txtKisiTel = alertView.findViewById(R.id.txtKisiTel);

        txtKisiAd.setText(kisi.getKisi_ad());
        txtKisiTel.setText(kisi.getKisi_tel());
        final String yenikisiId = kisi.getKisi_id();

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Kişi Düzenle");
        alertDialog.setView(alertView);
        alertDialog.setPositiveButton("Düzenle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                String yenikisiAd = txtKisiAd.getText().toString().trim();
                String yenikisiTel = txtKisiTel.getText().toString().trim();

                kisiDüzenle(yenikisiId, yenikisiAd, yenikisiTel);


                if (TextUtils.isEmpty(yenikisiAd)) {
                    Snackbar.make(alertView.getRootView(), "Kişi Adı Giriniz", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(yenikisiTel)) {
                    Snackbar.make(alertView.getRootView(), "Kişi Tel Giriniz", Snackbar.LENGTH_SHORT).show();
                    return;
                }


            }
        });
        alertDialog.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.create().show();

    }

}
