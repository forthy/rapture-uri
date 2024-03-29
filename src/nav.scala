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

import java.io._
import java.net._

trait UriMethods extends ModeGroup

trait Navigable[UrlType] {
  
  private implicit val errorHandler = raw
  
  def children(url: UrlType)(implicit mode: Mode[UriMethods]): mode.Wrap[Seq[UrlType], Exception]
  
  /** Returns false if the filesystem object represented by this FileUrl is a file, and true if
    * it is a directory. */
  def isDirectory(url: UrlType)(implicit mode: Mode[UriMethods]): mode.Wrap[Boolean, Exception]
  
  /** If this represents a directory, returns an iterator over all its descendants,
    * otherwise returns the empty iterator. */
  def descendants(url: UrlType)(implicit mode: Mode[UriMethods]):
      mode.Wrap[Iterator[UrlType], Exception] =
    mode wrap {
      children(url).iterator.flatMap { c =>
        if(isDirectory(c)) Iterator(c) ++ descendants(c)
        else Iterator(c)
      }
    }
}

class NavigableExtras[UrlType: Navigable](url: UrlType) {
  
  protected implicit val errorHandler = raw
  
  /** Return a sequence of children of this URL */
  def children(implicit mode: Mode[UriMethods]) =
    mode wrap ?[Navigable[UrlType]].children(url)
  
  /** Return true if this URL node is a directory (i.e. it can contain other URLs). */
  def isDirectory(implicit mode: Mode[UriMethods]): mode.Wrap[Boolean, Exception] =
    mode wrap ?[Navigable[UrlType]].isDirectory(url)(raw)

  /** Return an iterator of all descendants of this URL. */
  def descendants(implicit mode: Mode[UriMethods]): mode.Wrap[Iterator[UrlType], Exception] =
    mode wrap ?[Navigable[UrlType]].descendants(url)(raw)

  def walkFilter(cond: UrlType => Boolean)(implicit mode: Mode[UriMethods]):
      mode.Wrap[Seq[UrlType], Exception] = mode wrap {
    children(raw) filter cond flatMap { f =>
      new NavigableExtras(f).walkFilter(cond)
    }
  }
}
