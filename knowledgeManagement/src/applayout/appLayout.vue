<template>
  <div class="layout">
    <div class="header">
       党建知识库管理系统
    </div>
    <div class="body">
      <div ref="leftPanel" class="sidebar">
        <nav>
          <ul>
            <li><RouterLink to="/app/fileupload">文件上传</RouterLink></li>
            <li><RouterLink to="/app/fileupload2">文件上传2</RouterLink></li>
            <li><RouterLink to="/app/fileList">文件列表</RouterLink></li>
            <li><RouterLink to="/app/fileManager">文件管理</RouterLink></li>
            <li><RouterLink to="/app/responsiveTable">响应式表格</RouterLink></li>
           
          </ul>
        </nav>
      </div>
      <div ref="resizer" class="resizer"></div>
      <div ref="rightPanel" class="content">
        <RouterView />
      </div>
    </div>
    <div class="footer">
      北京智慧云科技有限公司 
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref ,onUnmounted } from 'vue';
import { RouterLink } from 'vue-router';

const leftPanel = ref(null)
const rightPanel = ref(null)
const resizer = ref(null)
let   isDragging = false;
let   initialX  = 0;
let   initialY  = 0;
 
let   initialLeftWidth =0;
let   initialRightWidth =0;

const mouseDownHandler = function (e) {
    isDragging = true;

    // Get the current mouse position
    initialX = e.clientX;
    initialY = e.clientY;

    //Get the initial width of the left and right panels
    initialLeftWidth = leftPanel.value.getBoundingClientRect().width;
    initialRightWidth = rightPanel.value.getBoundingClientRect().width;

 
    // Attach the listeners to `document`
    document.addEventListener('mousemove', mouseMoveHandler);
    document.addEventListener('mouseup', mouseUpHandler);

    document.addEventListener('touchmove', mouseMoveHandler);
    document.addEventListener('touchend', mouseUpHandler);

};

const mouseMoveHandler = function (e) {
    if (!isDragging) return;

    // How far the mouse has been moved
    const deltaX  = e.clientX - initialX;
    const deltaY  = e.clientY - initialY;

    let newLeftWidth = initialLeftWidth + deltaX;
    let newRightWidth = initialRightWidth - deltaX;

    const newRightWidthPercent =  (newRightWidth * 100) / resizer.value.parentNode.getBoundingClientRect().width;
    rightPanel.value.style.width = `${newRightWidthPercent}%`;
    leftPanel.value.style.width = `${100 - newRightWidthPercent}%`;

    //leftPanel.value.style.width  = `${initialLeftWidth  + deltaX}px`;
    //rightPanel.value.style.width = `${initialRightWidth - deltaX}px`;

    resizer.value.style.cursor = 'col-resize';
    document.body.style.cursor = 'col-resize';
};

const mouseUpHandler = function () {
    resizer.value.style.removeProperty('cursor');
    document.body.style.removeProperty('cursor');

    //efContainer.value.style.removeProperty('user-select');
    //efContainer.value.style.removeProperty('pointer-events');

    resizer.value.style.removeProperty('user-select');
    resizer.value.style.removeProperty('pointer-events');

    // Remove the handlers of `mousemove` and `mouseup`
    document.removeEventListener('mousemove', mouseMoveHandler);
    document.removeEventListener('mouseup', mouseUpHandler);

    document.removeEventListener('touchmove', mouseMoveHandler);
    document.removeEventListener('touchend', mouseUpHandler);
};
 
onMounted(() => {
  resizer.value.addEventListener('mousedown', mouseDownHandler);
  resizer.value.addEventListener('touchstart', mouseDownHandler,{ passive: true });

})
onUnmounted(() => {
 
});
 
</script>

<style scoped>
.layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.header {
  background-color: #007bff;
  color: white;
  padding: 1rem;
  text-align: center;
 
}

.body {
  display: flex;
  flex: 1;
}

.sidebar {
  /*width: 200px;*/
  background-color: #f8f9fa;
  padding: 1rem;
  border-right: 1px solid #ddd;
}

.sidebar ul {
  list-style: none;
  padding: 0;
}

.sidebar li {
  margin-bottom: 1rem;
}

.sidebar a {
  text-decoration: none;
  color: #333;
}

.sidebar a:hover {
  color: #007bff;
}

.content {
  flex: 1;
  padding: 1rem;
}

.footer {
  background-color: #d7dee4;
  color: white;
  padding: 1rem;
  text-align: center;
}



.resizer {
    background-color: #cbd5e0;
    cursor: ew-resize;
    
    width: 2px;
}
</style>