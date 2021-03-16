package com.sap.oss.phosphor.fosstars.data.github;

import static com.sap.oss.phosphor.fosstars.model.feature.oss.OssFeatures.HAS_README;

import com.sap.oss.phosphor.fosstars.model.Feature;
import com.sap.oss.phosphor.fosstars.model.Value;
import com.sap.oss.phosphor.fosstars.model.subject.oss.GitHubProject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * This data provider gathers info about project's README file.
 * It fills out {@link com.sap.oss.phosphor.fosstars.model.feature.oss.OssFeatures#HAS_README}.
 */
public class ReadmeInfo extends CachedSingleFeatureGitHubDataProvider<Boolean> {

  /**
   * A list of known README file names.
   */
  private static final List<String> KNOWN_README_FILES = Arrays.asList("README", "README.txt");

  /**
   * Initializes a data provider.
   *
   * @param fetcher An interface to GitHub.
   */
  public ReadmeInfo(GitHubDataFetcher fetcher) {
    super(fetcher);
  }

  @Override
  protected Feature<Boolean> supportedFeature() {
    return HAS_README;
  }

  @Override
  protected Value<Boolean> fetchValueFor(GitHubProject project) throws IOException {
    logger.info("Gathering info about project's README file ...");
    LocalRepository repository = GitHubDataFetcher.localRepositoryFor(project);
    return HAS_README.value(KNOWN_README_FILES.stream().anyMatch(repository::hasFile));
  }

}
