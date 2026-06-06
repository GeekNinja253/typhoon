<script setup lang="ts">
import { ref, watch, computed } from 'vue';

const props = defineProps<{
  frames: any[];
}>();

const emit = defineEmits(['update:frameIndex']);

const currentIndex = ref(0);
const isPlaying = ref(false);
let playInterval: any = null;

const currentFrameTime = computed(() => {
  if (!props.frames || props.frames.length === 0) return '';
  return props.frames[currentIndex.value]?.time || '';
});

watch(() => props.frames, () => {
  currentIndex.value = 0;
  isPlaying.value = false;
  if (playInterval) clearInterval(playInterval);
});

watch(currentIndex, (newVal) => {
  emit('update:frameIndex', newVal);
});

const togglePlay = () => {
  if (isPlaying.value) {
    pause();
  } else {
    play();
  }
};

const play = () => {
  if (props.frames.length === 0) return;
  isPlaying.value = true;
  if (currentIndex.value >= props.frames.length - 1) {
    currentIndex.value = 0;
  }
  playInterval = setInterval(() => {
    if (currentIndex.value < props.frames.length - 1) {
      currentIndex.value++;
    } else {
      pause();
    }
  }, 300); // 300ms per frame for smoother playback
};

const pause = () => {
  isPlaying.value = false;
  if (playInterval) clearInterval(playInterval);
};

const onSliderChange = (e: Event) => {
  const val = (e.target as HTMLInputElement).value;
  currentIndex.value = parseInt(val, 10);
};
</script>

<template>
  <div class="timeline-player" v-if="frames.length > 0">
    <div class="controls">
      <button class="play-btn" @click="togglePlay">
        {{ isPlaying ? '暂停' : '播放' }}
      </button>
      <div class="time-display">
        {{ currentFrameTime }}
      </div>
    </div>
    
    <div class="slider-container">
      <input 
        type="range" 
        :min="0" 
        :max="frames.length - 1" 
        :value="currentIndex" 
        @input="onSliderChange"
        class="slider"
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
  background: white;
  padding: 12px 24px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 60%;
  min-width: 400px;
  z-index: 1000;
}

.controls {
  display: flex;
  align-items: center;
  gap: 16px;
}

.play-btn {
  background: var(--primary-color, #1890ff);
  color: white;
  border: none;
  padding: 6px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
}

.play-btn:hover {
  background: #40a9ff;
}

.time-display {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.slider-container {
  width: 100%;
}

.slider {
  width: 100%;
  cursor: pointer;
}
</style>
