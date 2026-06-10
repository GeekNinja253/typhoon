<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as Cesium from 'cesium'
import 'cesium/Build/Cesium/Widgets/widgets.css'

type TyphoonPoint = {
  lng: number
  lat: number
  time: number
  grade: number
  isFuture: boolean
}

const mapContainer = ref<HTMLElement | null>(null)

let viewer: Cesium.Viewer | null = null
let allPoints: TyphoonPoint[] = []

let pathEntities: Cesium.Entity[] = []
let dynamicEntities: Cesium.Entity[] = []
let resizeObserver: ResizeObserver | null = null

// 根据台风等级获取对应的颜色
function getGradeColor(grade: number, isFuture: boolean = false) {
  let color = Cesium.Color.fromCssColorString('#00D5CB'); // TD, <=7
  if (grade >= 8 && grade <= 9) color = Cesium.Color.fromCssColorString('#FCFA00'); // TS
  else if (grade >= 10 && grade <= 11) color = Cesium.Color.fromCssColorString('#FDAA09'); // STS
  else if (grade >= 12 && grade <= 13) color = Cesium.Color.fromCssColorString('#FB3320'); // TY
  else if (grade >= 14 && grade <= 15) color = Cesium.Color.fromCssColorString('#F600A9'); // STY
  else if (grade >= 16) color = Cesium.Color.fromCssColorString('#AA0000'); // SuperTY
  
  return isFuture ? color.withAlpha(0.5) : color;
}

function initCesium() {
  if (!mapContainer.value || viewer) return;
  
  viewer = new Cesium.Viewer(mapContainer.value, {
    animation: false,
    timeline: false,
    geocoder: false,
    homeButton: false,
    sceneModePicker: false,
    navigationHelpButton: false,
    fullscreenButton: false,
    infoBox: false,
    selectionIndicator: false,
    baseLayerPicker: false,
    terrainProvider: new Cesium.EllipsoidTerrainProvider()
  })

  // 先不开启极致按需渲染，给地球贴图一点时间加载
  viewer.scene.requestRenderMode = false
  setTimeout(() => {
    if (viewer) viewer.scene.requestRenderMode = true
  }, 3000)

  viewer.clock.shouldAnimate = false
  viewer.clock.multiplier = 0

  // 关闭高耗能的大气渲染
  viewer.scene.globe.enableLighting = false
  viewer.scene.fog.enabled = false
  viewer.scene.skyAtmosphere.show = false

  // 监听地图点击事件：点击地球上的某点，平滑转动并下降（聚焦拉近细节），不再转为 2D
  const handler = new Cesium.ScreenSpaceEventHandler(viewer.scene.canvas)
  handler.setInputAction((click: any) => {
    if (!viewer) return
    const cartesian = viewer.camera.pickEllipsoid(click.position, viewer.scene.globe.ellipsoid)
    if (cartesian) {
      playAnimation(3000)
      const cartographic = Cesium.Cartographic.fromCartesian(cartesian)
      
      // 完全尊重当前用户的相机状态，仅仅进行位置移动（平移）
      viewer.camera.flyTo({
        destination: Cesium.Cartesian3.fromRadians(
          cartographic.longitude, 
          cartographic.latitude, 
          viewer.camera.positionCartographic.height // 保持当前高度
        ),
        orientation: {
          heading: viewer.camera.heading, // 保持当前偏航角
          pitch: viewer.camera.pitch,     // 保持当前俯仰角
          roll: 0.0
        },
        duration: 2.5
      })
    }
  }, Cesium.ScreenSpaceEventType.LEFT_CLICK)
}

/* ---------------- 初始化三维地球 ---------------- */
onMounted(() => {
  if (!mapContainer.value) return;

  // 严谨的 ResizeObserver：确保父容器在真正有了具体的宽和高之后，再初始化地球
  // 彻底解决 Vue 路由跳转或者 Flex 动画期间因为尺寸为 0 导致地球初始偏移和错误放大的问题
  resizeObserver = new ResizeObserver((entries) => {
    const entry = entries[0];
    if (entry.contentRect.width > 50 && entry.contentRect.height > 50) {
      if (!viewer) {
        initCesium();
      } else {
        viewer.resize();
        viewer.scene.requestRender();
      }
    }
  });

  resizeObserver.observe(mapContainer.value);
})

