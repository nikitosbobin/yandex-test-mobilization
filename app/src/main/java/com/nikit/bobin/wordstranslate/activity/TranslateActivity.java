package com.nikit.bobin.wordstranslate.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nikit.bobin.wordstranslate.App;
import com.nikit.bobin.wordstranslate.R;
import com.nikit.bobin.wordstranslate.logging.ILog;
import com.nikit.bobin.wordstranslate.translating.ITranslator;
import com.nikit.bobin.wordstranslate.translating.models.TranslatedText;
import com.nikit.bobin.wordstranslate.translating.models.Translation;

import org.jdeferred.DoneCallback;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TranslateActivity extends AppCompatActivity {
    @Inject ILog log;
    @Inject ITranslator translator;
    @BindView(R.id.editText) EditText input;
    @BindView(R.id.editText2) EditText output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        // dependency injection
        App.getComponent().injectsMainActivity(this);
        ButterKnife.bind(this);
    }

    public void onClick(View view) {
        translator
                .translateAsync(new Translation(input.getText().toString(), "en-ru"))
                .then(new DoneCallback<TranslatedText>() {
                    public void onDone(final TranslatedText result) {
                        if (!result.isSuccess()) {
                            Toast.makeText(TranslateActivity.this, "Translation failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        output.setText(result.getTranslatedText());
                        slideInOutputView();
                    }
                });
    }

    private void slideInOutputView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                output.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInLeft).duration(300).playOn(output);
            }
        });
    }
}
