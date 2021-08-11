package com.sap.oss.phosphor.fosstars.model.rating.oss;

import static com.sap.oss.phosphor.fosstars.model.feature.oss.OssRiskFeatures.AVAILABILITY_IMPACT;
import static com.sap.oss.phosphor.fosstars.model.feature.oss.OssRiskFeatures.CONFIDENTIALITY_IMPACT;
import static com.sap.oss.phosphor.fosstars.model.feature.oss.OssRiskFeatures.DATA_CONFIDENTIALITY;
import static com.sap.oss.phosphor.fosstars.model.feature.oss.OssRiskFeatures.FUNCTIONALITY;
import static com.sap.oss.phosphor.fosstars.model.feature.oss.OssRiskFeatures.HANDLING_UNTRUSTED_DATA_LIKELIHOOD;
import static com.sap.oss.phosphor.fosstars.model.feature.oss.OssRiskFeatures.HOW_MANY_COMPONENTS_USE_OSS_PROJECT;
import static com.sap.oss.phosphor.fosstars.model.feature.oss.OssRiskFeatures.INTEGRITY_IMPACT;
import static com.sap.oss.phosphor.fosstars.model.feature.oss.OssRiskFeatures.IS_ADOPTED;
import static com.sap.oss.phosphor.fosstars.model.qa.TestScoreValue.testScoreValue;
import static com.sap.oss.phosphor.fosstars.model.qa.TestVectorBuilder.newTestVector;
import static com.sap.oss.phosphor.fosstars.model.rating.oss.SecurityRiskRatingIntroducedByOss.OssSecurityRiskLabel.CRITICAL;
import static com.sap.oss.phosphor.fosstars.model.rating.oss.SecurityRiskRatingIntroducedByOss.OssSecurityRiskLabel.HIGH;
import static com.sap.oss.phosphor.fosstars.model.rating.oss.SecurityRiskRatingIntroducedByOss.OssSecurityRiskLabel.LOW;
import static com.sap.oss.phosphor.fosstars.model.rating.oss.SecurityRiskRatingIntroducedByOss.OssSecurityRiskLabel.MEDIUM;
import static com.sap.oss.phosphor.fosstars.model.rating.oss.SecurityRiskRatingIntroducedByOss.OssSecurityRiskLabel.NOTE;

import com.sap.oss.phosphor.fosstars.model.RatingRepository;
import com.sap.oss.phosphor.fosstars.model.Score;
import com.sap.oss.phosphor.fosstars.model.feature.DataConfidentialityType;
import com.sap.oss.phosphor.fosstars.model.feature.Impact;
import com.sap.oss.phosphor.fosstars.model.feature.Likelihood;
import com.sap.oss.phosphor.fosstars.model.feature.Quantity;
import com.sap.oss.phosphor.fosstars.model.feature.oss.Functionality;
import com.sap.oss.phosphor.fosstars.model.math.DoubleInterval;
import com.sap.oss.phosphor.fosstars.model.qa.RatingVerification;
import com.sap.oss.phosphor.fosstars.model.qa.TestVectors;
import com.sap.oss.phosphor.fosstars.model.qa.VerificationFailedException;
import com.sap.oss.phosphor.fosstars.model.score.oss.OssSecurityScore;
import org.junit.Test;

public class SecurityRiskRatingIntroducedByOssVerificationTest {

