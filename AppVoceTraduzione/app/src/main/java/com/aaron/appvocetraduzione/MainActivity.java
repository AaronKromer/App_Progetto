package com.aaron.appvocetraduzione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //Variabili

    private ImageButton imageButton = null;
    private TextView Trascrizione = null;
    private Button mTranslateBtn = null;
    private TextView mSourceLang = null;
    private TextView mTranslatedText = null;
    private Spinner mSpinner = null;
    private Button mDelete = null;
    private Button mCopy = null;
    private Button mCopy2= null;
    private String sourceText = null;
    private int count=0;

    SpeechRecognizer speechRecognizer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Associamo i Widget alle istanze delle classi

        mTranslateBtn=findViewById(R.id.translate);
        mSourceLang=findViewById(R.id.sourceLang);
        imageButton=findViewById(R.id.button);
        Trascrizione=findViewById(R.id.edittext);
        mTranslatedText=findViewById(R.id.TranslatedText);
        mSpinner=findViewById(R.id.Spinner);
        mDelete=findViewById(R.id.delete);
        mCopy=findViewById(R.id.Copy);
        mCopy2=findViewById(R.id.Copy2);

        //Impostiamo lo spinner

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.Language, android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);

        //Impostiamo speechRecognizer
        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(this);

        // Settiamo un Intent associato all'azione ACTION_RECOGNIZE_SPEECH
        Intent speechRecognizerIntent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);


        //Funzionalità del bottone Copy relativo al testo tradotto

        mCopy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Value = mTranslatedText.getText().toString();
                if(Value.isEmpty()){
                    Toast.makeText(MainActivity.this, "Empty", Toast.LENGTH_SHORT).show();

                }
                else{

                    ClipboardManager clipboardManager=(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData=ClipData.newPlainText("Data",Value);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(MainActivity.this, "Copied", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Funzionalità del bottone Copy relativo al testo da tradurre
        mCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Value = Trascrizione.getText().toString();
                if(Value.isEmpty()){

                    Toast.makeText(MainActivity.this, "Empty", Toast.LENGTH_SHORT).show();

                }
                else{

                    ClipboardManager clipboardManager=(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData=ClipData.newPlainText("Data",Value);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(MainActivity.this, "Copied", Toast.LENGTH_SHORT).show();

                }
            }
        });

        //Funzionalità del bottone delete

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Value = Trascrizione.getText().toString();
                if(Value.isEmpty()){
                    Toast.makeText(MainActivity.this, "Already Empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    Trascrizione.setText("");
                    mTranslatedText.setText("");
                }
            }
        });

        //Funzionalità del bottone Translate

        mTranslateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                //chiama il metodo identifyLanguage
                identifyLanguage();
            }
        });



        //Funzionalità del bottone imageButton, modifica l'immagine una volta avviato l'ascolto
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(count==0)
                {
                    imageButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_mic_24));

                    //startlistening
                    speechRecognizer.startListening(speechRecognizerIntent);
                    count=1;
                }
                else
                {
                    imageButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_mic_off_24));
                    //stop listening
                    speechRecognizer.stopListening();
                    count=0;


                }

            }
        });


         final String TAG="RecognitionListener";

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {
                //inviamo un messaggio di errore
                Log.i(TAG,"Error");
            }

            @Override
            public void onResults(Bundle bundle) {
                //Passiamo il risultato dall'ascolto a data e lo inseriamo dentro Trascrizione
                ArrayList<String> data =bundle.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION);
                Trascrizione.setText(data.get(0));
        }

           @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });


        //Controllo permessi di registrazione

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},1);

        }

    }


    //Richiesta permessi di registrazione
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT);
            }
            else
            {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT);
            }
        }
    }


    //data la trascrizione identifica la lingua utilizzando Firebase
    private void identifyLanguage() {
        sourceText=Trascrizione.getText().toString();
        FirebaseLanguageIdentification identifier= FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
        mSourceLang.setText("Detecting");
        identifier.identifyLanguage(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if(s.equals("und")){
                    Toast.makeText(getApplicationContext(),"Language not identified", Toast.LENGTH_SHORT).show();
                }
                else{
                    //chiama il metodo getLanguageCode passandogli il codice della lingua identificata
                    getLanguageCode(s);
                }
            }
        });
    }


    //Assegna a mSourceLang la lingua identificata e chiama il metodo translateText passandogli il codice della lingua
    private void getLanguageCode(String language) {

        int langCode;
        switch (language){
            case "en":
                langCode= FirebaseTranslateLanguage.EN;
                mSourceLang.setText("English");
                break;
            case "it":
                langCode= FirebaseTranslateLanguage.IT;
                mSourceLang.setText("Italian");
                break;
            case "de":
                langCode= FirebaseTranslateLanguage.DE;
                mSourceLang.setText("German");
                break;
            case "el":
                langCode= FirebaseTranslateLanguage.EL;
                mSourceLang.setText("Greek");
                break;
            case "es":
                langCode= FirebaseTranslateLanguage.ES;
                mSourceLang.setText("Spanish");
                break;
            case "fr":
                langCode= FirebaseTranslateLanguage.FR;
                mSourceLang.setText("French");
                break;
            case "ru":
                langCode= FirebaseTranslateLanguage.RU;
                mSourceLang.setText("Russian");
                break;
            case "sq":
                langCode= FirebaseTranslateLanguage.SQ;
                mSourceLang.setText("Albanian");
                break;
            case "uk":
                langCode= FirebaseTranslateLanguage.UK;
                mSourceLang.setText("Ukranian");
                break;

            default:
                langCode=0;

        }
        translateText(langCode);
    }
    String lingua="";


    //data la lingua identificata traduce utilizzando Firebase

    private void translateText(int langCode) {
        mTranslatedText.setText("Translating");
        FirebaseTranslatorOptions options= null;
        switch (lingua){
            case "Italian":
                options = new FirebaseTranslatorOptions.Builder().setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.IT).build();
                break;
            case "English":
                options = new FirebaseTranslatorOptions.Builder().setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.EN).build();
                break;
            case "German":
                options = new FirebaseTranslatorOptions.Builder().setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.DE).build();
                break;
            case "Greek":
                options = new FirebaseTranslatorOptions.Builder().setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.EL).build();
                break;
            case "Spanish":
                options = new FirebaseTranslatorOptions.Builder().setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.ES).build();
                break;
            case "Albanian":
                options = new FirebaseTranslatorOptions.Builder().setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.SQ).build();
                break;
            case "French":
                options = new FirebaseTranslatorOptions.Builder().setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.FR).build();
                break;
            case "Russian":
                options = new FirebaseTranslatorOptions.Builder().setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.RU).build();
                break;
            case "Ukranian":
                options = new FirebaseTranslatorOptions.Builder().setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.UK).build();
                break;
            case "":
                Toast.makeText(this, "non funzina lingua", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "-- not selected", Toast.LENGTH_SHORT).show();

        }


        //se necessario installa il pacchetto della lingua

        final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                translator.translate(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
                    //Assegnamo a mTranslatedText il risultato della traduzione
                    @Override
                    public void onSuccess(String s) {
                        mTranslatedText.setText(s);
                    }
                });
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
         lingua = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}