package com.example.piano;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.transition.Fade;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class Piano extends Activity
{
    private ImageButton[] botonNota = new ImageButton[12];
    private int[] botonesId = {R.id.D,R.id.Ds,R.id.R,R.id.Rs,R.id.M,R.id.F,R.id.Fs,R.id.S,R.id.Ss,R.id.L,R.id.Ls,R.id.Si};
    private int[] piano = {R.raw.pianodo, R.raw.pianodos, R.raw.pianore, R.raw.pianores,R.raw.pianomi, R.raw.pianofa,
            R.raw.pianofas, R.raw.pianoso,R.raw.pianosos, R.raw.pianola, R.raw.pianolas, R.raw.pianossi};
    private int[] guitarra = {R.raw.guitarrad, R.raw.guitarrads, R.raw.guitarrar, R.raw.guitarrars,R.raw.guitarram, R.raw.guitarraf,
            R.raw.guitarrafs, R.raw.guitarras,R.raw.guitarrass, R.raw.guitarral, R.raw.guitarrals, R.raw.guitarrasi};
    private int[] bateria = {R.raw.bateriad, R.raw.bateriads, R.raw.bateriar, R.raw.baterias,R.raw.bateriam, R.raw.bateriaf,
            R.raw.bateriafs, R.raw.baterias,R.raw.bateriass, R.raw.baterial, R.raw.baterials, R.raw.bateriasi};
    private int[] flauta = {R.raw.flautad, R.raw.flautads, R.raw.flautar, R.raw.flautars,R.raw.flautam, R.raw.flautaf,
            R.raw.flautafs, R.raw.flautas,R.raw.flautass, R.raw.flautal, R.raw.flautals, R.raw.flautasi};
    private int[] trompeta = {R.raw.trompetad, R.raw.trompetas, R.raw.trompetar, R.raw.trompetars,R.raw.trompetam, R.raw.trompetaf,
            R.raw.trompetafs, R.raw.trompetas,R.raw.trompetass, R.raw.trompetal, R.raw.trompetals, R.raw.trompetasi};

    //private MediaPlayer reproducir;
    private MediaRecorder grabacion;
    private String outputFile = null;
    //private File archivo;

    private AudioManager audio;
    private String instrumentoElegido = "Piano";
    private String[] valores = {"Piano","Guitarra","Bateria","Flauta","Trompeta"};

    //--------------------Imagenes con las notas------------------------------------
    private int[] imagenConNotas = {R.drawable.ndo,R.drawable.nds,R.drawable.nre,R.drawable.nrs,R.drawable.nmi,R.drawable.nfa,R.drawable.nfs,R.drawable.nsol,
                                    R.drawable.nss,R.drawable.nla,R.drawable.nls,R.drawable.nsi};
    //--------------------Imagenes presionadas con las notas------------------------------------
    private int[] imagenPConNotas = {R.drawable.ndop,R.drawable.nds,R.drawable.nrep,R.drawable.nrs,R.drawable.nmip,R.drawable.nfap,R.drawable.nfs,R.drawable.nsolp,
            R.drawable.nss,R.drawable.nlap,R.drawable.nls,R.drawable.nsip};
    //--------------------Imagenes sin las notas------------------------------------
    private int[] imagenSinNotas = {R.drawable.ni,R.drawable.na,R.drawable.nm,R.drawable.na,R.drawable.nd,R.drawable.ni,R.drawable.na,R.drawable.nm,R.drawable.na,
                                    R.drawable.nm,R.drawable.na,R.drawable.nd};
    //--------------------Imagenes presionadas sin las notas------------------------------------
    private int[] imagenPSinNotas = {R.drawable.nip,R.drawable.na,R.drawable.nmp,R.drawable.na,R.drawable.ndp,R.drawable.nip,R.drawable.na,R.drawable.nmp,R.drawable.na,
                                     R.drawable.nmp,R.drawable.na,R.drawable.ndp};
    private boolean mostrar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.piano);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat .requestPermissions(Piano.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1000);
        }

        Instrumento();
        Volumen();
        Sonar();
        Mostrar();
        Grabar();
        Pausar();
        Bucle();
    }

    //-----------------------------Funcion para variar el volumen--------------------------------
    public void Volumen()
    {
        try
        {
            SeekBar botonVolumen = findViewById(R.id.volumen);
            audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            botonVolumen.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            botonVolumen.setProgress(audio.getStreamVolume(AudioManager.STREAM_MUSIC));
            botonVolumen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b)
                {
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //----------------------------------Funcion para cambiar color a la tecla presionada-----------------------------
    public void Presion(int i)
    {   // !mostrar == false
        if (!mostrar)
        {
            if (i == 0)
                botonNota[0].setImageResource(imagenPSinNotas[i]);
            else if (i == 2)
                botonNota[2].setImageResource(imagenPSinNotas[i]);
            else if (i == 4)
                botonNota[4].setImageResource(imagenPSinNotas[i]);
            else if (i == 5)
                botonNota[5].setImageResource(imagenPSinNotas[i]);
            else if (i == 7)
                botonNota[7].setImageResource(imagenPSinNotas[i]);
            else if (i == 9)
                botonNota[9].setImageResource(imagenPSinNotas[i]);
            else if (i == 11)
                botonNota[11].setImageResource(imagenPSinNotas[i]);
        }
        else {
            if (i == 0)
                botonNota[0].setImageResource(imagenPConNotas[i]);
            else if (i == 1)
                botonNota[1].setImageResource(imagenPConNotas[i]);
            else if (i == 2)
                botonNota[2].setImageResource(imagenPConNotas[i]);
            else if (i == 3)
                botonNota[3].setImageResource(imagenPConNotas[i]);
            else if (i == 4)
                botonNota[4].setImageResource(imagenPConNotas[i]);
            else if (i == 5)
                botonNota[5].setImageResource(imagenPConNotas[i]);
            else if (i == 6)
                botonNota[6].setImageResource(imagenPConNotas[i]);
            else if (i == 7)
                botonNota[7].setImageResource(imagenPConNotas[i]);
            else if (i == 8)
                botonNota[8].setImageResource(imagenPConNotas[i]);
            else if (i == 9)
                botonNota[9].setImageResource(imagenPConNotas[i]);
            else if (i == 10)
                botonNota[10].setImageResource(imagenPConNotas[i]);
            else if (i == 11)
                botonNota[11].setImageResource(imagenPConNotas[i]);
        }
    }

    //----------------------------------Funcion para restablecer color a la tecla soltada-----------------------------
    public void Soltar(int i)
    {
        if (!mostrar)
        {
            if(i==0)
                botonNota[0].setImageResource(imagenSinNotas[i]);
            else if(i==2)
                botonNota[2].setImageResource(imagenSinNotas[i]);
            else if(i==4)
                botonNota[4].setImageResource(imagenSinNotas[i]);
            else if(i==5)
                botonNota[5].setImageResource(imagenSinNotas[i]);
            else if(i==7)
                botonNota[7].setImageResource(imagenSinNotas[i]);
            else if(i==9)
                botonNota[9].setImageResource(imagenSinNotas[i]);
            else if(i==11)
                botonNota[11].setImageResource(imagenSinNotas[i]);
        }
        else
        {
            if(i==0)
                botonNota[0].setImageResource(imagenConNotas[i]);
            else if(i==1)
                botonNota[1].setImageResource(imagenConNotas[i]);
            else if(i==2)
                botonNota[2].setImageResource(imagenConNotas[i]);
            else if(i==3)
                botonNota[3].setImageResource(imagenConNotas[i]);
            else if(i==4)
                botonNota[4].setImageResource(imagenConNotas[i]);
            else if(i==5)
                botonNota[5].setImageResource(imagenConNotas[i]);
            else if(i==6)
                botonNota[6].setImageResource(imagenConNotas[i]);
            else if(i==7)
                botonNota[7].setImageResource(imagenConNotas[i]);
            else if(i==8)
                botonNota[8].setImageResource(imagenConNotas[i]);
            else if(i==9)
                botonNota[9].setImageResource(imagenConNotas[i]);
            else if(i==10)
                botonNota[10].setImageResource(imagenConNotas[i]);
            else if(i==11)
                botonNota[11].setImageResource(imagenConNotas[i]);
        }

    }

    //----------------------------------Funcion para mostrar las notas en las teclas---------------------------
    public void Mostrar()
    {
        Button amarillo = findViewById(R.id.mostrar);
        amarillo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!mostrar)
                {
                    botonNota[0].setImageResource(imagenConNotas[0]);
                    botonNota[1].setImageResource(imagenConNotas[1]);
                    botonNota[2].setImageResource(imagenConNotas[2]);
                    botonNota[3].setImageResource(imagenConNotas[3]);
                    botonNota[4].setImageResource(imagenConNotas[4]);
                    botonNota[5].setImageResource(imagenConNotas[5]);
                    botonNota[6].setImageResource(imagenConNotas[6]);
                    botonNota[7].setImageResource(imagenConNotas[7]);
                    botonNota[8].setImageResource(imagenConNotas[8]);
                    botonNota[9].setImageResource(imagenConNotas[9]);
                    botonNota[10].setImageResource(imagenConNotas[10]);
                    botonNota[11].setImageResource(imagenConNotas[11]);
                    mostrar=true;
                }
                else
                {
                    botonNota[0].setImageResource(imagenSinNotas[0]);
                    botonNota[1].setImageResource(imagenSinNotas[1]);
                    botonNota[2].setImageResource(imagenSinNotas[2]);
                    botonNota[3].setImageResource(imagenSinNotas[3]);
                    botonNota[4].setImageResource(imagenSinNotas[4]);
                    botonNota[5].setImageResource(imagenSinNotas[5]);
                    botonNota[6].setImageResource(imagenSinNotas[6]);
                    botonNota[7].setImageResource(imagenSinNotas[7]);
                    botonNota[8].setImageResource(imagenSinNotas[8]);
                    botonNota[9].setImageResource(imagenSinNotas[9]);
                    botonNota[10].setImageResource(imagenSinNotas[10]);
                    botonNota[11].setImageResource(imagenSinNotas[11]);
                    mostrar=false;
                }
            }
        });
    }

    //-----------------------------Funcion para elegir instrumento------------------------------------
    public void Instrumento()
    {
        //private TextView pantalla;
        Spinner instrumento = findViewById(R.id.instrumentos);
        instrumento.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, valores));
        instrumento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
            {
                //pantalla.setText((String) adapterView.getItemAtPosition(position));
                instrumentoElegido = (String) adapterView.getItemAtPosition(position);
                //Toast.makeText(adapterView.getContext(), (String) adapterView.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    //-----------------------------Funcion para sonar la nota-------------------------------------
    public void inicializarPlayer(int n, String i)
    {
        switch (i)
        {
            case "Piano":
                MediaPlayer mediaplayer = MediaPlayer.create(this, piano[n]);
                mediaplayer.start();
                break;
            case "Guitarra":
                mediaplayer = MediaPlayer.create(this, guitarra[n]);
                mediaplayer.start();
                break;
            case "Bateria":
                mediaplayer = MediaPlayer.create(this, bateria[n]);
                mediaplayer.start();
                break;
            case "Flauta":
                mediaplayer = MediaPlayer.create(this, flauta[n]);
                mediaplayer.start();
                break;
            case "Trompeta":
                mediaplayer = MediaPlayer.create(this, trompeta[n]);
                mediaplayer.start();
                break;
        }
    }

    public void Grabar()
    {
        Button rojo = findViewById(R.id.grabar);
        rojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Grabacion.wav";
                // Se crea una instancia de MediaRecorder
                grabacion = new MediaRecorder();
                // Configuramos las fuentes de entrada
                grabacion.setAudioSource(MediaRecorder.AudioSource.MIC);
                // Seleccionamos el formato de salida
                grabacion.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                // Seleccionamos el codec de audio
                grabacion.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                // Especificamos el fichero de salida
                grabacion.setOutputFile(outputFile);
                try
                {
                    grabacion.prepare();
                    grabacion.start();
                } catch (IllegalStateException | IOException e)
                {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "La grabación comenzó", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void Pausar()
    {
        Button azul = findViewById(R.id.pausar);
        azul.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (grabacion != null)
                {
                    grabacion.stop();
                    grabacion.reset();
                    grabacion.release();
                    grabacion = null;
                    Toast.makeText(getApplicationContext(), "El audio  grabado con éxito", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void Bucle()
    {
        Button verde = findViewById(R.id.bucle);
        verde.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                MediaPlayer m = new MediaPlayer();
                try
                {
                    m.setDataSource(outputFile);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    m.prepare();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                m.start();
                m.setLooping(true);
                Toast.makeText(getApplicationContext(), "Reproducción de audio", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void Nota(final int i)
    {
        botonNota[i].setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    //boton presionado
                    if(i == 0 || i == 2 || i == 4 || i == 5 || i == 7 || i == 9 || i == 11)
                        Presion(i);
                    inicializarPlayer(i,instrumentoElegido);
                } else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    //boton liberado
                    if(i == 0 || i == 2 || i == 4 || i == 5 || i == 7 || i == 9 || i == 11)
                        Soltar(i);
                }
                return true;
            }
        });
    }

    public void Sonar()
    {
        for(int i=0; i<12; i++)
        {
            botonNota[i] = findViewById(botonesId[i]);
        }

        Nota(0);
        Nota(1);
        Nota(2);
        Nota(3);
        Nota(4);
        Nota(5);
        Nota(6);
        Nota(7);
        Nota(8);
        Nota(9);
        Nota(10);
        Nota(11);

    }
}