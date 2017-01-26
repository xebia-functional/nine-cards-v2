/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.services.permissions.impl

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import cards.nine.commons.CatchAll
import cards.nine.commons.contexts.{ActivityContextSupport, ContextSupport}
import cards.nine.commons.services.TaskService
import cards.nine.models.types.{PermissionDenied, PermissionGranted}
import cards.nine.services.permissions._

class AndroidSupportPermissionsServices
    extends PermissionsServices
    with ImplicitsPermissionsServicesExceptions {

  override def checkPermissions(permissions: Seq[String])(
      implicit contextSupport: ContextSupport) =
    TaskService {
      CatchAll[PermissionsServicesException] {
        permissions.map { permission =>
          val status =
            ContextCompat.checkSelfPermission(contextSupport.context, permission) match {
              case PackageManager.PERMISSION_GRANTED => PermissionGranted
              case _                                 => PermissionDenied
            }
          permission -> status
        }.toMap
      }
    }

  override def shouldShowRequestPermissions(permissions: Seq[String])(
      implicit contextSupport: ActivityContextSupport) =
    TaskService {
      CatchAll[PermissionsServicesException] {
        withActivity { activity =>
          permissions.map { permission =>
            permission -> ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
          }.toMap
        }
      }
    }

  override def requestPermissions(requestCode: Int, permissions: Seq[String])(
      implicit contextSupport: ActivityContextSupport) =
    TaskService {
      CatchAll[PermissionsServicesException] {
        withActivity { activity =>
          ActivityCompat.requestPermissions(activity, permissions.toArray, requestCode)
        }
      }
    }

  def withActivity[T](f: (Activity) => T)(implicit contextSupport: ActivityContextSupport): T =
    contextSupport.getActivity match {
      case Some(activity) => f(activity)
      case None =>
        throw new IllegalStateException("Activity not found in the context")
    }

  override def readPermissionsRequestResult(permissions: Seq[String], grantResults: Seq[Int]) =
    TaskService {
      CatchAll[PermissionsServicesException] {
        ((permissions zip grantResults) map {
          case (permission, PackageManager.PERMISSION_GRANTED) =>
            permission -> PermissionGranted
          case (permission, _) => permission -> PermissionDenied
        }).toMap
      }
    }
}
