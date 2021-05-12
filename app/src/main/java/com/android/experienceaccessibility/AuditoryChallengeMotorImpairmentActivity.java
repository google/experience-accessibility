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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Activity that showcases a challenge in motor impairment scenario. */
public class AuditoryChallengeMotorImpairmentActivity extends AppCompatActivity {
  private static final int NUMBER_OF_CHECKBOXES = 6;

  private final Random random = new Random();
  private final List<ViewGroup> checkBoxParents = new ArrayList<>();

  // Separate text&checkbox to better simulate an inaccessible design.
  private final List<CheckBox> checkBoxes = new ArrayList<>();
  private final List<TextView> checkBoxTexts = new ArrayList<>();

  private final AnimatorSet animation = new AnimatorSet();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_auditory_challenge_motor_impairment);
    getWindow()
        .setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setUpBackButtons();
    setUpCheckBoxes();
    setUpAccessibleSwitch();
    setUpChips();
  }

  private void setUpBackButtons() {
    //MaterialButton backToStoryButton = findViewById(R.id.auditoryChallengeMotorBackToStoryButton);
    //backToStoryButton.setOnClickListener(v -> backToStory());
    MaterialButton backToMenuButton = findViewById(R.id.auditoryChallengeMotorBackToMenuButton);
    backToMenuButton.setOnClickListener(v -> backToMenu());
  }

  private void setUpCheckBoxes() {
    checkBoxes.add(findViewById(R.id.ChallengeMotorImpairmentCheckbox1));
    checkBoxes.add(findViewById(R.id.ChallengeMotorImpairmentCheckbox2));
    checkBoxes.add(findViewById(R.id.ChallengeMotorImpairmentCheckbox3));
    checkBoxes.add(findViewById(R.id.ChallengeMotorImpairmentCheckbox4));
    checkBoxes.add(findViewById(R.id.ChallengeMotorImpairmentCheckbox5));
    checkBoxes.add(findViewById(R.id.ChallengeMotorImpairmentCheckbox6));
    checkBoxTexts.add(findViewById(R.id.ChallengeMotorImpairmentCheckboxText1));
    checkBoxTexts.add(findViewById(R.id.ChallengeMotorImpairmentCheckboxText2));
    checkBoxTexts.add(findViewById(R.id.ChallengeMotorImpairmentCheckboxText3));
    checkBoxTexts.add(findViewById(R.id.ChallengeMotorImpairmentCheckboxText4));
    checkBoxTexts.add(findViewById(R.id.ChallengeMotorImpairmentCheckboxText5));
    checkBoxTexts.add(findViewById(R.id.ChallengeMotorImpairmentCheckboxText6));
    checkBoxParents.add(findViewById(R.id.ChallengeMotorImpairmentCheckboxParent1));
    checkBoxParents.add(findViewById(R.id.ChallengeMotorImpairmentCheckboxParent2));
    checkBoxParents.add(findViewById(R.id.ChallengeMotorImpairmentCheckboxParent3));
    checkBoxParents.add(findViewById(R.id.ChallengeMotorImpairmentCheckboxParent4));
    checkBoxParents.add(findViewById(R.id.ChallengeMotorImpairmentCheckboxParent5));
    checkBoxParents.add(findViewById(R.id.ChallengeMotorImpairmentCheckboxParent6));
  }

  private void setUpAccessibleSwitch() {
    SwitchMaterial accessibleSwitch = findViewById(R.id.auditoryChallengeMotorAccessibleSwitch);
    accessibleSwitch.setOnCheckedChangeListener(
        (CompoundButton v, boolean isChecked) -> {
          for (int i = 0; i < NUMBER_OF_CHECKBOXES; i++) {
            expandTouchArea(checkBoxParents.get(i), checkBoxes.get(i), isChecked ? 100 : -100);
          }
          checkBoxTexts.get(0).setText(isChecked ? "" : getString(R.string.motor_checkbox_1));
          checkBoxTexts.get(1).setText(isChecked ? "" : getString(R.string.motor_checkbox_2));
          checkBoxTexts.get(2).setText(isChecked ? "" : getString(R.string.motor_checkbox_3));
          checkBoxTexts.get(3).setText(isChecked ? "" : getString(R.string.motor_checkbox_4));
          checkBoxTexts.get(4).setText(isChecked ? "" : getString(R.string.motor_checkbox_5));
          checkBoxTexts.get(5).setText(isChecked ? "" : getString(R.string.motor_checkbox_6));
          checkBoxes.get(0).setText(!isChecked ? "" : getString(R.string.motor_checkbox_1));
          checkBoxes.get(1).setText(!isChecked ? "" : getString(R.string.motor_checkbox_2));
          checkBoxes.get(2).setText(!isChecked ? "" : getString(R.string.motor_checkbox_3));
          checkBoxes.get(3).setText(!isChecked ? "" : getString(R.string.motor_checkbox_4));
          checkBoxes.get(4).setText(!isChecked ? "" : getString(R.string.motor_checkbox_5));
          checkBoxes.get(5).setText(!isChecked ? "" : getString(R.string.motor_checkbox_6));
        });
  }

  private void setUpChips() {
    Chip chipMotorImpairment = findViewById(R.id.chipMotorImpairment);
    chipMotorImpairment.setOnCheckedChangeListener(
        (CompoundButton v, boolean isChecked) -> {
          if (isChecked) {
            for (ViewGroup checkBoxParent : checkBoxParents) {
              startCheckboxAnimation(checkBoxParent);
            }
          } else {
            stopCheckboxAnimation();
          }
        });
    chipMotorImpairment.setChecked(true);
  }

  private static void expandTouchArea(View parentView, View targetView, int extraPadding) {
    parentView.post(() -> {
          Rect rect = new Rect();
          targetView.getHitRect(rect);
          rect.top -= extraPadding;
          rect.left -= extraPadding;
          rect.right += extraPadding;
          rect.bottom += extraPadding;
          parentView.setTouchDelegate(new TouchDelegate(rect, targetView));
        });
  }

  private void startCheckboxAnimation(View view) {
    ObjectAnimator animationX =
        ObjectAnimator.ofFloat(view, "translationX", 150f * (random.nextFloat() * 2.0f - 1.0f));
    ObjectAnimator animationY =
        ObjectAnimator.ofFloat(view, "translationY", 50f * (random.nextFloat() * 2.0f - 1.0f));
    animationX.setRepeatCount(1);
    animationX.setRepeatMode(ObjectAnimator.REVERSE);
    animationY.setRepeatCount(1);
    animationY.setRepeatMode(ObjectAnimator.REVERSE);
    animation.playTogether(animationX, animationY);
    animation.setDuration(100);
    animation.setInterpolator(new LinearInterpolator());
    animation.addListener(
        new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator a) {
            animationX.setFloatValues(150f * (random.nextFloat() * 2.0f - 1.0f));
            animationY.setFloatValues(50f * (random.nextFloat() * 2.0f - 1.0f));
            animation.playTogether(animationX, animationY);
            animation.start();
          }
        });
    animation.start();
  }

  private void stopCheckboxAnimation() {
    animation.removeAllListeners();
  }

  public void backToMenu() {
    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
    finish();
  }

}
