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
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.switchmaterial.SwitchMaterial;
import android.util.Log;

/** Activity that showcases a challenge in vision impairment scenario. */
public class AuditoryChallengeActivity extends AppCompatActivity {
  private static final String TAG = "AuditoryChallengeActivity";
  private TextToSpeech textToSpeech;
  private int checkedChips = 0;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_auditory_challenge);
    getWindow()
        .setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

    TextView textTitle = findViewById(R.id.auditoryChallengeTitle);
    TextView textHeader = findViewById(R.id.auditoryChallengeHeader);
    TextView textBody = findViewById(R.id.auditoryChallengeText);
    textTitle.setText(Html.fromHtml(getString(R.string.auditory_challenge_title), Html.FROM_HTML_MODE_LEGACY));
    textHeader.setText(Html.fromHtml(getString(R.string.auditory_challenge_header), Html.FROM_HTML_MODE_LEGACY));
    textBody.setText(getString(R.string.auditory_challenge_body));

    setUpBackButtons();
    setUpAccessibleSwitch();
    setUpChips();

    textToSpeech = TextToSpeechFactory.createTextToSpeech(getApplicationContext());

    setUpIconTTS();
  }

  private void setUpIconTTS() {
    ImageView favorite = findViewById(R.id.auditoryChallengeIconFavorite);
    favorite.setOnClickListener(this::speakContentDescription);
    ImageView share = findViewById(R.id.auditoryChallengeIconShare);
    share.setOnClickListener(this::speakContentDescription);
    ImageView menu = findViewById(R.id.auditoryChallengeIconMenu);
    menu.setOnClickListener(this::speakContentDescription);
  }

  private void speakContentDescription(View view) {
    String data = view.getContentDescription().toString();
    int speechStatus = textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, /* params= */ null, "ContentDescriptionId");
    if (speechStatus == TextToSpeech.ERROR) {
      Log.v(TAG,"TTS error in converting Text to Speech!");
    }
    Toast toast = Toast.makeText(this, view.getContentDescription().toString(), Toast.LENGTH_SHORT);
    toast.setGravity(
        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, /* xOffset= */ 0, /* yOffset= */ 150);
    toast.show();
  }

  private void setUpBackButtons() {
    MaterialButton backToMenuButton = findViewById(R.id.auditoryChallengeBackToMenuButton);
    backToMenuButton.setOnClickListener(v -> backToMenu());
  }

  private void setUpAccessibleSwitch() {
    SwitchMaterial accessibleSwitch = findViewById(R.id.auditoryChallengeAccessibleSwitch);
    ImageView favorite = findViewById(R.id.auditoryChallengeIconFavorite);
    ImageView share = findViewById(R.id.auditoryChallengeIconShare);
    ImageView menu = findViewById(R.id.auditoryChallengeIconMenu);
    TextView textBody = findViewById(R.id.auditoryChallengeText);
    textBody.setAlpha(0.5f);
    accessibleSwitch.setOnCheckedChangeListener(
        (CompoundButton v, boolean isChecked) -> {
          if (isChecked) {
            favorite.setContentDescription(getString(R.string.icon_favorite_description));
            share.setContentDescription(getString(R.string.icon_share_description));
            menu.setContentDescription(getString(R.string.icon_menu_description));
            textBody.setAlpha(1.0f);
          } else {
            // Intentionally set inaccessible content descriptions and contrast ratio.
            favorite.setContentDescription(getString(R.string.button_description));
            share.setContentDescription(getString(R.string.button_description));
            menu.setContentDescription(getString(R.string.button_description));
            textBody.setAlpha(0.5f);
          }
        });
  }

  private void setUpChips() {
    Chip chipHealthy = findViewById(R.id.chipHealthy);
    Chip chipContrast = findViewById(R.id.chipContrast);
    Chip chipMacular = findViewById(R.id.chipMacular);
    ScrollView scrollView = findViewById(R.id.auditoryChallengeScrollView);
    ImageView macularGradient = findViewById(R.id.auditoryChallengeGradientMask);

    // Checking this chip unchecks all the other chips.
    chipHealthy.setOnCheckedChangeListener(
        (CompoundButton v, boolean isChecked) -> {
          if (isChecked) {
            chipContrast.setChecked(false);
            chipMacular.setChecked(false);
            checkedChips = 0;
          }
        });
    chipContrast.setOnCheckedChangeListener(
        (CompoundButton v, boolean isChecked) -> {
          if (isChecked) {
            // Lower the contrast of main text to mimic contrast sensitivity.
            scrollView.setAlpha(0.2f);
            chipHealthy.setChecked(false);
            checkedChips++;
          } else {
            scrollView.setAlpha(1.0f);
            checkedChips--;
            if (checkedChips == 0) {
              chipHealthy.setChecked(true);
            }
          }
        });
    chipMacular.setOnCheckedChangeListener(
        (CompoundButton v, boolean isChecked) -> {
          if (isChecked) {
            macularGradient.setVisibility(View.VISIBLE);
            chipHealthy.setChecked(false);
            checkedChips++;
          } else {
            macularGradient.setVisibility(View.GONE);
            checkedChips--;
            if (checkedChips == 0) {
              chipHealthy.setChecked(true);
            }
          }
        });
    chipHealthy.setChecked(true);
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
}
