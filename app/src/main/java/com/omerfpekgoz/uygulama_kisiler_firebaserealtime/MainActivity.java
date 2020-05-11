package com.omerfpekgoz.uygulama_kisiler_firebaserealtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omerfpekgoz.uygulama_kisiler_firebaserealtime.adapter.KisilerAdapter;
import com.omerfpekgoz.uygulama_kisiler_firebaserealtime.model.Kisiler;

import java.util.ArrayList;


//Searc için arama menüsüne ekle
// app:actionViewClass="androidx.appcompat.widget.SearchView"

//Kisiler Adapter constructor a myref ekledik
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Toolbar toolbarMain;
    private RecyclerView recyclerViewKisiler;
    private FloatingActionButton fabKisi;

    private ArrayList<Kisiler> kisilerArrayList;
    private KisilerAdapter kisilerAdapter;

    private FirebaseDatabase database;
    private DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbarMain = findViewById(R.id.toolbarMain);
        recyclerViewKisiler = findViewById(R.id.recyclerKisiler);
        fabKisi = findViewById(R.id.fabKisi);


        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("kisiler");

        kisilerArrayList = new ArrayList<>();
        kisilerAdapter=new KisilerAdapter(MainActivity.this,kisilerArrayList,myRef);
        recyclerViewKisiler.setAdapter(kisilerAdapter);


        toolbarMain.setTitle("KİŞİLER");
        setSupportActionBar(toolbarMain);


        recyclerViewKisiler.setHasFixedSize(true);
        recyclerViewKisiler.setLayoutManager(new LinearLayoutManager(this));


        tumKisiler();


        fabKisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertView();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menuArama);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(MainActivity.this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        arama(newText);
        return false;
    }


    public void alertView() {


        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View alertView = layoutInflater.inflate(R.layout.alertview_tasarim, null);


        final EditText txtKisiAd = alertView.findViewById(R.id.txtKisiAd);
        final EditText txtKisiTel = alertView.findViewById(R.id.txtKisiTel);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Kişi Ekle");
        alertDialog.setView(alertView);
        alertDialog.setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String kisiAd = txtKisiAd.getText().toString().trim();
                String kisiTel = txtKisiTel.getText().toString().trim();

                String key=myRef.push().getKey();    //Burda anlık key aldık.Anlık silme ve güncelleme yapmak için kullanılarbilir

                kisiEkle(kisiAd, kisiTel);


                if (TextUtils.isEmpty(kisiAd)) {
                    Snackbar.make(alertView.getRootView(), "Kişi Adı Giriniz", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(kisiTel)) {
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

    public void tumKisiler() {

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                kisilerArrayList.clear();

                for (DataSnapshot d : dataSnapshot.getChildren()) {


                    Kisiler kisi = d.getValue(Kisiler.class);
                    kisi.setKisi_id(d.getKey());
                    kisilerArrayList.add(kisi);

                }
                kisilerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void kisiEkle(final String kisi_ad, final String kisi_tel) {

        Kisiler kisi = new Kisiler("", kisi_ad, kisi_tel);
        myRef.push().setValue(kisi);
        kisilerAdapter.notifyDataSetChanged();

    }

    public void arama(final String arananKisi) {

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                kisilerArrayList.clear();

                for (DataSnapshot d : dataSnapshot.getChildren()) {


                    Kisiler kisi = d.getValue(Kisiler.class);

                    if (kisi.getKisi_ad().contains(arananKisi)) {
                        kisi.setKisi_id(d.getKey());
                        kisilerArrayList.add(kisi);

                    }


                }
                kisilerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
