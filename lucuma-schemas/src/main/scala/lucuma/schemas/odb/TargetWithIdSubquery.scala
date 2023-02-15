// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import lucuma.schemas.ObservationDB
import lucuma.schemas.decoders.given
import lucuma.schemas.model.TargetWithId

object TargetWithIdSubquery extends GraphQLSubquery.Typed[ObservationDB, TargetWithId]("Target"):
  // FIXME Replace wavelength, angle, fluxdensity, etc. when subqueries can contain subqueries
  override val subquery: String = """  
    {
      id
      name
      sidereal {
        ra {
          microarcseconds
        }
        dec {
          microarcseconds
        }
        epoch
        properMotion {
          ra {
            microarcsecondsPerYear
          }
          dec {
            microarcsecondsPerYear
          }
        }
        radialVelocity {
          centimetersPerSecond
        }
        parallax {
          microarcseconds
        }
        catalogInfo {
          name
          id
          objectType
        }
      }
      sourceProfile {
        point {
          bandNormalized {
            sed {
              stellarLibrary
              coolStar
              galaxy
              planet
              quasar
              hiiRegion
              planetaryNebula
              powerLaw
              blackBodyTempK
              fluxDensities {
                wavelength {
                  picometers
                }
                density
              }
            }
            brightnesses {
              band
              value
              units
              error
            }
          }
          emissionLines {
            lines {
              wavelength {
                picometers
              }
              lineWidth
              lineFlux {
                value
                units
              }
            }
            fluxDensityContinuum {
              value
              units
            }
          }
        }
        uniform {
          bandNormalized {
            sed {
              stellarLibrary
              coolStar
              galaxy
              planet
              quasar
              hiiRegion
              planetaryNebula
              powerLaw
              blackBodyTempK
              fluxDensities {
                wavelength {
                  picometers
                }
                density
              }
            }
            brightnesses {
              band
              value
              units
              error
            }
          }
          emissionLines {
            lines {
              wavelength {
                picometers
              }
              lineWidth
              lineFlux {
                value
                units
              }
            }
            fluxDensityContinuum {
              value
              units
            }
          }
        }
        gaussian {
          fwhm {
            microarcseconds
          }
          bandNormalized {
            sed {
              stellarLibrary
              coolStar
              galaxy
              planet
              quasar
              hiiRegion
              planetaryNebula
              powerLaw
              blackBodyTempK
              fluxDensities {
                wavelength {
                  picometers
                }
                density
              }
            }
            brightnesses {
              band
              value
              units
              error
            }
          }
          emissionLines {
            lines {
              wavelength {
                picometers
              }
              lineWidth
              lineFlux {
                value
                units
              }
            }
            fluxDensityContinuum {
              value
              units
            }
          }
        }
      }
    }
  """
