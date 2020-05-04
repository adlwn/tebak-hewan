package com.example.divianis.tebakhewan;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final int JUMLAH_HEWAN_YANG_DIMASUKKAN_KE_TEBAKAN = 10;

    private List<String> LIST_Semua_Nama_Hewan;
    private List<String> LIST_Nama_Hewan_Pada_Soal;

    // Untuk melakukan set pada (Interface) yang tidak boleh memiliki value duplikat / sama.
    private Set<String> jenisHewanYangDimasukkanSoal;
    private String jawabanYangBenar;
    private int jumlahDariSemuaTebakan;
    private int jumlahDariJawabanYangBenar;
    private int jumlahDariBarisTebakHewan;
    private SecureRandom pengacakanUntukSoal;
    private Handler handler;
    private Animation animasiKetikaJawabanSalah;
    private LinearLayout linearLayoutDariTebakHewan;
    private TextView txt_NomorSoal;
    private ImageView gambarHewan;
    private LinearLayout[] jumlahDariBarisTombolTebakHewan;
    private TextView txt_Jawaban;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);


        LIST_Semua_Nama_Hewan = new ArrayList<>();
        LIST_Nama_Hewan_Pada_Soal = new ArrayList<>();
        pengacakanUntukSoal = new SecureRandom();
        handler = new Handler();

        animasiKetikaJawabanSalah = AnimationUtils.loadAnimation(getActivity(), R.anim.animasi_jika_jawaban_salah);
        animasiKetikaJawabanSalah.setRepeatCount(1);

        linearLayoutDariTebakHewan = (LinearLayout) view.findViewById(R.id.linearLayoutDariTebakHewan);
        txt_NomorSoal = (TextView) view.findViewById(R.id.txt_NomorSoal);
        gambarHewan = (ImageView) view.findViewById(R.id.gambarHewan);

        jumlahDariBarisTombolTebakHewan = new LinearLayout[4];
        jumlahDariBarisTombolTebakHewan[0] = (LinearLayout) view.findViewById(R.id.firstRowLinearLayout);
        jumlahDariBarisTombolTebakHewan[1] = (LinearLayout) view.findViewById(R.id.secondRowLinearLayout);
        jumlahDariBarisTombolTebakHewan[2] = (LinearLayout) view.findViewById(R.id.thirdRowLinearLayout);
        jumlahDariBarisTombolTebakHewan[3] = (LinearLayout) view.findViewById(R.id.fourthRowLinearLayout);
        txt_Jawaban = (TextView) view.findViewById(R.id.txt_Jawaban);

        for (LinearLayout row : jumlahDariBarisTombolTebakHewan) {

            for (int column = 0; column < row.getChildCount(); column++) {

                Button tombolTebakan = (Button) row.getChildAt(column);
                tombolTebakan.setOnClickListener(tombolTebakanListener);
                tombolTebakan.setTextSize(24);

            }
        }

        txt_NomorSoal.setText(getString(R.string.teks_pertanyaan, 1, JUMLAH_HEWAN_YANG_DIMASUKKAN_KE_TEBAKAN));
        return view;

    }

    private View.OnClickListener tombolTebakanListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            Button tombolTebakan = ((Button) view);
            String nilaiTebakan = tombolTebakan.getText().toString();
            String nilaiJawaban = getNamaHewanYangBenar(jawabanYangBenar);
            ++jumlahDariSemuaTebakan;

            // when user guess the right answer

            if (nilaiTebakan.equals(nilaiJawaban)) {
                ++jumlahDariJawabanYangBenar;

                txt_Jawaban.setText(nilaiJawaban + " ! " + "BENAR");

                disableTombolTebakanYangAda();

                if (jumlahDariJawabanYangBenar == JUMLAH_HEWAN_YANG_DIMASUKKAN_KE_TEBAKAN) {

                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.app_name)
                            .setMessage(getString(R.string.nilai_hasil_bertipe_String, jumlahDariSemuaTebakan,
                                    1000 / (double) jumlahDariSemuaTebakan))
                            .setPositiveButton(R.string.reset_tebak_hewan, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    resetTebakHewan();


                                }
                            })
                            .setCancelable(false)
                            .show();
                    // user must click on reset the quiz
                    // animalQuizResults.setCancelable(false);
