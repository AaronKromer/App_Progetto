package com.aaron.identifylanguage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

public class MainActivity extends AppCompatActivity {


    private TextView mSourceLang;
    private EditText mSourcetext;
    private Button mTranslateBtn;
    private TextView mTranslatedText;

    private String sourceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSourceLang=findViewById(R.id.sourceLang);
        mSourcetext=findViewById(R.id.sourceText);
        mTranslateBtn=findViewById(R.id.translate);
        mTranslatedText=findViewById(R.id.TranslatedText);

        mTranslateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                identifyLanguage();
            }
        });

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