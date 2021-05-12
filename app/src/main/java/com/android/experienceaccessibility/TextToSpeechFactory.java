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
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;
import java.util.Locale;

/** Text to speech factory. */
public class TextToSpeechFactory {

  private TextToSpeechFactory() {}

  /** Creates fully initialised text to speech. */
  public static TextToSpeech createTextToSpeech(Context context) {
    return new InitListener(context).textToSpeech;
  }

  private static class InitListener implements OnInitListener {
    private final Context context;
    private final TextToSpeech textToSpeech;

    private InitListener(Context context) {
      this.context = context;
      this.textToSpeech = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
      if (status != TextToSpeech.SUCCESS) {
        Toast.makeText(
                context,
                "Text To Speech failed!",
                Toast.LENGTH_SHORT)
            .show();
        return;
      }
      int language = textToSpeech.setLanguage(Locale.US);
      if (language == TextToSpeech.LANG_MISSING_DATA
          || language == TextToSpeech.LANG_NOT_SUPPORTED) {
        Toast.makeText(context, "Text To Speech language is not supported!", Toast.LENGTH_SHORT)
            .show();
      }
    }
  }
}