onBeforeUnmount(() => {
  if (resizeObserver) {
    resizeObserver.disconnect();
    resizeObserver = null;
  }
  viewer?.destroy()
})

/* ---------------- 清空地图 ---------------- */
function clearMap() {
  if (!viewer) return
  const v = viewer
  v.entities.removeAll()
  pathEntities = []
  dynamicEntities = []
  allPoints = []
  if (animationTimeout) {
    clearTimeout(animationTimeout)
    animationTimeout = null
  }
  v.scene.requestRenderMode = true // ensure it's not frozen
  v.scene.requestRender()
}

let animationTimeout: any = null;
function playAnimation(durationMs: number = 2500) {
  if (!viewer) return
  viewer.scene.requestRenderMode = false
  if (animationTimeout) clearTimeout(animationTimeout)
  animationTimeout = setTimeout(() => {
    if (viewer) viewer.scene.requestRenderMode = true
  }, durationMs)
}

/* ---------------- 真实路径 ---------------- */
function loadRealPath(data: any[]) {
  if (!viewer) return
  clearMap()
  allPoints = data

  const positions: number[] = []
  const v = viewer

  data.forEach(item => {
    positions.push(item.longitude)
    positions.push(item.latitude)

    const grade = item.grade || 7;
    const point = v.entities.add({
      position: Cesium.Cartesian3.fromDegrees(item.longitude, item.latitude),
      point: {
        pixelSize: 8,
        color: getGradeColor(grade, false)
      }
    })
    pathEntities.push(point)
  })

  const line = v.entities.add({
    polyline: {
      positions: Cesium.Cartesian3.fromDegreesArray(positions),
      width: 3,
      material: Cesium.Color.DODGERBLUE
    }
  })
  pathEntities.push(line)
  
  // 核心特效：平滑在 3D 下平移镜头，不再转为 2D 且不改变俯仰角
  playAnimation(3000)
  if (allPoints.length > 0) {
    const targetPoint = allPoints[0]
    const targetLng = targetPoint.lng || targetPoint.longitude || targetPoint.lon
    const targetLat = targetPoint.lat || targetPoint.latitude
    v.camera.flyTo({
      destination: Cesium.Cartesian3.fromDegrees(
        targetLng,
        targetLat,
        v.camera.positionCartographic.height
      ),
      orientation: {
        heading: v.camera.heading,
        pitch: v.camera.pitch,
        roll: 0.0
      },
      duration: 2.5
    })
  }
}

/* ---------------- 生成预测台风数据 ---------------- */
function loadPredictedPath(data: any, pathPoints: any[]) {
  if (!viewer) return
  clearMap()

  const startLat = data.latitude
  const startLng = data.longitude
  const grade = data.grade || 12
  const startTime = new Date(data.startTime)

  allPoints = pathPoints.map(p => ({
    lat: p.lat,
    lng: p.longitude || p.lon, // Handle both formats
    time: p.time,
    grade: p.grade,
    isFuture: p.isFuture
  }))

  const past = allPoints.filter(p => !p.isFuture)
  const future = allPoints.filter(p => p.isFuture)
  const v = viewer

  if (past.length > 0) {
    v.entities.add({
      polyline: {
        positions: Cesium.Cartesian3.fromDegreesArray(past.flatMap(p => [p.lng, p.lat])),
        width: 3,
        material: Cesium.Color.BLUE
      }
    })
  }

  if (future.length > 0) {
    // Include the last past point to connect the lines
    let futureLinePoints = [...future]
    if (past.length > 0) {
      futureLinePoints = [past[past.length - 1], ...future]
    }
    
    v.entities.add({
      polyline: {
        positions: Cesium.Cartesian3.fromDegreesArray(futureLinePoints.flatMap(p => [p.lng, p.lat])),
        width: 3,
        material: Cesium.Color.ORANGE.withAlpha(0.8)
      }
    })
  }

  allPoints.forEach(p => {
    v.entities.add({
      position: Cesium.Cartesian3.fromDegrees(p.lng, p.lat),
      point: {
        pixelSize: p.isFuture ? 6 : 8,
        color: getGradeColor(p.grade, p.isFuture),
        outlineColor: Cesium.Color.WHITE,
        outlineWidth: p.isFuture ? 0 : 1
      }
    })
  })

  // 核心特效：平滑在 3D 下平移镜头，不再转为 2D 且不改变俯仰角
  playAnimation(3000)
  if (allPoints.length > 0) {
    const targetPoint = allPoints[0]
    const targetLng = targetPoint.lng || targetPoint.longitude || targetPoint.lon
    const targetLat = targetPoint.lat || targetPoint.latitude
    v.camera.flyTo({
      destination: Cesium.Cartesian3.fromDegrees(
        targetLng,
        targetLat,
        v.camera.positionCartographic.height
      ),
      orientation: {
        heading: v.camera.heading,
        pitch: v.camera.pitch,
        roll: 0.0
      },
      duration: 2.5
    })
  }
}

