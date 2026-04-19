<template>
  <div class="file-upload">
    <div
      class="drop-area"
      @dragover.prevent="dragover"
      @dragleave.prevent="dragleave"
      @drop.prevent="drop"
    >
      <p>拖拽文件到此处或点击选择文件</p>
      <input
        type="file"
        ref="fileInput"
        @change="handleFileChange"
        multiple
        style="display: none"
      />
      <button @click="triggerFileInput">选择文件</button>
    </div>
    <div v-if="selectedFiles.length > 0" class="file-list">
      <div v-for="(file, index) in selectedFiles" :key="index" class="file-item">
        <p>{{ file.name }}</p>
        <button @click="removeFile(index)">删除</button>
      </div>
      <button @click="uploadFiles">上传所有文件</button>
    </div>
    <div v-if="uploadStatus" class="status">
      <p>{{ uploadStatus }}</p>
      <progress v-if="uploadStatus.includes('上传中')" :value="uploadStatus.match(/\d+/)?.[0] || 0" max="100"></progress>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import axios from 'axios';

export default {
  name: 'FileUpload',
  setup() {
    const fileInput = ref(null);
    const selectedFiles = ref([]);
    const uploadStatus = ref('');
    const isDragging = ref(false);

    const triggerFileInput = () => {
      fileInput.value.click();
    };

    const handleFileChange = (event) => {
      const files = Array.from(event.target.files);
      selectedFiles.value = [...selectedFiles.value, ...files];
    };

    const dragover = () => {
      isDragging.value = true;
    };

    const dragleave = () => {
      isDragging.value = false;
    };

    const drop = (event) => {
      isDragging.value = false;
      const files = Array.from(event.dataTransfer.files);
      selectedFiles.value = [...selectedFiles.value, ...files];
    };

    const removeFile = (index) => {
      selectedFiles.value.splice(index, 1);
    };

    const uploadFiles = () => {
      if (selectedFiles.value.length === 0) {
        uploadStatus.value = '请先选择文件';
        return;
      }

      uploadStatus.value = '上传中...';
      let progress = 0;

      let formData = new FormData();
      // 将每个文件添加到 FormData 对象中
      for (let i = 0; i < selectedFiles.value.length; i++) {
           let fileObj = selectedFiles.value[i];    
           //formData.append(fileObj.name, fileObj);
           //formData.append('fileName', fileObj.name);
           formData.append('files', selectedFiles.value[i]); // 注意这里的 'files[]' 应该与你后端期望的名称相匹配
      }

      axios.post('http://localhost:8088/api/process/file', 
        formData,
        {
          headers: {
              'Content-Type': 'multipart/form-data'
          },
          onUploadProgress: progressEvent => {
              // 上传进度处理函数
              let percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
              console.log(percentCompleted);
              uploadStatus.value = `上传中... ${progress}%`;
              if (percentCompleted >= 100) {
                //clearInterval(interval);
                uploadStatus.value = '上传成功';

              }
          }
        }
      )
      .then(response => {
          console.log('文件上传成功', response);
          selectedFiles.value = [];
      })
      .catch(error => {
          console.error('文件上传失败', error);
      });

      // for (let i = 0; i < selectedFiles.value.length; i++) {
      //       let fileObj = selectedFiles.value[i];        
      //       formData.append('file', fileObj);
      //       formData.append('fileName', fileObj.name);
      //       //formData.append('files[]', selectedFiles[i]); // 注意这里的 'files[]' 应该与你后端期望的名称相匹配
      
      //     axios.post('http://localhost:8088/api/process/file', 
      //      formData,
      //      {
      //         headers: {
      //             'Content-Type': 'multipart/form-data'
      //         },
      //         onUploadProgress: progressEvent => {
      //             // 上传进度处理函数
      //             let percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
      //             console.log(percentCompleted);
      //             uploadStatus.value = `上传中... ${progress}%`;
      //             if (percentCompleted >= 100) {
      //               //clearInterval(interval);
      //               uploadStatus.value = '上传成功';

      //             }
      //         }
      //      }
      //     )
      //     .then(response => {
      //         console.log('文件上传成功', response);
      //         selectedFiles.value = [];
      //     })
      //     .catch(error => {
      //         console.error('文件上传失败', error);
      //     });
      // }


 
      // const interval = setInterval(() => {
      //   progress += 10;
      //   uploadStatus.value = `上传中... ${progress}%`;
      //   if (progress >= 100) {
      //     clearInterval(interval);
      //     uploadStatus.value = '上传成功';
      //     selectedFiles.value = [];
      //   }
      // }, 200);
    };

    return {
      fileInput,
      selectedFiles,
      uploadStatus,
      isDragging,
      triggerFileInput,
      handleFileChange,
      dragover,
      dragleave,
      drop,
      removeFile,
      uploadFiles,
    };
  },
};
</script>

<style scoped>
.file-upload {
  margin: 20px;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
  max-width: 100%;
  box-sizing: border-box;
}

.drop-area {
  padding: 20px;
  border: 2px dashed #ccc;
  border-radius: 5px;
  text-align: center;
  margin-bottom: 20px;
  background-color: #f9f9f9;
}

.drop-area p {
  margin-bottom: 10px;
}

button {
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin: 5px;
}

button:hover {
  background-color: #0056b3;
}

.file-list {
  margin-top: 20px;
}

.file-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  border-bottom: 1px solid #eee;
}

.status {
  margin-top: 20px;
  color: #007bff;
}

@media (max-width: 768px) {
  .file-upload {
    padding: 10px;
  }
  button {
    width: 100%;
    margin: 5px 0;
  }
}
</style>