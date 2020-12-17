package com.sap.sgs.phosphor.fosstars.model;

import java.util.List;

/**
 * An advisor that can give advices about a rating and scores of a subject.
 */
public interface Advisor {

  /**
   * Get a list of advices for a subject.
   *
   * @param subject The subject.
   * @return A list of advices for the subject.
   */
  List<Advice> adviseFor(Subject subject);
}