  @Test
  public void testVerification() throws VerificationFailedException {
    TestVectors vectors = new TestVectors();

    vectors.add(
        newTestVector("good_security|low_likelihood|low_impact")
            .set(testScoreValue(OssSecurityScore.class, Score.MAX))
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.FEW)
            .set(FUNCTIONALITY, Functionality.TESTING)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.NEGLIGIBLE)
            .set(IS_ADOPTED.yes())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.TEST)
            .set(CONFIDENTIALITY_IMPACT, Impact.NEGLIGIBLE)
            .set(INTEGRITY_IMPACT, Impact.NEGLIGIBLE)
            .set(AVAILABILITY_IMPACT, Impact.NEGLIGIBLE)
            .expectedScore(DoubleInterval.closed(0.0, 1.0))
            .expectedLabel(NOTE)
            .make());

    vectors.add(
        newTestVector("good_security|high_likelihood|low_impact")
            .set(testScoreValue(OssSecurityScore.class, 9.0))
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.A_LOT)
            .set(FUNCTIONALITY, Functionality.SDK)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.HIGH)
            .set(IS_ADOPTED.yes())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.PUBLIC)
            .set(CONFIDENTIALITY_IMPACT, Impact.LOW)
            .set(INTEGRITY_IMPACT, Impact.LOW)
            .set(AVAILABILITY_IMPACT, Impact.LOW)
            .expectedScore(DoubleInterval.closed(0, 1))
            .expectedLabel(NOTE)
            .make());

    vectors.add(
        newTestVector("good_security|high_likelihood|high_impact")
            .set(testScoreValue(OssSecurityScore.class, 9.0))
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.A_LOT)
            .set(FUNCTIONALITY, Functionality.SDK)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.HIGH)
            .set(IS_ADOPTED.yes())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.PUBLIC)
            .set(CONFIDENTIALITY_IMPACT, Impact.HIGH)
            .set(INTEGRITY_IMPACT, Impact.HIGH)
            .set(AVAILABILITY_IMPACT, Impact.HIGH)
            .expectedScore(DoubleInterval.closed(0, 2))
            .expectedLabel(LOW)
            .make());

    vectors.add(
        newTestVector("bad_security|high_likelihood|low_impact")
            .set(testScoreValue(OssSecurityScore.class, Score.MIN))
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.A_LOT)
            .set(FUNCTIONALITY, Functionality.SDK)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.HIGH)
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.PUBLIC)
            .set(CONFIDENTIALITY_IMPACT, Impact.LOW)
            .set(INTEGRITY_IMPACT, Impact.LOW)
            .set(AVAILABILITY_IMPACT, Impact.LOW)
            .expectedScore(DoubleInterval.closed(1, 2))
            .expectedLabel(MEDIUM)
            .make());

    vectors.add(
        newTestVector("bad_security|low_likelihood|high_impact")
            .set(testScoreValue(OssSecurityScore.class, Score.MIN))
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.FEW)
            .set(FUNCTIONALITY, Functionality.NETWORKING)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.LOW)
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.PERSONAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.HIGH)
            .set(INTEGRITY_IMPACT, Impact.HIGH)
            .set(AVAILABILITY_IMPACT, Impact.HIGH)
            .expectedScore(DoubleInterval.closed(2, 3))
            .expectedLabel(MEDIUM)
            .make());

    vectors.add(
        newTestVector("bad_security|high_likelihood|high_impact")
            .set(testScoreValue(OssSecurityScore.class, Score.MIN))
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.A_LOT)
            .set(FUNCTIONALITY, Functionality.APPLICATION_FRAMEWORK)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.HIGH)
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.PERSONAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.HIGH)
            .set(INTEGRITY_IMPACT, Impact.HIGH)
            .set(AVAILABILITY_IMPACT, Impact.HIGH)
            .expectedScore(DoubleInterval.closed(9.0, 10.0))
            .expectedLabel(CRITICAL)
            .make());

    vectors.add(
        newTestVector("netty")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.QUITE_A_LOT)
            .set(FUNCTIONALITY, Functionality.APPLICATION_FRAMEWORK)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.HIGH)
            .set(testScoreValue(OssSecurityScore.class, 7.0))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.CONFIDENTIAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.HIGH)
            .set(INTEGRITY_IMPACT, Impact.HIGH)
            .set(AVAILABILITY_IMPACT, Impact.HIGH)
            .expectedScore(DoubleInterval.closed(3, 4))
            .expectedLabel(MEDIUM)
            .make());

    vectors.add(
        newTestVector("junit")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.A_LOT)
            .set(FUNCTIONALITY, Functionality.TESTING)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.NEGLIGIBLE)
            .set(testScoreValue(OssSecurityScore.class, 2.0))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.TEST)
            .set(CONFIDENTIALITY_IMPACT, Impact.NEGLIGIBLE)
            .set(INTEGRITY_IMPACT, Impact.NEGLIGIBLE)
            .set(AVAILABILITY_IMPACT, Impact.NEGLIGIBLE)
            .expectedScore(DoubleInterval.closed(0, 2))
            .expectedLabel(NOTE)
            .make());

    vectors.add(
        newTestVector("zlib")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.SOME)
            .set(FUNCTIONALITY, Functionality.OTHER)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.HIGH)
            .set(testScoreValue(OssSecurityScore.class, 2.0))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.CONFIDENTIAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.HIGH)
            .set(INTEGRITY_IMPACT, Impact.HIGH)
            .set(AVAILABILITY_IMPACT, Impact.HIGH)
            .expectedScore(DoubleInterval.closed(5, 6))
            .expectedLabel(HIGH)
            .make());

    vectors.add(
        newTestVector("poi")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.SOME)
            .set(FUNCTIONALITY, Functionality.PARSER)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.HIGH)
            .set(testScoreValue(OssSecurityScore.class, 4.0))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.CONFIDENTIAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.HIGH)
            .set(INTEGRITY_IMPACT, Impact.HIGH)
            .set(AVAILABILITY_IMPACT, Impact.HIGH)
            .expectedScore(DoubleInterval.closed(4, 5))
            .expectedLabel(MEDIUM)
            .make());

    vectors.add(
        newTestVector("bc")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.SOME)
            .set(FUNCTIONALITY, Functionality.SECURITY)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.HIGH)
            .set(testScoreValue(OssSecurityScore.class, 3.0))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.CONFIDENTIAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.HIGH)
            .set(INTEGRITY_IMPACT, Impact.HIGH)
            .set(AVAILABILITY_IMPACT, Impact.HIGH)
            .expectedScore(DoubleInterval.closed(5, 6))
            .expectedLabel(HIGH)
            .make());

    vectors.add(
        newTestVector("codec")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.QUITE_A_LOT)
            .set(FUNCTIONALITY, Functionality.PARSER)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.MEDIUM)
            .set(testScoreValue(OssSecurityScore.class, 4.0))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.CONFIDENTIAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.LOW)
            .set(INTEGRITY_IMPACT, Impact.LOW)
            .set(AVAILABILITY_IMPACT, Impact.LOW)
            .expectedScore(DoubleInterval.closed(1, 2))
            .expectedLabel(NOTE)
            .make());

    vectors.add(
        newTestVector("collections")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.A_LOT)
            .set(FUNCTIONALITY, Functionality.OTHER)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.LOW)
            .set(testScoreValue(OssSecurityScore.class, 4.0))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.CONFIDENTIAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.LOW)
            .set(INTEGRITY_IMPACT, Impact.LOW)
            .set(AVAILABILITY_IMPACT, Impact.MEDIUM)
            .expectedScore(DoubleInterval.closed(2, 3))
            .expectedLabel(LOW)
            .make());

    vectors.add(
        newTestVector("upload")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.SOME)
            .set(FUNCTIONALITY, Functionality.OTHER)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.HIGH)
            .set(testScoreValue(OssSecurityScore.class, 2.0))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.CONFIDENTIAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.LOW)
            .set(INTEGRITY_IMPACT, Impact.HIGH)
            .set(AVAILABILITY_IMPACT, Impact.HIGH)
            .expectedScore(DoubleInterval.closed(4, 5))
            .expectedLabel(HIGH)
            .make());

    vectors.add(
        newTestVector("i/o")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.A_LOT)
            .set(FUNCTIONALITY, Functionality.OTHER)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.MEDIUM)
            .set(testScoreValue(OssSecurityScore.class, 4.0))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.CONFIDENTIAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.MEDIUM)
            .set(INTEGRITY_IMPACT, Impact.MEDIUM)
            .set(AVAILABILITY_IMPACT, Impact.MEDIUM)
            .expectedScore(DoubleInterval.closed(3, 4))
            .expectedLabel(LOW)
            .make());

    vectors.add(
        newTestVector("lang")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.SOME)
            .set(FUNCTIONALITY, Functionality.OTHER)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.LOW)
            .set(testScoreValue(OssSecurityScore.class, 7.0))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.CONFIDENTIAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.LOW)
            .set(INTEGRITY_IMPACT, Impact.LOW)
            .set(AVAILABILITY_IMPACT, Impact.MEDIUM)
            .expectedScore(DoubleInterval.closed(0, 1))
            .expectedLabel(LOW)
            .make());

    vectors.add(
        newTestVector("log4j")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.A_LOT)
            .set(FUNCTIONALITY, Functionality.LOGGER)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.LOW)
            .set(testScoreValue(OssSecurityScore.class, 0.5))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.CONFIDENTIAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.HIGH)
            .set(INTEGRITY_IMPACT, Impact.HIGH)
            .set(AVAILABILITY_IMPACT, Impact.LOW)
            .expectedScore(DoubleInterval.closed(4, 5))
            .expectedLabel(HIGH)
            .make());

    vectors.add(
        newTestVector("client")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.QUITE_A_LOT)
            .set(FUNCTIONALITY, Functionality.NETWORKING)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.MEDIUM)
            .set(testScoreValue(OssSecurityScore.class, 1.5))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.CONFIDENTIAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.MEDIUM)
            .set(INTEGRITY_IMPACT, Impact.MEDIUM)
            .set(AVAILABILITY_IMPACT, Impact.LOW)
            .expectedScore(DoubleInterval.closed(3, 4))
            .expectedLabel(MEDIUM)
            .make());

    vectors.add(
        newTestVector("dom4j")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.QUITE_A_LOT)
            .set(FUNCTIONALITY, Functionality.PARSER)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.HIGH)
            .set(testScoreValue(OssSecurityScore.class, 1.5))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.CONFIDENTIAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.HIGH)
            .set(INTEGRITY_IMPACT, Impact.HIGH)
            .set(AVAILABILITY_IMPACT, Impact.HIGH)
            .expectedScore(DoubleInterval.closed(6, 7))
            .expectedLabel(HIGH)
            .make());

    vectors.add(
        newTestVector("databind")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.QUITE_A_LOT)
            .set(FUNCTIONALITY, Functionality.PARSER)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.HIGH)
            .set(testScoreValue(OssSecurityScore.class, 5))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.PERSONAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.HIGH)
            .set(INTEGRITY_IMPACT, Impact.HIGH)
            .set(AVAILABILITY_IMPACT, Impact.MEDIUM)
            .expectedScore(DoubleInterval.closed(4, 5))
            .expectedLabel(MEDIUM)
            .make());

    vectors.add(
        newTestVector("http")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.QUITE_A_LOT)
            .set(FUNCTIONALITY, Functionality.NETWORKING)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.MEDIUM)
            .set(testScoreValue(OssSecurityScore.class, 2))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.CONFIDENTIAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.MEDIUM)
            .set(INTEGRITY_IMPACT, Impact.MEDIUM)
            .set(AVAILABILITY_IMPACT, Impact.LOW)
            .expectedScore(DoubleInterval.closed(2, 3))
            .expectedLabel(MEDIUM)
            .make());

    vectors.add(
        newTestVector("olingo")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.SOME)
            .set(FUNCTIONALITY, Functionality.NETWORKING)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.HIGH)
            .set(testScoreValue(OssSecurityScore.class, 1))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.CONFIDENTIAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.HIGH)
            .set(INTEGRITY_IMPACT, Impact.HIGH)
            .set(AVAILABILITY_IMPACT, Impact.MEDIUM)
            .expectedScore(DoubleInterval.closed(5, 6))
            .expectedLabel(HIGH)
            .make());

    vectors.add(
        newTestVector("annotations")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.QUITE_A_LOT)
            .set(FUNCTIONALITY, Functionality.ANNOTATIONS)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.LOW)
            .set(testScoreValue(OssSecurityScore.class, 1))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.CONFIDENTIAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.MEDIUM)
            .set(INTEGRITY_IMPACT, Impact.MEDIUM)
            .set(AVAILABILITY_IMPACT, Impact.MEDIUM)
            .expectedScore(DoubleInterval.closed(2, 3))
            .expectedLabel(LOW)
            .make());

    vectors.add(
        newTestVector("spring")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.A_LOT)
            .set(FUNCTIONALITY, Functionality.APPLICATION_FRAMEWORK)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.HIGH)
            .set(testScoreValue(OssSecurityScore.class, 7))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.PERSONAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.HIGH)
            .set(INTEGRITY_IMPACT, Impact.HIGH)
            .set(AVAILABILITY_IMPACT, Impact.HIGH)
            .expectedScore(DoubleInterval.closed(4, 5))
            .expectedLabel(MEDIUM)
            .make());

    vectors.add(
        newTestVector("oauth")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.A_LOT)
            .set(FUNCTIONALITY, Functionality.SECURITY)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.HIGH)
            .set(testScoreValue(OssSecurityScore.class, 2))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.CONFIDENTIAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.HIGH)
            .set(INTEGRITY_IMPACT, Impact.HIGH)
            .set(AVAILABILITY_IMPACT, Impact.HIGH)
            .expectedScore(DoubleInterval.closed(8, 9))
            .expectedLabel(CRITICAL)
            .make());

    vectors.add(
        newTestVector("openssl")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.QUITE_A_LOT)
            .set(FUNCTIONALITY, Functionality.SECURITY)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.HIGH)
            .set(testScoreValue(OssSecurityScore.class, 8))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.CONFIDENTIAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.HIGH)
            .set(INTEGRITY_IMPACT, Impact.HIGH)
            .set(AVAILABILITY_IMPACT, Impact.MEDIUM)
            .expectedScore(DoubleInterval.closed(2, 3))
            .expectedLabel(MEDIUM)
            .make());

    vectors.add(
        newTestVector("java")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.A_LOT)
            .set(FUNCTIONALITY, Functionality.SDK)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.HIGH)
            .set(testScoreValue(OssSecurityScore.class, 2))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.CONFIDENTIAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.HIGH)
            .set(INTEGRITY_IMPACT, Impact.HIGH)
            .set(AVAILABILITY_IMPACT, Impact.HIGH)
            .expectedScore(DoubleInterval.closed(8, 9))
            .expectedLabel(CRITICAL)
            .make());

    vectors.add(
        newTestVector("python")
            .set(HOW_MANY_COMPONENTS_USE_OSS_PROJECT, Quantity.A_LOT)
            .set(FUNCTIONALITY, Functionality.SDK)
            .set(HANDLING_UNTRUSTED_DATA_LIKELIHOOD, Likelihood.HIGH)
            .set(testScoreValue(OssSecurityScore.class, 7))
            .set(IS_ADOPTED.no())
            .set(DATA_CONFIDENTIALITY, DataConfidentialityType.CONFIDENTIAL)
            .set(CONFIDENTIALITY_IMPACT, Impact.HIGH)
            .set(INTEGRITY_IMPACT, Impact.HIGH)
            .set(AVAILABILITY_IMPACT, Impact.HIGH)
            .expectedScore(DoubleInterval.closed(4, 5))
            .expectedLabel(MEDIUM)
            .make());

    SecurityRiskRatingIntroducedByOss rating
        = RatingRepository.INSTANCE.rating(SecurityRiskRatingIntroducedByOss.class);

    new RatingVerification(rating, vectors).run();
  }
}
