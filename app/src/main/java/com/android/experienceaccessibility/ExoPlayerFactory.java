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

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.common.collect.Lists;

/** ExoPlayer factory. */
public class ExoPlayerFactory {

  private static final String SUBTITLE_LANGUAGE = "en";

  private ExoPlayerFactory() {}

  /** Creates ExoPlayer and sets given media items / subtitles to it. */
  public static SimpleExoPlayer createExoPlayer(Context context, String subPath, int resId) {
    MediaItem.Subtitle subtitle = createSubtitleMediaItem(subPath);
    MediaItem mediaItem = createMediaItem(context, resId, subtitle);

    SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();

    player.setMediaItem(mediaItem);
    player.prepare();

    return player;
  }

  private static MediaItem createMediaItem(
      Context context, int resourceId, MediaItem.Subtitle subtitle) {
    return new MediaItem.Builder()
        .setUri(getResourceUri(resourceId, context))
        .setSubtitles(Lists.newArrayList(subtitle))
        .build();
  }

  private static MediaItem.Subtitle createSubtitleMediaItem(String path) {
    return new MediaItem.Subtitle(
        Uri.parse(path), MimeTypes.APPLICATION_SUBRIP, SUBTITLE_LANGUAGE, C.SELECTION_FLAG_FORCED);
  }

  private static Uri getResourceUri(int resId, Context context) {
    Resources resources = context.getResources();
    return new Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(resources.getResourcePackageName(resId))
        .appendPath(resources.getResourceTypeName(resId))
        .appendPath(resources.getResourceEntryName(resId))
        .build();
  }
}
