// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.android.experienceaccessibility;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

/** Activity that showcases a challenge in learning disability scenario. */
public class AuditoryChallengeLearningDisabilityActivity extends AppCompatActivity {
  private static final String TAG = "AuditoryChallengeLearningDisabilityActivity";
  private static final BackgroundColorSpan HIGHLIGHT_COLOUR = new BackgroundColorSpan(Color.YELLOW);

  private TextToSpeech textToSpeech;
  private boolean enableHighlight = false;
  private String currentUtteranceId;
  private int utteranceCounter = 0;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_auditory_challenge_learning_disability);
    getWindow()
        .setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

    TextView textTitle = findViewById(R.id.auditoryChallengeLearningDisabilityTitle);
    TextView textBody = findViewById(R.id.auditoryChallengeLearningDisabilityText);
    textTitle.setText(
        Html.fromHtml(getString(R.string.auditory_challenge_learning_disability_title), Html.FROM_HTML_MODE_LEGACY));

    textBody.setText(
        new SpannableString(
            getString(R.string.auditory_challenge_learning_disability_difficult_instruction)),
        BufferType.SPANNABLE);

    MaterialButton play = findViewById(R.id.auditoryChallengeLearningDisabilityPlay);
    MaterialButton highlight = findViewById(R.id.auditoryChallengeLearningDisabilityHighlight);

    play.setContentDescription(getString(R.string.icon_play_description));
    highlight.setContentDescription(getString(R.string.icon_highlight_description));

    setUpBackButtons();
    setUpAccessibleSwitch();

    // Setup listener to highlight spoken words
    UtteranceProgressListener listener = createUtteranceListener(textBody);

    textToSpeech = TextToSpeechFactory.createTextToSpeech(getApplicationContext());
    textToSpeech.setOnUtteranceProgressListener(listener);

    play.setOnClickListener(v -> speakText(textBody.getText()));
    highlight.addOnCheckedChangeListener((v, checked) -> enableHighlight = checked);
  }

  private String nextUtteranceId() {
    currentUtteranceId = "LearningDisabilityId-" + utteranceCounter;
    utteranceCounter++;
    return currentUtteranceId;
  }

  private UtteranceProgressListener createUtteranceListener(TextView textBody) {
    return new UtteranceProgressListener() {

      @Override
      public void onStart(String utteranceId) {}

      @Override
      public void onDone(String utteranceId) {
        runOnUiThread(() -> {
          Spannable highlightedText = getSpannableTextBody();
          highlightedText.removeSpan(HIGHLIGHT_COLOUR);
        });
      }

      @Override
      public void onError(String utteranceId) {
        Log.v(TAG,"Utterance listener error! Utterance ID: "+ utteranceId);
      }

      @Override
      public void onRangeStart(String utteranceId, int start, int end, int frame) {
        if (!currentUtteranceId.equals(utteranceId)) {
          return;
        }
        runOnUiThread(() -> {
          Spannable highlightedText = getSpannableTextBody();
          if (enableHighlight) {
            highlightedText.setSpan(
                HIGHLIGHT_COLOUR, start, end + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
          } else {
            highlightedText.removeSpan(HIGHLIGHT_COLOUR);
          }
          textBody.invalidate();
        });
      }

      private Spannable getSpannableTextBody() {
        return (Spannable) textBody.getText();
      }
    };
  }

  private void speakText(CharSequence text) {
    // onRangeStart breaks on full stop, see https://issuetracker.google.com/issues/174605128
    String noFullStopsText = text.toString().replace('.', ';');
    int speechStatus =
        textToSpeech.speak(noFullStopsText, TextToSpeech.QUEUE_FLUSH, null, nextUtteranceId());
    if (speechStatus == TextToSpeech.ERROR) {
      Log.v(TAG,"TTS error in converting Text to Speech!");
    }
  }

  private void setUpBackButtons() {
    MaterialButton backToMenuButton =
        findViewById(R.id.auditoryChallengeLearningDisabilityBackToMenuButton);
    backToMenuButton.setOnClickListener(v -> backToMenu());
  }

  private void setUpAccessibleSwitch() {
    TextView textBody = findViewById(R.id.auditoryChallengeLearningDisabilityText);
    SwitchMaterial accessibleSwitch =
        findViewById(R.id.auditoryChallengeLearningDisabilityAccessibleSwitch);
    accessibleSwitch.setOnCheckedChangeListener(
        (CompoundButton v, boolean isChecked) -> {
          textToSpeech.stop();

          if (isChecked) {
            textBody.setText(
                getString(R.string.auditory_challenge_learning_disability_easy_instruction));
          } else {
            textBody.setText(
                getString(R.string.auditory_challenge_learning_disability_difficult_instruction));
          }
        });
  }

  private void backToMenu() {
    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
    finish();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (textToSpeech != null) {
      textToSpeech.stop();
      textToSpeech.shutdown();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (textToSpeech != null) {
      textToSpeech.stop();
    }
  }
}
