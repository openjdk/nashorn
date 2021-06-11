/*
 * Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/**
 * JDK-8261926: Attempt to access property/element of a Java method results in AssertionError: unknown call type
 *
 * @test
 * @run
 */

var v = java.lang.String.valueOf

isVCallUndefined()
v.call = 1
isVCallUndefined()

function isVCallUndefined() {
  Assert.assertEquals(typeof(v.call), "undefined")
}

function mustFailWithTypeError(f){
  try {
    f()
    Assert.fail("Did not fail with TypeError")
  } catch (e) {
    if (!(e instanceof TypeError)) {
      Assert.fail("Should have failed with TypeError, it was instead " + e)
    }
  }
}

(function() {
  "use strict";
  Assert.assertEquals(typeof(v.call), "undefined")
  mustFailWithTypeError(function() { 
    v.call = 1
  })
  isVCallUndefined()
  Assert.assertTrue(delete v.call)  
})()

Assert.assertTrue(delete v.call)
isVCallUndefined()
