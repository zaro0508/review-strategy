include_defs('//bucklets/gerrit_plugin.bucklet')

gerrit_plugin(
  name = 'review-strategy',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/**/*']),
  gwt_module = 'com.googlesource.gerrit.plugins.reviewstrategy.ReviewStrategy',
  manifest_entries = [
    'Gerrit-PluginName: review-strategy',
    'Gerrit-ApiType: plugin',
    'Gerrit-ApiVersion: 2.12-SNAPSHOT',
    'Gerrit-Module: com.googlesource.gerrit.plugins.reviewstrategy.Module',
    'Gerrit-HttpModule: com.googlesource.gerrit.plugins.reviewstrategy.HttpModule',
  ],
)

# this is required for bucklets/tools/eclipse/project.py to work
java_library(
  name = 'classpath',
  deps = [':review-strategy__plugin'],
)
