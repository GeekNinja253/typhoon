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
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  padding: 16px 28px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  flex-direction: column;
  gap: 12px;
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
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
}

.play-btn:hover {
  background: linear-gradient(135deg, #764ba2, #667eea);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.time-display {
  font-size: 14px;
  font-weight: 600;
  color: #667eea;
}

.slider-container {
  width: 100%;
}

.slider {
  width: 100%;
  cursor: pointer;
  height: 6px;
  border-radius: 3px;
  appearance: none;
  background: linear-gradient(90deg, #667eea, #764ba2);
}

.slider::-webkit-slider-thumb {
  appearance: none;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #fff;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  transition: all 0.2s ease;
}

.slider::-webkit-slider-thumb:hover {
  transform: scale(1.2);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}
</style>
