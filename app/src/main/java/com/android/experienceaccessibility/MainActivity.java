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
import android.view.View;
import androidx.viewpager.widget.ViewPager;
import com.android.experienceaccessibility.auditory.AuditoryLandingModel;
import com.android.experienceaccessibility.auditory.CarouselTransformer;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

/** Main activity for MovingButtons App. */
public class MainActivity extends AppCompatActivity {
  private ViewPager viewPager;
  private List<AuditoryLandingModel> auditoryLandingModels;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    initializeAuditoryLandingModels();
    AuditoryLandingPagerAdapter adapter =
        new AuditoryLandingPagerAdapter(auditoryLandingModels, this);
    viewPager = findViewById(R.id.auditoryLandingViewPager);
    viewPager.setAdapter(adapter);
    viewPager.setPageTransformer(false, new CarouselTransformer(this));
    TabLayout dotIndicator = findViewById(R.id.auditoryLandingDotIndicator);
    dotIndicator.setupWithViewPager(viewPager, true);
    setTabLabelVisibility(dotIndicator);
    ViewPager.OnPageChangeListener onPageChangeListener =
        new ViewPager.OnPageChangeListener() {
          @Override
          public void onPageScrolled(
              int position, float positionOffset, int positionOffsetPixels) {}

          @Override
          public void onPageSelected(int position) {
            View view = viewPager.findViewWithTag("auditory_landing_" + viewPager.getCurrentItem());
            view.setOnClickListener(
                v -> startAuditoryActivity(auditoryLandingModels.get(position).getStoryId()));
          }

          @Override
          public void onPageScrollStateChanged(int state) {}
        };
    viewPager.addOnPageChangeListener(onPageChangeListener);

    // Run onPageSelected on first page.
    viewPager.post(
        () -> onPageChangeListener.onPageSelected(viewPager.getCurrentItem()));
  }

  private void setTabLabelVisibility(TabLayout tablayout) {
    for (int i = 0; i < tablayout.getTabCount(); i++) {
      tablayout.getTabAt(i).setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_UNLABELED);
    }}

  /** Initialize auditoryLandingModels for all card views. */
  private void initializeAuditoryLandingModels() {
    auditoryLandingModels = new ArrayList<>();
    auditoryLandingModels.add(
        new AuditoryLandingModel(
            getString(R.string.auditory_landing_title_blind),
            getString(R.string.auditory_story_description),
            R.drawable.blindness,
            AuditoryStoryActivity.STORY_BLINDNESS_ID));
    auditoryLandingModels.add(
        new AuditoryLandingModel(
            getString(R.string.auditory_landing_title_deaf),
            getString(R.string.auditory_story_deaf_description),
            R.drawable.deafness,
            AuditoryStoryActivity.STORY_DEAFNESS_ID));
    auditoryLandingModels.add(
        new AuditoryLandingModel(
            getString(R.string.auditory_landing_title_motor_impairment),
            getString(R.string.auditory_story_motor_description),
            R.drawable.motor_impairment,
            AuditoryStoryActivity.STORY_MOTOR_IMPAIRMENT_ID));
    auditoryLandingModels.add(
        new AuditoryLandingModel(
            getString(R.string.auditory_landing_title_learning_disabilities),
            getString(R.string.auditory_story_learning_description),
            R.drawable.learning_disability,
            AuditoryStoryActivity.STORY_LEARNING_DISABILITY_ID));
  }

  public void startAuditoryActivity(String storyId) {
    Intent intent = new Intent(this, AuditoryStoryActivity.class);
    intent.putExtra(AuditoryStoryActivity.STORY_KEY, storyId);
    startActivity(intent);
  }
}
