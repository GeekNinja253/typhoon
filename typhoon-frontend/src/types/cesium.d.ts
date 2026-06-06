import * as Cesium from 'cesium'

declare module 'cesium' {
  interface Entity {
    isDestroyed?: () => boolean
  }

  interface Viewer {
    isDestroyed?: () => boolean
  }

  interface Camera {
    cancelFlight?: () => void
  }
  
  interface Scene {
    skyAtmosphere?: Cesium.SkyAtmosphere
  }
}