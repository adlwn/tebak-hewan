package com.example.divianis.tebakhewan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {


    public static final String TEBAKAN = "pengaturan_jumlahDariTebakan";
    public static final String JENIS_DARI_HEWAN = "pengaturan_jenisDariHewan";
    public static final String WARNA_DARI_BACKGROUND = "pengaturan_warnaDariBackground";
    public static final String JENIS_FONT = "pengaturan_jenisFont";


    private boolean apakahPengaturanTelahBerubah = false;

    static Typeface fontlerybrown;
    static Typeface wonderbarDemo;


    MainActivityFragment bagianTebakGambarFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fontlerybrown = Typeface.createFromAsset(getAssets(), "fonts/Fontleroy Brown.ttf");
        wonderbarDemo = Typeface.createFromAsset(getAssets(), "fonts/Wonderbar Demo.otf");


        PreferenceManager.setDefaultValues(MainActivity.this, R.xml.preferensi_tebakan, false);


        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).
                registerOnSharedPreferenceChangeListener(settingsChangeListener);


        bagianTebakGambarFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.animalQuizFragment);

        bagianTebakGambarFragment.modifikasiBarisTebakHewan(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        bagianTebakGambarFragment.modifikasiJenisHewanYangDitebak(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        bagianTebakGambarFragment.modifikasiJenisFont(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        bagianTebakGambarFragment.modifikasiWarnaDariBackground(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        bagianTebakGambarFragment.resetTebakHewan();
        apakahPengaturanTelahBerubah = false;






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate Menu = menambahkan item ke action panel jika ada.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent preferencesIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(preferencesIntent);
        return super.onOptionsItemSelected(item);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener settingsChangeListener
            = new SharedPreferences.OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            apakahPengaturanTelahBerubah = true;

            if (key.equals(TEBAKAN)) {

                bagianTebakGambarFragment.modifikasiBarisTebakHewan(sharedPreferences);
                bagianTebakGambarFragment.resetTebakHewan();

            } else if (key.equals(JENIS_DARI_HEWAN)) {

                Set<String> jenisHewan = sharedPreferences.getStringSet(JENIS_DARI_HEWAN, null);

                if (jenisHewan != null && jenisHewan.size() > 0) {

                    bagianTebakGambarFragment.modifikasiJenisHewanYangDitebak(sharedPreferences);
                    bagianTebakGambarFragment.resetTebakHewan();

                } else {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    jenisHewan.add(getString(R.string.jenis_hewan_defaultnya_HEWAN_BUAS));
                    editor.putStringSet(JENIS_DARI_HEWAN, jenisHewan);
                    editor.apply();

                    Toast.makeText(MainActivity.this, R.string.toast_message, Toast.LENGTH_SHORT).show();

                }

            } else if (key.equals(JENIS_FONT)) {

                bagianTebakGambarFragment.modifikasiJenisFont(sharedPreferences);
                bagianTebakGambarFragment.resetTebakHewan();
            } else if (key.equals(WARNA_DARI_BACKGROUND)) {

                bagianTebakGambarFragment.modifikasiWarnaDariBackground(sharedPreferences);
                bagianTebakGambarFragment.resetTebakHewan();

            }

            Toast.makeText(MainActivity.this, R.string.change_message, Toast.LENGTH_SHORT).show();




        }
    };

}
