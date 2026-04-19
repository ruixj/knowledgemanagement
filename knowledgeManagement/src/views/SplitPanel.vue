<template>
  <div class="split-container">
    <!-- 垂直分割（左右布局） -->
    <div class="vertical-split">
      <div class="pane pane-1">左侧面板</div>
      <div class="pane pane-2">右侧面板</div>
    </div>

    <!-- 水平分割（上下布局） -->
    <div class="horizontal-split">
      <div class="pane pane-3">上方面板</div>
      <div class="pane pane-4">下方面板</div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue';
import Split from 'split.js';

// 存储分割线实例，用于组件卸载时清理
const splitInstances = ref([]);

onMounted(() => {
  // 初始化垂直分割
  const verticalSplit = Split(['.vertical-split .pane-1', '.vertical-split .pane-2'], {
    direction: 'vertical', // 垂直分割（左右）
    sizes: [30, 70], // 初始尺寸比例（百分比）
    minSize: [100, 100], // 最小尺寸（像素）
    gutterSize: 4, // 分割线宽度（像素）
    gutterStyle: (dimension, size, gutterSize) => ({
      // 自定义分割线样式
      background: '#e0e0e0',
      hover: { background: '#888' }
    }),
    onDrag: (sizes) => {
      // 拖拽时的回调
      console.log('垂直分割尺寸变化:', sizes);
    }
  });

  // 初始化水平分割
  const horizontalSplit = Split(['.horizontal-split .pane-3', '.horizontal-split .pane-4'], {
    direction: 'horizontal', // 水平分割（上下）
    sizes: [40, 60],
    minSize: [150, 150],
    gutterSize: 4,
    onDragEnd: (sizes) => {
      // 拖拽结束的回调
      console.log('水平分割最终尺寸:', sizes);
    }
  });

  // 保存实例
  splitInstances.value = [verticalSplit, horizontalSplit];
});

onUnmounted(() => {
  // 组件卸载时销毁实例，避免内存泄漏
  splitInstances.value.forEach(instance => {
    instance.destroy();
  });
});
</script>

<style scoped>
.split-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
  box-sizing: border-box;
}

.vertical-split, .horizontal-split {
  width: 100%;
  overflow: hidden;
}

.vertical-split {
  height: 50%;
  display: flex;
}

.horizontal-split {
  height: 50%;
  display: flex;
  flex-direction: column;
}

.pane {
  padding: 20px;
  overflow: auto;
}

.pane-1 { background-color: #f5f5f5; }
.pane-2 { background-color: #e9e9e9; }
.pane-3 { background-color: #f0f0f0; }
.pane-4 { background-color: #e0e0e0; }
</style>
