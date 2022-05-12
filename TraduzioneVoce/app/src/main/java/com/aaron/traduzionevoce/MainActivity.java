package com.aaron.traduzionevoce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

public class MainActivity extends AppCompatActivity {

    private ImageButton imageButton=null;

    private TextView mSourceLang=null;
    private EditText mSourcetext=null;
    private Button mTranslateBtn=null;
    private TextView mTranslatedText=null;

    private String sourceText=null;


    int count=0;
    SpeechRecognizer speechRecognizer=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSourceLang=findViewById(R.id.sourceLang);
        mSourcetext=findViewById(R.id.sourceText);
        mTranslateBtn=findViewById(R.id.translate);
        mTranslatedText=findViewById(R.id.TranslatedText);

        setContentView(R.layout.activity_main);

        imageButton=findViewById(R.id.btt);
        mTranslateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                identifyLanguage();
            }
        });

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},1);
        }


        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(this);


        Intent speechRecognizerIntent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count==0)
                {
                    imageButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_mic_24));

                    //startListening
                    speechRecognizer.startListening(speechRecognizerIntent);
                    count=1;
                }
                else
                {
                    imageButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_mic_off_24));

                    //stop Listening
                    speechRecognizer.stopListening();
                    count=0;
                }


            }
        });


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

            }

            @Override
            public void onResults(Bundle bundle) {

                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Toast.makeText(MainActivity.this, "son arrivato", Toast.LENGTH_SHORT).show();


                mSourcetext.setText(data.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if(requestCode==1)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT);
            }
            else
            {
                Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT);
            }
        }
    }
    private void identifyLanguage() {
        sourceText=mSourcetext.getText().toString();
        FirebaseLanguageIdentification identifier= FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
        mSourceLang.setText("detecting");
        identifier.identifyLanguage(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if(s.equals("und")){
                    Toast.makeText(getApplicationContext(),"Language not identified", Toast.LENGTH_SHORT).show();
                }
                else{
                    getLanguageCode(s);
                }
            }
        });
    }

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

    private void translateText(int langCode) {
        mTranslatedText.setText("Translating");
        FirebaseTranslatorOptions options= new FirebaseTranslatorOptions.Builder()
                //From language
                .setSourceLanguage(langCode)
                //to language
                .setTargetLanguage(FirebaseTranslateLanguage.EN)
                .build();

        final FirebaseTranslator translator= FirebaseNaturalLanguage.getInstance()
                .getTranslator(options);

        FirebaseModelDownloadConditions conditions= new FirebaseModelDownloadConditions.Builder()
                .build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                translator.translate(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        mTranslatedText.setText(s);
                    }
                });
            }
        });



    }
}