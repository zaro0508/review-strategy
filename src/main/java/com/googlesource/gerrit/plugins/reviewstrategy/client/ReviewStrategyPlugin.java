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

import com.google.gerrit.client.GerritUiExtensionPoint;
import com.google.gerrit.client.Resources;
import com.google.gerrit.plugin.client.Plugin;
import com.google.gerrit.plugin.client.PluginEntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

import com.googlesource.gerrit.plugins.reviewstrategy.client.PreferencesInfo.ReviewStrategy;

import java.util.HashMap;
import java.util.Map;

public class ReviewStrategyPlugin extends PluginEntryPoint {
  public static final Resources RESOURCES = GWT.create(Resources.class);

  private final static String REVIEWERS_CLASS = "com-google-gerrit-" +
      "client-change-ChangeScreen_BinderImpl_GenCss_style-label_user";
  private final static String AUTHOR_COMMITER_CLASS = "com-google-gerrit-" +
      "client-change-CommitBox_BinderImpl_GenCss_style-userPanel";
  private final static String OWNER_CLASS = "com-google-gerrit-" +
      "client-change-ChangeScreen_BinderImpl_GenCss_style-ownerPanel";
  private final static String REVIEW_HISTORY_SUMMARY_CLASS = "com-google-" +
      "gerrit-client-change-Message_BinderImpl_GenCss_style-summary";

  private final static String HIDE_REVIEWERS_CLASS = Plugin.get()
      .getName() + "-hideReviewers";
  private final static String HIDE_AUTHOR_COMMITER_CLASS = Plugin.get()
      .getName() + "-hideAuthorCommiter";
  private final static String HIDE_OWNER_CLASS = Plugin.get()
      .getName() + "-hideOwner";
  private final static String HIDE_REVIEW_HISTORY_SUMMARY_CLASS = Plugin.get()
      .getName() + "-hideReviewHistorySummary";

  private static Map<String, String> classes = null;
  static {
    classes = new HashMap<>();
    classes.put(REVIEWERS_CLASS, HIDE_REVIEWERS_CLASS);
    classes.put(AUTHOR_COMMITER_CLASS, HIDE_AUTHOR_COMMITER_CLASS);
    classes.put(OWNER_CLASS, HIDE_OWNER_CLASS);
    classes.put(
        REVIEW_HISTORY_SUMMARY_CLASS, HIDE_REVIEW_HISTORY_SUMMARY_CLASS);
  }

  @Override
  public void onPluginLoad() {
    injectCssToHideDefaultClasses();

    Plugin.get().panel(
        GerritUiExtensionPoint.CHANGE_SCREEN_BELOW_CHANGE_INFO_BLOCK,
        new LabelPanel.Factory());
    Plugin.get().panel(
        GerritUiExtensionPoint.PREFERENCES_SCREEN_BOTTOM,
        new ReviewStrategyPreferencesPanel.Factory());
  }

  public static void refreshDefaultReviewStrategy(ReviewStrategy ui) {
    if (ui == ReviewStrategy.BLIND) {
      hideDefaultReviewStrategy();
    } else {
      showDefaultReviewStrategy();
    }
  }

  private static void hideDefaultReviewStrategy() {
    Element body = RootPanel.getBodyElement();
    for (Map.Entry<String, String> entry : classes.entrySet()) {
      if (!body.hasClassName(entry.getValue())) {
        body.addClassName(entry.getValue());
      }
    }
  }

  private static void showDefaultReviewStrategy() {
    for (Map.Entry<String, String> entry : classes.entrySet()) {
      RootPanel.getBodyElement().removeClassName(entry.getValue());
    }
  }

  private void injectCssToHideDefaultClasses() {
    StringBuilder css = new StringBuilder();
    for (Map.Entry<String, String> entry : classes.entrySet()) {
        css.append(".");
        css.append(entry.getValue());
        css.append(" .");
        css.append(entry.getKey());
        css.append(" {display: none;}");
        injectCss(css.toString());
    }
  }

  private final native void injectCss(String css)
  /*-{ return $wnd.Gerrit.injectCss(css) }-*/;
}
