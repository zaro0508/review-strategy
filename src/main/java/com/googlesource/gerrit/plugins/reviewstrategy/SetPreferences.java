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

package com.googlesource.gerrit.plugins.reviewstrategy;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.restapi.AuthException;
import com.google.gerrit.extensions.restapi.RestModifyView;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.account.AccountResource;
import com.google.gerrit.server.account.VersionedAccountPreferences;
import com.google.gerrit.server.config.AllUsersName;
import com.google.gerrit.server.git.MetaDataUpdate;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.googlesource.gerrit.plugins.reviewstrategy.GetPreferences.PreferencesInfo;
import com.googlesource.gerrit.plugins.reviewstrategy.SetPreferences.Input;

import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;

import java.io.IOException;

public class SetPreferences implements RestModifyView<AccountResource, Input> {
  public static class Input {
    ReviewStrategy reviewStrategy;
  }

  private final String pluginName;
  private final Provider<CurrentUser> self;
  private final AllUsersName allUsersName;
  private final Provider<MetaDataUpdate.User> metaDataUpdateFactory;

  @Inject
  public SetPreferences(@PluginName String pluginName,
      Provider<CurrentUser> self,
      AllUsersName allUsersName,
      Provider<MetaDataUpdate.User> metaDataUpdateFactory) {
    this.pluginName = pluginName;
    this.self = self;
    this.allUsersName = allUsersName;
    this.metaDataUpdateFactory = metaDataUpdateFactory;
  }

  @Override
  public GetPreferences.PreferencesInfo apply(AccountResource rsrc, Input input)
      throws AuthException, RepositoryNotFoundException, IOException,
      ConfigInvalidException {
    if (self.get() != rsrc.getUser()
        && !self.get().getCapabilities().canModifyAccount()) {
      throw new AuthException("not allowed to set preferences");
    }

    if (input == null) {
      input = new Input();
    }
    if (input.reviewStrategy == null) {
      input.reviewStrategy = ReviewStrategy.DEFAULT;
    }

    VersionedAccountPreferences p =
        VersionedAccountPreferences.forUser(rsrc.getUser().getAccountId());
    MetaDataUpdate md = metaDataUpdateFactory.get().create(allUsersName);
    try {
      p.load(md);

      if (input.reviewStrategy != ReviewStrategy.DEFAULT) {
        p.getConfig().setEnum("plugin", pluginName, "strategy", input.reviewStrategy);
      } else {
        p.getConfig().unset("plugin", pluginName, "strategy");
      }

      p.commit(md);
    } finally {
      md.close();
    }

    PreferencesInfo info = new PreferencesInfo();
    info.reviewStrategy = input.reviewStrategy;
    return info;
  }
}
