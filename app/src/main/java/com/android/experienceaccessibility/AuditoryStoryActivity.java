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
import com.android.experienceaccessibility.auditory.AuditoryStoryMapModel;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.material.button.MaterialButton;
import java.util.HashMap;
import java.util.Map;

/** Activity that handles all four auditory story activities. */
public class AuditoryStoryActivity extends AppCompatActivity {
  private SimpleExoPlayer player;
  private AuditoryStoryMapModel storyModel;

  private static final Map<String, AuditoryStoryMapModel> STORY_MODELS = new HashMap<>();

  public static final String STORY_KEY = "story";
  public static final String STORY_BLINDNESS_ID = "blindness";
  public static final String STORY_DEAFNESS_ID = "deafness";
  public static final String STORY_MOTOR_IMPAIRMENT_ID = "motorImpairment";
  public static final String STORY_LEARNING_DISABILITY_ID = "learningDisability";

  static {
    STORY_MODELS.put(
        STORY_BLINDNESS_ID,
        new AuditoryStoryMapModel(
            "file:///android_asset/blind_story_subtitles.srt",
            R.raw.blind_story_video,
            AuditoryChallengeActivity.class));
    STORY_MODELS.put(
        STORY_DEAFNESS_ID,
        new AuditoryStoryMapModel(
            "file:///android_asset/deaf_story_subtitles.srt",
            R.raw.deaf_story_video,
            AuditoryChallengeDeafActivity.class));
    STORY_MODELS.put(
        STORY_MOTOR_IMPAIRMENT_ID,
        new AuditoryStoryMapModel(
            "file:///android_asset/motor_impairment_story_subtitles.srt",
            R.raw.motor_impairment_story_video,
            AuditoryChallengeMotorImpairmentActivity.class));
    STORY_MODELS.put(
        STORY_LEARNING_DISABILITY_ID,
        new AuditoryStoryMapModel(
            "file:///android_asset/learning_disability_story_subtitles.srt",
            R.raw.learning_disability_story_video,
            AuditoryChallengeLearningDisabilityActivity.class));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    String selectedStory = getIntent().getStringExtra(STORY_KEY);
    storyModel = STORY_MODELS.get(selectedStory);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_auditory_story_video);

    setUpMuteButton();
    setUpBackAndNextButtons();
    setUpPlayPauseButton();

    player =
        ExoPlayerFactory.createExoPlayer(
            getApplicationContext(),
            storyModel.getSubtitlesPath(),
            storyModel.getVideoResourceId());

    setUpExoPlayerStateChangedListener();

    StyledPlayerView playerView = findViewById(R.id.playerView);
    playerView.setUseController(false);
    playerView.setPlayer(player);
  }

  private void setUpExoPlayerStateChangedListener() {
    player.addListener(
        new EventListener() {
          @Override
          public void onPlaybackStateChanged(int state) {
            if (state == SimpleExoPlayer.STATE_ENDED) {
              startAuditoryChallengeActivity();
            }
          }
        });
  }

  private void setUpMuteButton() {
    MaterialButton muteButton = findViewById(R.id.auditoryStoryMuteButton);
    muteButton.addOnCheckedChangeListener(
        (MaterialButton v, boolean isMuted) -> {
          if (isMuted) {
            player.setVolume(0);
          } else {
            player.setVolume(1);
          }
        });
  }

  private void setUpPlayPauseButton() {
    MaterialButton playPauseButton = findViewById(R.id.playPauseButton);
    playPauseButton.addOnCheckedChangeListener(
        (MaterialButton v, boolean isPaused) -> {
          if (isPaused) {
            player.pause();
            playPauseButton.setContentDescription(getString(R.string.play_video));
          } else {
            player.play();
            playPauseButton.setContentDescription(getString(R.string.pause_video));
          }
        });
  }

  private void setUpBackAndNextButtons() {
    MaterialButton nextButton = findViewById(R.id.auditoryStoryNextButton);
    nextButton.setOnClickListener(v -> startAuditoryChallengeActivity());
  }

  public void startAuditoryChallengeActivity() {
    Intent intent = new Intent(this, storyModel.getClazz());
    startActivity(intent);
  }

  private void backToMenu() {
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
