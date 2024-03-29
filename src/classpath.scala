/**********************************************************************************************\
* Rapture URI Library                                                                          *
* Version 0.10.0                                                                               *
*                                                                                              *
* The primary distribution site is                                                             *
*                                                                                              *
*   http://rapture.io/                                                                         *
*                                                                                              *
* Copyright 2010-2014 Jon Pretty, Propensive Ltd.                                              *
*                                                                                              *
* Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file    *
* except in compliance with the License. You may obtain a copy of the License at               *
*                                                                                              *
*   http://www.apache.org/licenses/LICENSE-2.0                                                 *
*                                                                                              *
* Unless required by applicable law or agreed to in writing, software distributed under the    *
* License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,    *
* either express or implied. See the License for the specific language governing permissions   *
* and limitations under the License.                                                           *
\**********************************************************************************************/
package rapture.uri
import rapture.core._

class ClasspathUrl(elements: Seq[String]) extends Url[ClasspathUrl](elements, Map()) {
  def makePath(ascent: Int, elements: Seq[String], afterPath: AfterPath) =
    new ClasspathUrl(elements)
  
  def schemeSpecificPart = elements.mkString("", "/", "")
  val pathRoot = Classpath
}

object Classpath extends PathRoot[ClasspathUrl] with Scheme[ClasspathUrl] {
  def schemeName = "classpath"
  def makePath(ascent: Int, elements: Seq[String], afterPath: AfterPath) =
    new ClasspathUrl(elements)
  def scheme = Classpath

  private val Matcher = """classpath:(.*)$""".r

  def parse(s: String): ClasspathUrl = s match {
    case Matcher(path) => makePath(0, path.split("/"), Map())
  }
}
