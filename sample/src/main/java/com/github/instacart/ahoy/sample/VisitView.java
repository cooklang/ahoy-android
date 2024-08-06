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

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.github.instacart.ahoy.AhoySingleton;
import com.github.instacart.ahoy.Visit;
import com.github.instacart.ahoy.sample.databinding.VisitViewBinding;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;

public class VisitView extends LinearLayout {

    private VisitViewBinding binding;
    private Disposable mDisposable;

    public VisitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        binding = VisitViewBinding.bind(this);
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDisposable = Flowable.just(AhoySingleton.visit())
                .concatWith(AhoySingleton.visitStream())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(visit -> updateViews(AhoySingleton.visitorToken(), visit));
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDisposable.dispose();
    }

    public void updateViews(String visitorToken, Visit visit) {
        Resources resources = getResources();
        binding.visitorToken.setText(resources.getString(R.string.visitor_token, visitorToken));
        boolean isVisitValid = visit != null && visit.isValid();
        binding.visitFetchProgress.setVisibility(isVisitValid ? View.INVISIBLE : View.VISIBLE);
        if (isVisitValid) {
            binding.visitToken.setText(resources.getString(R.string.visit_token, visit.visitToken()));
        } else {
            binding.visitToken.setText(resources.getString(R.string.visit_token, ""));
        }
    }
}
