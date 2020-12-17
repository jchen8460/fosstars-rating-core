package com.sap.sgs.phosphor.fosstars.model.subject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.sgs.phosphor.fosstars.model.Subject;
import com.sap.sgs.phosphor.fosstars.model.value.RatingValue;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * A base class for a subject.
 */
public abstract class AbstractSubject implements Subject {

  /**
   * A rating value for the subject.
   */
  private RatingValue ratingValue;

  /**
   * When the rating value was calculated.
   */
  private Date ratingValueDate;

  /**
   * Initializes a new subject.
   *
   * @param ratingValue A rating value for the project.
   * @param ratingValueDate When the rating value was calculated.
   */
  @JsonCreator
  public AbstractSubject(
      @JsonProperty("ratingValue") RatingValue ratingValue,
      @JsonProperty("ratingValueDate") Date ratingValueDate) {

    this.ratingValue = ratingValue;
    this.ratingValueDate = ratingValueDate;
  }

  /**
   * Initializes a new subject.
   */
  public AbstractSubject() {
    this(NO_RATING_VALUE, NO_RATING_DATE);
  }

  @Override
  public Optional<Date> ratingValueDate() {
    return Optional.ofNullable(ratingValueDate);
  }

  @Override
  public Optional<RatingValue> ratingValue() {
    return Optional.ofNullable(ratingValue);
  }

  @Override
  public void set(RatingValue value) {
    ratingValue = value;
    ratingValueDate = new Date();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof AbstractSubject == false) {
      return false;
    }
    AbstractSubject that = (AbstractSubject) o;
    return Objects.equals(ratingValue, that.ratingValue)
        && Objects.equals(ratingValueDate, that.ratingValueDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ratingValue, ratingValueDate);
  }

  /**
   * Returns the rating value. The method is used for serialization.
   *
   * @return The rating value.
   */
  @JsonGetter("ratingValue")
  private RatingValue getRatingValue() {
    return ratingValue;
  }

  /**
   * Returns the date when the rating value was calculated. The method is used for serialization.
   *
   * @return The date.
   */
  @JsonGetter("ratingValueDate")
  private Date getRatingValueDate() {
    return ratingValueDate;
  }

}
