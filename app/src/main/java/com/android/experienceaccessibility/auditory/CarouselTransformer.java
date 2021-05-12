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

package com.android.experienceaccessibility.auditory;

import android.content.Context;
import androidx.core.view.ViewCompat;
import android.view.View;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.PageTransformer;

/** Provides the pan-zoom effect when swiping to new page. */
public final class CarouselTransformer implements PageTransformer {

  public static final int MAX_TRANSLATE_OFFSET_X_DIP = 180;
  private final int maxTranslateOffsetX;
  private ViewPager viewPager;

  public CarouselTransformer(Context context) {
    this.maxTranslateOffsetX = dp2px(context, MAX_TRANSLATE_OFFSET_X_DIP);
  }

  @Override
  public void transformPage(View view, float position) {
    if (viewPager == null) {
      viewPager = (ViewPager) view.getParent();
    }

    int leftInScreen = view.getLeft() - viewPager.getScrollX();
    int centerXInViewPager = leftInScreen + view.getMeasuredWidth() / 2;
    int offsetX = centerXInViewPager - viewPager.getMeasuredWidth() / 2;
    float offsetRate = (float) offsetX * 0.38f / viewPager.getMeasuredWidth();
    float scaleFactor = 1 - Math.abs(offsetRate);

    if (scaleFactor > 0) {
      view.setScaleX(scaleFactor);
      view.setScaleY(scaleFactor);
      view.setTranslationX(-maxTranslateOffsetX * offsetRate);
    }
    ViewCompat.setElevation(view, scaleFactor);
  }

  /** Dp to pixel conversion */
  private static int dp2px(Context context, float dipValue) {
    float m = context.getResources().getDisplayMetrics().density;
    return (int) (dipValue * m + 0.5f);
  }
}