//                    animalQuizResults.show(getFragmentManager(), "AnimalQuizResults");

                }
                // when user choose wrong answer
                else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animasiPadaTebakHewan(true);
                        }

                    }, 250);
                }
            } else {

                gambarHewan.startAnimation(animasiKetikaJawabanSalah);

                txt_Jawaban.setText(R.string.pesan_jika_jawaban_salah);
                tombolTebakan.setEnabled(false);
            }
        }

    };
    // method di bawah ini akan get nama hewan yang benar dari assets folder
    // indexOf('-') + 1
    // replace('-',' ')

    private String getNamaHewanYangBenar(String namaHewan) {
        return namaHewan.substring(namaHewan.indexOf('-') + 1).replace('_', ' ');

    }
    // disabling buttons with non matching answers, when clicked once

    private void disableTombolTebakanYangAda() {
        for (int row = 0; row < jumlahDariBarisTebakHewan; row++) {

            LinearLayout guessRowLinearLayout = jumlahDariBarisTombolTebakHewan[row];
            for (int tombolIndex = 0; tombolIndex < guessRowLinearLayout.getChildCount(); tombolIndex++) {
                guessRowLinearLayout.getChildAt(tombolIndex).setEnabled(false);
            }
        }

    }

    public void resetTebakHewan() {

        AssetManager assets = getActivity().getAssets();
        LIST_Semua_Nama_Hewan.clear();

        // getting animal images and names from assets
        try {
            for (String jenisHewan : jenisHewanYangDimasukkanSoal) {

                String[] pathSemuaGambarHewanYangAda = assets.list(jenisHewan);

                for (String pathGambarHewanYangAda : pathSemuaGambarHewanYangAda) {

                    LIST_Semua_Nama_Hewan.add(pathGambarHewanYangAda.replace(".png", ""));
                }
            }
        } catch (IOException e) {
            Log.e("Tebak Hewan", "Error", e);
        }

        jumlahDariJawabanYangBenar = 0;
        jumlahDariSemuaTebakan = 0;
        LIST_Nama_Hewan_Pada_Soal.clear();

        int counter = 1;
        int jumlahDariHewanYangTersedia = LIST_Semua_Nama_Hewan.size();

        while (counter <= JUMLAH_HEWAN_YANG_DIMASUKKAN_KE_TEBAKAN) {
            int randomIndex = pengacakanUntukSoal.nextInt(jumlahDariHewanYangTersedia);
            String gambarNamaHewan = LIST_Semua_Nama_Hewan.get(randomIndex);

            if (!LIST_Nama_Hewan_Pada_Soal.contains(gambarNamaHewan)) {
                LIST_Nama_Hewan_Pada_Soal.add(gambarNamaHewan);
                ++counter;
            }
        }
        tampilkanHewanBerikutnya();
    }

    private void animasiPadaTebakHewan(boolean animasiOutGambarHewan) {
        if (jumlahDariJawabanYangBenar == 0) {

            return;
        }
        int xTopLeft = 0;
        int yTopLeft = 0;

        int xBottomRight = linearLayoutDariTebakHewan.getLeft() + linearLayoutDariTebakHewan.getRight();
        int yBottomRight = linearLayoutDariTebakHewan.getTop() + linearLayoutDariTebakHewan.getBottom();

        // Here is max value for radius
        int radius = Math.max(linearLayoutDariTebakHewan.getWidth(), linearLayoutDariTebakHewan.getHeight());

        Animator animator;

        if (animasiOutGambarHewan) {

            animator = ViewAnimationUtils.createCircularReveal(linearLayoutDariTebakHewan,
                    xBottomRight, yBottomRight, radius, 0);

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    tampilkanHewanBerikutnya();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

        } else {
            animator = ViewAnimationUtils.createCircularReveal(linearLayoutDariTebakHewan,
                    xTopLeft, yTopLeft, radius, 0);
        }

        animator.setDuration(250);
        animator.start();

    }

    private void tampilkanHewanBerikutnya() {
        String gambarNamaHewanBerikutnya = LIST_Nama_Hewan_Pada_Soal.remove(0);
        jawabanYangBenar = gambarNamaHewanBerikutnya;
        txt_Jawaban.setText("");

        txt_NomorSoal.setText(getString(R.string.teks_pertanyaan, jumlahDariJawabanYangBenar + 1,
                JUMLAH_HEWAN_YANG_DIMASUKKAN_KE_TEBAKAN));

        String jenisHewan = gambarNamaHewanBerikutnya.substring(0, gambarNamaHewanBerikutnya.indexOf("-"));

        AssetManager assets = getActivity().getAssets();

        // getting gambar hewan dari assets folder dan menampilkannya kepada user
        try (InputStream stream = assets.open(jenisHewan + "/" + gambarNamaHewanBerikutnya + ".png")) {

            Drawable animalImage = Drawable.createFromStream(stream, gambarNamaHewanBerikutnya);
            gambarHewan.setImageDrawable(animalImage);
            animasiPadaTebakHewan(false);
        } catch (IOException e) {
            Log.e("Tebak Hewan", "Terdapat error saat me-load gambar " + gambarNamaHewanBerikutnya, e);
        }

        Collections.shuffle(LIST_Semua_Nama_Hewan);

        // 'LIST_Semua_Nama_Hewan' dipilih bagian 'jawabanYangBenar' ke index 'namaHewanYangBenar'

        int namaHewanYangBenarIndex = LIST_Semua_Nama_Hewan.indexOf(jawabanYangBenar);
        String namaHewanYangBenar = LIST_Semua_Nama_Hewan.remove(namaHewanYangBenarIndex);
        LIST_Semua_Nama_Hewan.add(namaHewanYangBenar);

        for (int row = 0; row < jumlahDariBarisTebakHewan; row++) {

            // Enabling tombol

            for (int column = 0; column < jumlahDariBarisTombolTebakHewan[row].getChildCount();
                 column++) {

                Button tombolTebakan = (Button) jumlahDariBarisTombolTebakHewan[row].getChildAt(column);
                tombolTebakan.setEnabled(true);

                // Showing nama hewan di tombol

                String gambarNamaHewan = LIST_Semua_Nama_Hewan.get((row * 2) + column);
                tombolTebakan.setText(getNamaHewanYangBenar(gambarNamaHewan));
            }
        }
        // pengacakanUntukSoal akan meregenerasi random number DAN jumlahDariBarisTebakHewan
        // menunjukkan jumlah baris tebakan hewan
        // Di sini mengganti salah satu opsi tebakan dengan jawaban yang benar
        int row = pengacakanUntukSoal.nextInt(jumlahDariBarisTebakHewan);
        int column = pengacakanUntukSoal.nextInt(2);
        LinearLayout randomRow = jumlahDariBarisTombolTebakHewan[row];
        String gambarNamaHewanYangBenar = getNamaHewanYangBenar(namaHewanYangBenar);
        ((Button) randomRow.getChildAt(column)).setText(gambarNamaHewanYangBenar);


    }

    public void modifikasiBarisTebakHewan(SharedPreferences sharedPreferences) {

        final String JUMLAH_DARI_PILIHAN_TEBAKAN = sharedPreferences.getString(MainActivity.TEBAKAN, null);
        jumlahDariBarisTebakHewan = Integer.parseInt(JUMLAH_DARI_PILIHAN_TEBAKAN) / 2;

        for (LinearLayout horizontalLinearLayout : jumlahDariBarisTombolTebakHewan) {
            horizontalLinearLayout.setVisibility(View.GONE);
        }

        for (int row = 0; row < jumlahDariBarisTebakHewan; row++) {
            jumlahDariBarisTombolTebakHewan[row].setVisibility(View.VISIBLE);

        }

    }

    public void modifikasiJenisHewanYangDitebak(SharedPreferences sharedPreferences) {
        jenisHewanYangDimasukkanSoal = sharedPreferences.getStringSet(MainActivity.JENIS_DARI_HEWAN, null);
    }

    public void modifikasiJenisFont(SharedPreferences sharedPreferences) {

        String fontStringValue = sharedPreferences.getString(MainActivity.JENIS_FONT, null);

        switch (fontStringValue) {

            case "Fontleroy Brown.ttf":

                for (LinearLayout row : jumlahDariBarisTombolTebakHewan) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setTypeface(MainActivity.fontlerybrown);
                    }
                }
                break;

            case "Wonderbar Demo.otf":

                for (LinearLayout row : jumlahDariBarisTombolTebakHewan) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setTypeface(MainActivity.wonderbarDemo);
                    }
                }
                break;
        }
    }

    public void modifikasiWarnaDariBackground(SharedPreferences sharedPreferences) {

        String bgColor = sharedPreferences.getString(MainActivity.WARNA_DARI_BACKGROUND, null);

        switch (bgColor) {

            case "Putih":
                linearLayoutDariTebakHewan.setBackgroundColor(Color.WHITE);

                for (LinearLayout row : jumlahDariBarisTombolTebakHewan) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.BLUE);
                        button.setTextColor(Color.WHITE);
                    }
                }

                txt_Jawaban.setTextColor(Color.BLUE);
                txt_NomorSoal.setTextColor(Color.BLACK);

                break;

            case "Hitam":
                linearLayoutDariTebakHewan.setBackgroundColor(Color.BLACK);

                for (LinearLayout row : jumlahDariBarisTombolTebakHewan) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.YELLOW);
                        button.setTextColor(Color.BLACK);
                    }
                }

                txt_Jawaban.setTextColor(Color.WHITE);
                txt_NomorSoal.setTextColor(Color.WHITE);

                break;

            case "Hijau":
                linearLayoutDariTebakHewan.setBackgroundColor(Color.GREEN);

                for (LinearLayout row : jumlahDariBarisTombolTebakHewan) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.BLUE);
                        button.setTextColor(Color.WHITE);
                    }
                }

                txt_Jawaban.setTextColor(Color.BLACK);
                txt_NomorSoal.setTextColor(Color.BLACK);

                break;

            case "Biru":
                linearLayoutDariTebakHewan.setBackgroundColor(Color.BLUE);

                for (LinearLayout row : jumlahDariBarisTombolTebakHewan) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.RED);
                        button.setTextColor(Color.WHITE);
                    }
                }

                txt_Jawaban.setTextColor(Color.WHITE);
                txt_NomorSoal.setTextColor(Color.WHITE);

                break;

            case "Merah":
                linearLayoutDariTebakHewan.setBackgroundColor(Color.RED);

                for (LinearLayout row : jumlahDariBarisTombolTebakHewan) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.BLUE);
                        button.setTextColor(Color.WHITE);
                    }
                }

                txt_Jawaban.setTextColor(Color.WHITE);
                txt_NomorSoal.setTextColor(Color.WHITE);

                break;

            case "Kuning":
                linearLayoutDariTebakHewan.setBackgroundColor(Color.YELLOW);

                for (LinearLayout row : jumlahDariBarisTombolTebakHewan) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.WHITE);
                        button.setTextColor(Color.BLACK);
                    }
                }

                txt_Jawaban.setTextColor(Color.BLACK);
                txt_NomorSoal.setTextColor(Color.BLACK);

                break;


        }
    }
}