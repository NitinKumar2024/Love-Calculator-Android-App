package com.viddoer.lovecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        final ImageView imageView = findViewById(R.id.imageView);
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_animation);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.startAnimation(animation);
            }
        });

        // Retrieve result and percentage from intent extras
        String result = getIntent().getStringExtra("result");
        String percentage = getIntent().getStringExtra("percentage");
        String username = getIntent().getStringExtra("username");
        String crush = getIntent().getStringExtra("crush");

        final TextView textView1 = findViewById(R.id.textView1);
        final TextView textView2 = findViewById(R.id.textView2);
        final TextView textView3 = findViewById(R.id.textView3);
        final TextView textView4 = findViewById(R.id.textView4);

        textView1.setText(username);
        textView2.setText(percentage+ "%");
        textView3.setText(result);
        textView4.setText(crush);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Set language (you can change this based on your requirements)
                    int result = textToSpeech.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(ResultActivity.this, "Text-to-speech language is not supported.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ResultActivity.this, "Text-to-speech initialization failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });





        // Apply bouncing animation to TextView 1
        applyBouncingAnimation(textView1);

        // Apply bouncing animation to TextView 2
        applyBouncingAnimation(textView2);

        // Apply bouncing animation to TextView 3
        applyBouncingAnimation(textView3);

        // Apply bouncing animation to TextView 4
        applyBouncingAnimation(textView4);
    }

    private void convertToAudioAndShare(String result) {


        // Set UtteranceId to identify the speech synthesis process
        HashMap<String, String> params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");

        // Speak the text and set an UtteranceProgressListener
        textToSpeech.speak(result, TextToSpeech.QUEUE_FLUSH, params);


    }


    private void applyBouncingAnimation(final View view) {
        // Create ObjectAnimator to animate translationY property
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", -100f, 0f);
        animator.setDuration(1000); // Duration of the animation in milliseconds
        animator.setRepeatCount(ObjectAnimator.INFINITE); // Infinite repeat
        animator.setRepeatMode(ObjectAnimator.REVERSE); // Reverse the animation
        animator.start();
    }
}