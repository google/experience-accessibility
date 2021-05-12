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

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.android.experienceaccessibility.auditory.AuditoryLandingModel;
import com.android.experienceaccessibility.common.Constants;
import java.util.List;

/** Slide pager adapter for auditory landing page. */
public final class AuditoryLandingPagerAdapter extends PagerAdapter {
  private final List<AuditoryLandingModel> models;
  private final Context context;

  public AuditoryLandingPagerAdapter(List<AuditoryLandingModel> models, Context context) {
    this.models = models;
    this.context = context;
  }

  @NonNull
  @Override
  public Object instantiateItem(@NonNull ViewGroup container, int position) {
    LayoutInflater layoutInflater = LayoutInflater.from(context);
    View view =
        layoutInflater.inflate(
            R.layout.auditory_landing_item, container, /* attachToRoot= */ false);

    // Set background.
    ImageView image = view.findViewById(R.id.auditoryLandingBackgroundImage);
    int imageSrc = models.get(position).getImage();
    if (imageSrc != Constants.BLACK_BACKGROUND) {
      image.setImageResource(imageSrc);
    } else {
      image.setBackgroundColor(Color.BLACK);
    }
    // Set text.
    TextView title = view.findViewById(R.id.auditoryLandingTitle);
    title.setText(models.get(position).getTitle());
    TextView body = view.findViewById(R.id.auditoryLandingBody);
    body.setText(models.get(position).getText());

    container.addView(view, /* index= */ 0);
    view.setTag("auditory_landing_" + position);
    return view;
  }

  @Nullable
  @Override
  public CharSequence getPageTitle(int position) {
    return models.get(position).getTitle();
  }

  @Override
  public int getCount() {
    return models.size();
  }

  @Override
  public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
    return view.equals(object);
  }

  @Override
  public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    container.removeView((View) object);
  }
}