/* ---------------- 动画帧 + 风圈 ---------------- */
function renderFrame(index: number) {
  if (!viewer) return
  if (!allPoints.length || index >= allPoints.length) return

  const v = viewer
  dynamicEntities.forEach(e => v.entities.remove(e))
  dynamicEntities = []

  const pt = allPoints[index]

  const active = v.entities.add({
    position: Cesium.Cartesian3.fromDegrees(pt.lng, pt.lat),
    point: {
      pixelSize: 12,
      color: Cesium.Color.RED
    },
    label: {
      text: new Date(pt.time).toLocaleString(),
      font: '14px sans-serif',
      fillColor: Cesium.Color.WHITE,
      style: Cesium.LabelStyle.FILL_AND_OUTLINE,
      outlineWidth: 2
    }
  })
  dynamicEntities.push(active)

  // 7级风圈: 半径随等级推断变化
  if (pt.grade >= 7) {
    const radius = 150000 + Math.max(0, pt.grade - 7) * 15000;
    const c7 = v.entities.add({
      position: Cesium.Cartesian3.fromDegrees(pt.lng, pt.lat),
      ellipse: {
        semiMajorAxis: radius,
        semiMinorAxis: radius,
        material: Cesium.Color.LIMEGREEN.withAlpha(0.25)
      }
    })
    dynamicEntities.push(c7)
  }

  // 10级风圈: 半径随等级推断变化
  if (pt.grade >= 10) {
    const radius = 80000 + Math.max(0, pt.grade - 10) * 12000;
    const c10 = v.entities.add({
      position: Cesium.Cartesian3.fromDegrees(pt.lng, pt.lat),
      ellipse: {
        semiMajorAxis: radius,
        semiMinorAxis: radius,
        material: Cesium.Color.ORANGE.withAlpha(0.35)
      }
    })
    dynamicEntities.push(c10)
  }

  // 12级风圈: 半径随等级推断变化
  if (pt.grade >= 12) {
    const radius = 40000 + Math.max(0, pt.grade - 12) * 10000;
    const c12 = v.entities.add({
      position: Cesium.Cartesian3.fromDegrees(pt.lng, pt.lat),
      ellipse: {
        semiMajorAxis: radius,
        semiMinorAxis: radius,
        material: Cesium.Color.RED.withAlpha(0.45)
      }
    })
    dynamicEntities.push(c12)
  }
  
  v.scene.requestRender()
}

defineExpose({
  loadRealPath,
  loadPredictedPath,
  renderFrame,
  clearMap
})
</script>

<template>
  <div class="map-container" ref="mapContainer"></div>
</template>

<style>
.map-container {
  /* 使用绝对定位强制填满父元素 .map-box，彻底解决 flex 结算过程中的 0 高度/宽度问题 */
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #f0f2f5;
  overflow: hidden;
}
.typhoon-tooltip {
  background-color: rgba(255, 255, 255, 0.9);
  border: 1px solid #1890ff;
  border-radius: 4px;
  font-weight: 500;
  box-shadow: 0 2px 6px rgba(0,0,0,0.15);
}
</style>
