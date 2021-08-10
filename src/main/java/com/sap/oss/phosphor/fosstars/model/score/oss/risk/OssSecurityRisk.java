package com.sap.oss.phosphor.fosstars.model.score.oss.risk;

import static com.sap.oss.phosphor.fosstars.model.other.Utils.setOf;
import static java.util.Collections.emptySet;

import com.sap.oss.phosphor.fosstars.model.Feature;
import com.sap.oss.phosphor.fosstars.model.Score;
import com.sap.oss.phosphor.fosstars.model.Value;
import com.sap.oss.phosphor.fosstars.model.score.AbstractScore;
import com.sap.oss.phosphor.fosstars.model.value.ScoreValue;
import java.util.Set;

/**
 * This scoring function calculates a risk introduced by an open source project.
 * It is based on the following sub-scores:
 * <ul>
 *   <li>{@link RiskLikelihoodScore}</li>
 *   <li>{@link RiskImpactFactors}</li>
 * </ul>
 * <pre>security risk = likelihood * impact</pre>
 */
public class OssSecurityRisk extends AbstractScore {

  /**
   * Calculates likelihood factors.
   */
  private final RiskLikelihoodFactors likelihoodFactors = new RiskLikelihoodFactors();

  /**
   * Calculates impact factors.
   */
  private final RiskImpactFactors impactFactors = new RiskImpactFactors();

  /**
   * Creates a new scoring function.
   */
  public OssSecurityRisk() {
    super("Security risk introduced by an open source project");
  }

  @Override
  public Set<Feature<?>> features() {
    return emptySet();
  }

  @Override
  public Set<Score> subScores() {
    return setOf(likelihoodFactors, impactFactors);
  }

  @Override
  public ScoreValue calculate(Value<?>... values) {
    ScoreValue likelihood = calculateIfNecessary(likelihoodFactors, values);
    ScoreValue impact = calculateIfNecessary(impactFactors, values);

    ScoreValue risk = scoreValue(MIN, likelihood, impact);

    if (likelihood.isUnknown() || impact.isUnknown()) {
      return risk.makeUnknown();
    }

    return risk.set(likelihood.get() * impact.get() / MAX);
  }
}