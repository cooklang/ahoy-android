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

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.instacart.ahoy.AhoySingleton;
import com.github.instacart.ahoy.Event;
import com.github.instacart.ahoy.sample.databinding.SimpleActivityBinding;

import java.util.Collections;

public class SimpleActivity extends AppCompatActivity {

    private SimpleActivityBinding binding;


    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        binding = SimpleActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.newVisit.setOnClickListener(view -> AhoySingleton.newVisit(Collections.emptyMap()));
        binding.trackEvent.setOnClickListener(view -> AhoySingleton.trackEvent(Event.create("Test event", Collections.emptyMap())));
        binding.saveUtms.setOnClickListener(view -> startActivity(new Intent(this, UtmActivity.class)));
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
