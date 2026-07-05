<script setup lang="ts">
import { ref, watch, computed, onBeforeUnmount } from 'vue';

const props = defineProps<{
  frames: any[];
}>();

const emit = defineEmits(['update:frameIndex']);

const currentIndex = ref(0);
const isPlaying = ref(false);

let playInterval: number | null = null;

const currentFrameTime = computed(() => {
  if (!props.frames.length) return '';
  return props.frames[currentIndex.value]?.time || '';
});

const progressPercent = computed(() => {
  if (props.frames.length <= 1) return 0;

  return (
    currentIndex.value /
    (props.frames.length - 1)
  ) * 100;
});

watch(
  () => props.frames,
  () => {
    currentIndex.value = 0;
    pause();
  }
);

watch(currentIndex, (val) => {
  emit('update:frameIndex', val);
});

const play = () => {
  if (!props.frames.length) return;

  if (currentIndex.value >= props.frames.length - 1) {
    currentIndex.value = 0;
  }

  isPlaying.value = true;

  playInterval = window.setInterval(() => {
    if (currentIndex.value < props.frames.length - 1) {
      currentIndex.value++;
    } else {
      pause();
    }
  }, 300);
};

const pause = () => {
  isPlaying.value = false;

  if (playInterval !== null) {
    clearInterval(playInterval);
    playInterval = null;
  }
};

const togglePlay = () => {
  isPlaying.value ? pause() : play();
};

const onSliderChange = (e: Event) => {
  currentIndex.value = Number(
    (e.target as HTMLInputElement).value
  );
};

const jumpToFrame = (index: number) => {
  currentIndex.value = index;
};

onBeforeUnmount(() => {
  pause();
});
</script>

<template>
  <div
    class="timeline-player"
    v-if="frames.length > 0"
  >
    <div class="controls">
      <button
        class="play-btn"
        @click="togglePlay"
      >
        {{ isPlaying ? '暂停' : '播放' }}
      </button>

      <div class="time-display">
        {{ currentFrameTime }}
      </div>
    </div>

    <div class="timeline">

      <!-- 灰色轨道 -->
      <div class="track"></div>

      <!-- 蓝色进度 -->
      <div
        class="progress"
        :style="{
          width: `${progressPercent}%`
        }"
      ></div>

      <!-- 节点 -->
      <div class="ticks">
        <div
          v-for="(_, index) in frames"
          :key="index"
          class="tick"
          :class="{
            active: index <= currentIndex,
            current: index === currentIndex
          }"
          @click="jumpToFrame(index)"
        ></div>
      </div>

      <!-- 透明滑块 -->
      <input
        class="slider"
        type="range"
        :min="0"
        :max="frames.length - 1"
        :step="1"
        :value="currentIndex"
        @input="onSliderChange"
      />
    </div>
  </div>
</template>
<style scoped>
.timeline-player {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);

  width: 60%;
  min-width: 400px;

  padding: 16px 28px;

  background: rgba(255,255,255,.95);

  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);

  border-radius: 16px;

  box-shadow:
    0 8px 32px rgba(0,0,0,.2);

  z-index: 1000;
}

.controls {
  display: flex;
  align-items: center;
  gap: 16px;

  margin-bottom: 14px;
}

.play-btn {
  border: none;

  color: white;

  cursor: pointer;

  padding: 8px 20px;

  border-radius: 8px;

  font-weight: 600;

  background:
    linear-gradient(
      135deg,
      #667eea,
      #764ba2
    );
}

.time-display {
  color: #667eea;
  font-weight: 600;
}

.timeline {
  position: relative;

  height: 28px;
}

/* 灰色轨道 */

.track {
  position: absolute;

  left: 0;
  right: 0;

  top: 50%;

  transform: translateY(-50%);

  height: 4px;

  background: #d9d9d9;

  border-radius: 2px;
}

/* 蓝色进度 */

.progress {
  position: absolute;

  left: 0;

  top: 50%;

  transform: translateY(-50%);

  height: 4px;

  background: #667eea;

  border-radius: 2px;
}

/* 节点层 */

.ticks {
  position: absolute;

  inset: 0;

  display: flex;

  justify-content: space-between;

  align-items: center;
}

.tick {
  width: 10px;
  height: 10px;

  border-radius: 50%;

  background: #bfbfbf;

  cursor: pointer;

  z-index: 3;

  transition: .2s;
}

.tick.active {
  background: #667eea;
}

.tick.current {
  transform: scale(1.5);

  box-shadow:
    0 0 10px rgba(102,126,234,.6);
}

/* 透明 slider */

.slider {
  position: absolute;

  inset: 0;

  width: 100%;
  height: 100%;

  opacity: 0;

  cursor: pointer;

  z-index: 4;
}
</style>