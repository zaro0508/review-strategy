// Copyright (C) 2016 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.reviewstrategy.client;

import com.google.gerrit.plugin.client.Plugin;
import com.google.gerrit.plugin.client.extension.Panel;
import com.google.gerrit.plugin.client.rpc.RestApi;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LabelPanel extends VerticalPanel {
  static class Factory implements Panel.EntryPoint {
    @Override
    public void onLoad(Panel panel) {
      panel.setWidget(new LabelPanel());
    }
  }

  LabelPanel() {
    new RestApi("accounts").id("self").view(
        Plugin.get().getPluginName(), "preferences")
        .get(new AsyncCallback<PreferencesInfo>() {
          @Override
          public void onSuccess(PreferencesInfo result) {
            ReviewStrategyPlugin.refreshDefaultReviewStrategy(result.reviewStrategy());
          }

          @Override
          public void onFailure(Throwable caught) {
            // never invoked
          }
        });
  }
}
