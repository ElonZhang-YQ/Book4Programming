/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Seppälä
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.interpreter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Date: 12/14/15 - 12:08 PM
 *
 * @author Jeroen Meulemeester
 */
public class NumberExpressionTest extends ExpressionTest<NumberExpression> {

  /**
   * Create a new set of test entries with the expected result
   *
   * @return The list of parameters used during this test
   */
  @Override
  public Stream<Arguments> expressionProvider() {
    return prepareParameters((f, s) -> f);
  }

  /**
   * Create a new test instance using the given test parameters and expected result
   */
  public NumberExpressionTest() {
    super("number", (f, s) -> f);
  }

  /**
   * Verify if the {@link NumberExpression#NumberExpression(String)} constructor works as expected
   */
  @ParameterizedTest
  @MethodSource("expressionProvider")
  public void testFromString(NumberExpression first) throws Exception {
    final int expectedValue = first.interpret();
    final String testStringValue = String.valueOf(expectedValue);
    final NumberExpression numberExpression = new NumberExpression(testStringValue);
    assertEquals(expectedValue, numberExpression.interpret());
  }

}