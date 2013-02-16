/*
 * Copyright (C) 2012-2013 Age Mooij
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scalapenos.riak


// ============================================================================
// RiakMeta
// ============================================================================

final case class RiakMeta[T](
  data: T,
  contentType: ContentType,
  vclock: VClock,
  etag: ETag,
  lastModified: DateTime
) {

  def map(f: T => T): RiakMeta[T] = RiakMeta(f(data), contentType, vclock, etag, lastModified)
  def toRiakValue(implicit writer: RiakValueWriter[T]): RiakValue = implicitly[RiakValueWriter[T]].write(this)

}

object RiakMeta {
  // def apply[T](data: T, contentType: ContentType): RiakMeta[T] = apply(value, contentType, VClock.NotSpecified, ETag.NotSpecified, DateTime.now)
}


// ============================================================================
// RiakValue
// ============================================================================

final case class RiakValue(
  data: String,
  contentType: ContentType,
  vclock: VClock,
  etag: ETag,
  lastModified: DateTime
  // indexes: Set[RiakIndex]
) {
  import scala.util._
  import converters._

  def as[T: RiakValueReader]: Try[T] = implicitly[RiakValueReader[T]].read(this)

  def toMeta[T: RiakValueReader]: Try[RiakMeta[T]] =
    implicitly[RiakValueReader[T]].read(this)
                                  .map(data => RiakMeta(data, contentType, vclock, etag, lastModified))
}

object RiakValue {
  // use the magnet pattern so we can have overloads that would break due to type-erasure

  // def apply(value: String): RiakValue = {
  //   apply(value, VClock.NotSpecified)
  // }

  // def apply(value: String, vclock: VClock): RiakValue = {
  //   apply(value, ContentType.`text/plain`, vclock)
  // }

  // def apply(value: String, contentType: ContentType): RiakValue = {
  //   apply(value, contentType, VClock.NotSpecified, ETag.NotSpecified, DateTime.now)
  // }

  // def apply(value: String, contentType: ContentType, vclock: VClock): RiakValue = {
  //   apply(value, contentType, vclock, "", DateTime.now)
  // }

  // def apply(value: Array[Byte], contentType: ContentType, vclock: VClock, etag: String, lastModified: DateTime): RiakValue = {
  //   RiakValue(new String(value, contentType.charset.nioCharset), contentType, vclock, etag, lastModified)
  // }

  // def apply[T: RiakValueWriter](value: T): RiakValue = implicitly[RiakValueWriter[T]].write(value)
  // def apply[T: RiakValueWriter](value: T, vclock: VClock): RiakValue = implicitly[RiakValueWriter[T]].write(value, vclock)

}
