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
import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

/** Activity that showcases a challenge in deaf scenario.. */
public class AuditoryChallengeDeafActivity extends AppCompatActivity {
  private static final String SUBTITLE_LANGUAGE = "en";
  private SimpleExoPlayer player;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_auditory_challenge_deaf);
    getWindow()
        .setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setUpBackButtons();

    TextView deafInstructions = findViewById(R.id.auditoryChallengeDeafDescription);
    deafInstructions.setText(R.string.auditory_challenge_deaf_description);
    player =
        ExoPlayerFactory.createExoPlayer(
            getApplicationContext(),
            "file:///android_asset/deaf_challenge_subtitles.srt",
            R.raw.deaf_challenge_video);

    setUpAccessibleSwitch();

    player.setRepeatMode(Player.REPEAT_MODE_ALL);
    StyledPlayerView playerView = findViewById(R.id.playerView);
    playerView.setPlayer(player);
    playerView.setUseController(false);
    playerView.setKeepScreenOn(true);
  }

  private void setUpBackButtons() {
    MaterialButton backToMenuButton = findViewById(R.id.auditoryChallengeDeafBackToMenuButton);
    backToMenuButton.setOnClickListener(v -> backToMenu());
  }

  private void setUpAccessibleSwitch() {
    SwitchMaterial accessibleSwitch = findViewById(R.id.auditoryChallengeDeafAccessibleSwitch);

    accessibleSwitch.setOnCheckedChangeListener(
        (CompoundButton v, boolean isChecked) -> switchSubtitles(isChecked));
  }

  private void switchSubtitles(boolean showSubtitles) {
    // Track selector is needed so we can toggle the subtitles on
    // and off through it as there is no 'simple' way of doing it.
    DefaultTrackSelector trackSelector = (DefaultTrackSelector) player.getTrackSelector();

    trackSelector.setParameters(
        trackSelector
            .buildUponParameters()
            .setPreferredTextLanguage(showSubtitles ? SUBTITLE_LANGUAGE : null));
  }

  private void backToMenu() {
    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
    finish();
  }

  @Override
  public void onResume() {
    super.onResume();
    player.play();
  }

  @Override
  public void onPause() {
    super.onPause();
    player.pause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    player.release();
  }
}
