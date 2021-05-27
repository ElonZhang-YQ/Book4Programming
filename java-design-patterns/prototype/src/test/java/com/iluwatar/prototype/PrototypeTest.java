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
package com.iluwatar.prototype;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Date: 12/28/15 - 8:45 PM
 * @param <P> Prototype
 * @author Jeroen Meulemeester
 */
public class PrototypeTest<P extends Prototype> {
  static Collection<Object[]> dataProvider() {
    return Arrays.asList(
            new Object[]{new OrcBeast("axe"), "Orcish wolf attacks with axe"},
            new Object[]{new OrcMage("sword"), "Orcish mage attacks with sword"},
            new Object[]{new OrcWarlord("laser"), "Orcish warlord attacks with laser"},
            new Object[]{new ElfBeast("cooking"), "Elven eagle helps in cooking"},
            new Object[]{new ElfMage("cleaning"), "Elven mage helps in cleaning"},
            new Object[]{new ElfWarlord("protecting"), "Elven warlord helps in protecting"}
    );
  }

  @ParameterizedTest
  @MethodSource("dataProvider")
  public void testPrototype(P testedPrototype, String expectedToString) throws Exception {
    assertEquals(expectedToString, testedPrototype.toString());

    final Object clone = testedPrototype.copy();
    assertNotNull(clone);
    assertNotSame(clone, testedPrototype);
    assertSame(testedPrototype.getClass(), clone.getClass());
  }

}
