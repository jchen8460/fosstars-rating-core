package com.sap.oss.phosphor.fosstars.data.github;

import static com.sap.oss.phosphor.fosstars.model.feature.oss.OssFeatures.RUNS_BANDIT_SCANS;
import static com.sap.oss.phosphor.fosstars.model.feature.oss.OssFeatures.USES_BANDIT_SCAN_CHECKS;
import static com.sap.oss.phosphor.fosstars.model.other.Utils.setOf;

import com.sap.oss.phosphor.fosstars.data.AbstractStaticScanToolsDataProvider;
import com.sap.oss.phosphor.fosstars.model.Value;
import com.sap.oss.phosphor.fosstars.model.ValueSet;
import com.sap.oss.phosphor.fosstars.model.feature.oss.OssFeatures;
import com.sap.oss.phosphor.fosstars.model.subject.oss.GitHubProject;
import com.sap.oss.phosphor.fosstars.model.value.ValueHashSet;
import com.sap.oss.phosphor.fosstars.util.Yaml;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.collections4.IteratorUtils;

/**
 * The data provider gathers info about how a project uses Bandit for static analysis. In
 * particular, it tries to fill out the following features:
 * <ul>
 *   <li>{@link OssFeatures#RUNS_BANDIT_SCANS}</li>
 *   <li>{@link OssFeatures#USES_BANDIT_SCAN_CHECKS}</li>
 * </ul>
 */
public class BanditDataProvider extends AbstractStaticScanToolsDataProvider {

  /**
   * A step in a GitHub action that triggers analysis with Bandit.
   */
  private static final Pattern RUN_STEP_BANDIT_REGEX_PATTERN
      = Pattern.compile("^.*bandit .*$", Pattern.DOTALL);

  /**
   * Initializes a data provider.
   *
   * @param fetcher An interface to GitHub.
   */
  public BanditDataProvider(GitHubDataFetcher fetcher) {
    super(fetcher, setOf(RUNS_BANDIT_SCANS, USES_BANDIT_SCAN_CHECKS));
  }

  @Override
  protected ValueSet fetchValuesFor(GitHubProject project) throws IOException {
    logger.info("Figuring out how the project uses Bandit ...");

    LocalRepository repository = GitHubDataFetcher.localRepositoryFor(project);

    Value<Boolean> runsBandit = RUNS_BANDIT_SCANS.value(false);
    Value<Boolean> usesBanditScanChecks = USES_BANDIT_SCAN_CHECKS.value(false);

    // ideally, we're looking for a GitHub action that runs Bandit scan on pull requests
    // but if we just find an action that runs Bandit scans, that's also fine
    for (Path configPath : findGitHubActionsIn(repository)) {
      try (InputStream content = Files.newInputStream(configPath)) {
        Map<String, Object> githubAction = Yaml.readMap(content);
        if (triggersScan(githubAction)) {
          runsBandit = RUNS_BANDIT_SCANS.value(true);
          if (runsOnPullRequests(githubAction)) {
            usesBanditScanChecks = USES_BANDIT_SCAN_CHECKS.value(true);
            break;
          }
        }
      }
    }

    return ValueHashSet.from(runsBandit, usesBanditScanChecks);
  }

  @Override
  public boolean triggersScan(Map<?, ?> githubAction) {
    return Optional.ofNullable(githubAction.get("jobs"))
        .filter(Map.class::isInstance)
        .map(Map.class::cast)
        .map(jobs -> jobs.values())
        .filter(Iterable.class::isInstance)
        .map(Iterable.class::cast)
        .map(BanditDataProvider::scanJobs)
        .orElse(false);
  }

  /**
   * Checks if any step in a collection of jobs triggers a Bandit scan.
   *
   * @param jobs The collection of jobs from GitHub action.
   * @return True if a step triggers a Bandit scan, false otherwise.
   */
  private static boolean scanJobs(Iterable<?> jobs) {
    return IteratorUtils.toList(jobs.iterator()).stream()
        .filter(Map.class::isInstance)
        .map(Map.class::cast)
        .map(job -> job.get("steps"))
        .filter(Iterable.class::isInstance)
        .map(Iterable.class::cast)
        .anyMatch(BanditDataProvider::hasBanditRunStep);
  }

  /**
   * Checks if a collection of steps from a GitHub action contains a step that triggers a Bandit
   * scan.
   *
   * @param steps The steps to be checked.
   * @return True if the steps contain a step that triggers a Bandit scan, false otherwise.
   */
  private static boolean hasBanditRunStep(Iterable<?> steps) {
    return IteratorUtils.toList(steps.iterator()).stream()
        .filter(Map.class::isInstance)
        .map(Map.class::cast)
        .map(step -> step.get("run"))
        .filter(String.class::isInstance)
        .map(String.class::cast)
        .anyMatch(run -> RUN_STEP_BANDIT_REGEX_PATTERN.matcher(run).matches());
  }
}