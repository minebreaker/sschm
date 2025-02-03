package rip.deadcode.sschm.service.car_info_list

import java.util.UUID

/** A basic car information.
  * @param odo
  *   The odo of the latest event. [km]
  */
case class CarInfo(
    carId: String,
    name: String,
    odo: Int,
    photoId: String
)
