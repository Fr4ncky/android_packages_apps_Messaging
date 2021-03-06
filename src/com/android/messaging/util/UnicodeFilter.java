/*
 * Copyright (C) 2012-2015 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.messaging.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

import com.android.internal.telephony.GsmAlphabet;
import com.android.internal.telephony.EncodeException;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Attempts to substitute characters that cannot be encoded in the limited
 * GSM 03.38 character set. In many cases this will prevent sending a message
 * containing characters that would switch the message from 7-bit GSM
 * encoding (160 char limit) to 16-bit Unicode encoding (70 char limit).
 */
public class UnicodeFilter {
    private static final Pattern DIACRITICS_PATTERN =
            Pattern.compile("\\p{InCombiningDiacriticalMarks}");

    private boolean mStripNonDecodableOnly;

    public UnicodeFilter(boolean stripNonDecodableOnly) {
        mStripNonDecodableOnly = stripNonDecodableOnly;
    }

    public CharSequence filter(CharSequence source) {
        StringBuilder output = new StringBuilder();
        final int sourceLength = source.length();

        for (int i = 0; i < sourceLength; i++) {
            char c = source.charAt(i);
            boolean canEncodeInGsm;

            try {
                GsmAlphabet.charToGsm(c, true);
                canEncodeInGsm = true;
            } catch (EncodeException e) {
                canEncodeInGsm = false;
            }

            // Character requires Unicode, try to replace it
            if (!mStripNonDecodableOnly || !canEncodeInGsm) {
                String s = String.valueOf(c);

                // Try normalizing the character into Unicode NFKD form and
                // stripping out diacritic mark characters.
                s = Normalizer.normalize(s, Normalizer.Form.NFKD);
                s = DIACRITICS_PATTERN.matcher(s).replaceAll("");

                // Special case characters that don't get stripped by the
                // above technique.
                s = s.replace("??", "OE");
                s = s.replace("??", "oe");
                s = s.replace("??", "L");
                s = s.replace("??", "l");
                s = s.replace("??", "Dj");
                s = s.replace("??", "dj");
                s = s.replace("??", "A");
                s = s.replace("??", "B");
                s = s.replace("??", "E");
                s = s.replace("??", "Z");
                s = s.replace("??", "H");
                s = s.replace("??", "I");
                s = s.replace("??", "K");
                s = s.replace("??", "M");
                s = s.replace("??", "N");
                s = s.replace("??", "O");
                s = s.replace("??", "P");
                s = s.replace("??", "T");
                s = s.replace("??", "Y");
                s = s.replace("??", "X");
                s = s.replace("??", "A");
                s = s.replace("??", "B");
                s = s.replace("??", "??");
                s = s.replace("??", "??");
                s = s.replace("??", "E");
                s = s.replace("??", "Z");
                s = s.replace("??", "H");
                s = s.replace("??", "??");
                s = s.replace("??", "I");
                s = s.replace("??", "K");
                s = s.replace("??", "??");
                s = s.replace("??", "M");
                s = s.replace("??", "N");
                s = s.replace("??", "??");
                s = s.replace("??", "O");
                s = s.replace("??", "??");
                s = s.replace("??", "P");
                s = s.replace("??", "??");
                s = s.replace("??", "T");
                s = s.replace("??", "Y");
                s = s.replace("??", "??");
                s = s.replace("??", "X");
                s = s.replace("??", "??");
                s = s.replace("??", "??");
                s = s.replace("??", "??");
                output.append(s);
            } else {
                output.append(c);
            }
        }

        // Source is a spanned string, so copy the spans from it
        if (source instanceof Spanned) {
            SpannableString spannedoutput = new SpannableString(output);
            TextUtils.copySpansFrom(
                    (Spanned) source, 0, sourceLength, null, spannedoutput, 0);

            return spannedoutput;
        }

        // Source is a vanilla charsequence, so return output as-is
        return output.toString();
    }
}
