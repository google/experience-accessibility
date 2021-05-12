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

import androidx.appcompat.app.AppCompatActivity;

/** A model which holds the data for each story video. */
public final class AuditoryStoryMapModel {
  private final String subtitlesPath;
  private final int videoResourceId;
  private final Class<? extends AppCompatActivity> clazz;

  public AuditoryStoryMapModel(
      String subtitlesPath, int videoResourceId, Class<? extends AppCompatActivity> clazz) {
    this.subtitlesPath = subtitlesPath;
    this.videoResourceId = videoResourceId;
    this.clazz = clazz;
  }

  public String getSubtitlesPath() {
    return subtitlesPath;
  }

  public int getVideoResourceId() {
    return videoResourceId;
  }

  public Class<? extends AppCompatActivity> getClazz() {
    return clazz;
  }
}
