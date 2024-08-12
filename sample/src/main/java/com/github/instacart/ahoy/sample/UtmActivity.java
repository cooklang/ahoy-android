/*
 * Copyright (C) 2016 Maplebear Inc., d/b/a Instacart
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
package com.github.instacart.ahoy.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArrayMap;

import com.github.instacart.ahoy.AhoySingleton;
import com.github.instacart.ahoy.Visit;
import com.github.instacart.ahoy.sample.databinding.UtmActivityBinding;
import com.github.instacart.ahoy.utils.TypeUtil;

import java.util.Collections;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;

public class UtmActivity extends AppCompatActivity {

    private UtmActivityBinding binding;

    private Disposable mDisposable;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UtmActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle(getString(R.string.utm_activity));
        binding.saveUtmParams.setOnClickListener(this::onClick);
    }

    @Override protected void onResume() {
        super.onResume();

        mDisposable = Flowable.just(AhoySingleton.visit())
                .concatWith(AhoySingleton.visitStream())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showUtmParams);
    }

    @Override protected void onPause() {
        super.onPause();
        mDisposable.dispose();
    }

    private void showUtmParams(Visit visit) {
        binding.utmCampaign.setText(visit.extra(Visit.UTM_CAMPAIGN));
        binding.utmContent.setText(visit.extra(Visit.UTM_CONTENT));
        binding.utmMedium.setText(visit.extra(Visit.UTM_MEDIUM));
        binding.utmSource.setText(visit.extra(Visit.UTM_SOURCE));
        binding.utmTerm.setText(visit.extra(Visit.UTM_TERM));
    }

    private static void saveContent(Map<String, Object> params, String key, TextView textView) {
        String value = textView.getText().toString();
        if (!TypeUtil.isEmpty(value)) {
            params.put(key, value);
        }
    }

    public void onClick(View view) {
        final Map<String, Object> utmParams = new ArrayMap<>();
        saveContent(utmParams, Visit.UTM_CAMPAIGN, binding.utmCampaign);
        saveContent(utmParams, Visit.UTM_CONTENT, binding.utmContent);
        saveContent(utmParams, Visit.UTM_MEDIUM, binding.utmMedium);
        saveContent(utmParams, Visit.UTM_SOURCE, binding.utmSource);
        saveContent(utmParams, Visit.UTM_TERM, binding.utmTerm);

        if (view.getId() == R.id.save_utm_params) {
            AhoySingleton.saveExtras(utmParams);
        }
    }
}
