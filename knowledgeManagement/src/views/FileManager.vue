<template>
  <a-layout class="file-manager">
    <a-layout-content>
      <div class="container">
        <!-- 搜索和上传区域 -->
        <div class="search-upload-section">
          <a-row :gutter="[16, 16]" align="middle">
            <a-col :xs="24" :sm="24" :md="16" :lg="18" :xl="18">
              <a-form layout="vertical" :model="searchForm">
                <a-row :gutter="[16, 16]">
                  <a-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
                    <a-form-item label="文件名">
                      <a-input
                        v-model:value="searchForm.fileName"
                        placeholder="输入文件名"
                        allow-clear
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
                    <a-form-item label="上传者">
                      <a-input
                        v-model:value="searchForm.uploader"
                        placeholder="输入上传者"
                        allow-clear
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
                    <a-form-item label="上传日期">
                      <a-date-picker
                        v-model:value="searchForm.uploadDate"
                        style="width: 100%"
                        placeholder="选择日期"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
                    <a-form-item label="文件类型">
                      <a-select
                        v-model:value="searchForm.fileType"
                        placeholder="选择文件类型"
                        allow-clear
                      >
                        <a-select-option value="doc">文档</a-select-option>
                        <a-select-option value="image">图片</a-select-option>
                        <a-select-option value="video">视频</a-select-option>
                        <a-select-option value="audio">音频</a-select-option>
                        <a-select-option value="other">其他</a-select-option>
                      </a-select>
                    </a-form-item>
                  </a-col>
                </a-row>
              </a-form>
            </a-col>
            <a-col :xs="24" :sm="24" :md="8" :lg="6" :xl="6" class="upload-btn-col">
              <a-button
                type="primary"
                icon="upload"
                @click="showUploadModal"
                class="upload-btn"
                block
              >
                上传文件
              </a-button>
            </a-col>
          </a-row>
        </div>

        <!-- 文件列表区域 -->
        <div class="file-list-section">
          <a-table
            :columns="columns"
            :data-source="filteredFiles"
            :pagination="pagination"
            :loading="loading"
            :scroll="{ x: 800 }"
            row-key="id"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'name'">
                <div class="file-name-cell">
                  <component :is="getFileIcon(record.type)" class="file-icon" />
                  <span class="file-name">{{ record.name }}</span>
                </div>
              </template>
              <template v-else-if="column.key === 'size'">
                {{ formatFileSize(record.size) }}
              </template>
              <template v-else-if="column.key === 'parsing_status'">
                <a-tag :color="parsingStatusColor(record.parsing_status)">
                  {{ parsingStatusText(record.parsing_status) }}
                </a-tag>
              </template>
              <template v-else-if="column.key === 'is_enabled'">
                <a-switch
                  :checked="!!record.is_enabled"
                  @change="(val) => handleToggleEnabled(record, val)"
                  checked-children="启用"
                  un-checked-children="禁用"
                />
              </template>
              <template v-else-if="column.key === 'actions'">
                <a-button type="link" size="small" @click="downloadFile(record)">
                  下载
                </a-button>
                <a-button
                  type="link"
                  size="small"
                  :disabled="record.parsing_status === 'processing'"
                  @click="handleParseFile(record)"
                >
                  解析
                </a-button>
                <a-button type="link" size="small" danger @click="deleteFile(record)">
                  删除
                </a-button>
              </template>
            </template>
          </a-table>
        </div>

        <!-- 文件上传模态框 -->
        <a-modal
          v-model:open="uploadModalVisible"
          title="上传文件"
          width="600px"
          :footer="null"
          :mask-closable="false"
        >
          <a-upload-dragger
            v-model:fileList="fileList"
            :multiple="true"
            :before-upload="beforeUpload"
            @remove="handleRemove"
            @drop="handleDrop"
            accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.txt,.md,.html,.htm,.xml,.json,.csv,.rtf,.odt,.ods,.odp,.epub"
            class="upload-dragger"
          >
            <p class="ant-upload-drag-icon">
              <inbox-outlined />
            </p>
            <p class="ant-upload-text">点击或拖拽文件到此处上传</p>
            <p class="ant-upload-hint">支持 PDF / Word / Excel / PPT / TXT / MD / HTML / XML / JSON / CSV / RTF / ODP / EPUB</p>
          </a-upload-dragger>

          <div class="upload-list">
            <div v-for="file in uploadFiles" :key="file.uid" class="upload-item">
              <div class="file-info">
                <div class="file-name">{{ file.name }}</div>
                <div class="file-status">
                  <a-progress
                    v-if="file.status === 'uploading'"
                    :percent="file.percent"
                    size="small"
                    status="active"
                  />
                  <span v-else-if="file.status === 'done'" class="success-text">上传成功</span>
                  <span v-else-if="file.status === 'error'" class="error-text">上传失败</span>
                </div>
              </div>
              <div class="file-actions">
                <a-button
                  v-if="file.status === 'uploading'"
                  size="small"
                  @click="handleCancel(file)"
                >
                  取消
                </a-button>
                <a-button
                  v-else-if="file.status === 'done' || file.status === 'error'"
                  type="link"
                  size="small"
                  @click="handleRemove(file)"
                >
                  删除
                </a-button>
              </div>
            </div>
          </div>

          <div class="upload-actions">
            <a-button
              type="primary"
              :disabled="uploadFiles.length === 0"
              :loading="uploading"
              @click="handleUpload"
            >
              {{ uploading ? '上传中...' : '开始上传' }}
            </a-button>
            <a-button @click="uploadModalVisible = false">取消</a-button>
          </div>
        </a-modal>
      </div>
    </a-layout-content>
  </a-layout>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import {
  InboxOutlined,
  FileTextOutlined,
  FileImageOutlined,
  FileOutlined,
  PlaySquareOutlined
} from '@ant-design/icons-vue'
import { message, Upload, Progress, Modal, Form, Input, Select, DatePicker, Button, Table, Layout, Row, Col, Space, Divider, Tooltip, Popover, Tag, UploadDragger } from 'ant-design-vue'
import { useBackendsStore, useCurrentKBStore } from '../state/appstate'
import { storeToRefs } from 'pinia'
import { useI18n } from 'vue-i18n';
import { loadFilesByKnowledgeBase, deleteFile as deleteFileApi, uploadFilesToKB, parseFile as parseFileApi, toggleFileEnabled as toggleFileEnabledApi } from '../api/service'

export default {
  name: 'FileManager',
 
  setup() {
    const { t } = useI18n();
    console.log(t('message.hello'))
    console.log(t('welcome', { name: 'User' }));
    const backendsStore = useBackendsStore()
    const { endpoints } = storeToRefs(backendsStore)
    const kbStore = useCurrentKBStore()
    console.log(endpoints.value.baseUrl)
    // 搜索表单
    const searchForm = reactive({
      fileName: '',
      uploader: '',
      uploadDate: null,
      fileType: null
    })

    // 上传模态框状态
    const uploadModalVisible = ref(false)
    const uploading = ref(false)
    const fileList = ref([])

    // 文件列表数据
    const files = ref([])
    const loading = ref(false)
    const pagination = reactive({
      current: 1,
      pageSize: 10,
      total: 0,
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条`
    })

    // 获取文件图标
    const getFileIcon = (type) => {
        if (type.includes('image')) return FileImageOutlined;
        if (type.includes('pdf')) return FilePdfOutlined;
        if (type.includes('word')) return FileWordOutlined;
        if (type.includes('excel') || type.includes('sheet')) return FileExcelOutlined;
        if (type.includes('zip') || type.includes('rar')) return FileZipOutlined;
        return FileTextOutlined;
    };
    // 表格列定义
    const columns = ref([
      {
        title: '文件名',
        dataIndex: 'name',
        key: 'name',
        width: '30%'
      },
      {
        title: '大小',
        dataIndex: 'size',
        key: 'size',
        width: '15%'
      },
      {
        title: '类型',
        dataIndex: 'type',
        key: 'type',
        width: '15%'
      },
      {
        title: '上传者',
        dataIndex: 'uploader',
        key: 'uploader',
        width: '15%'
      },
      {
        title: '上传时间',
        dataIndex: 'uploadTime',
        key: 'uploadTime',
        width: '12%'
      },
      {
        title: 'Chunk数',
        dataIndex: 'chunk_count',
        key: 'chunk_count',
        width: '8%'
      },
      {
        title: '解析状态',
        dataIndex: 'parsing_status',
        key: 'parsing_status',
        width: '10%'
      },
      {
        title: '是否启用',
        key: 'is_enabled',
        width: '10%'
      },
      {
        title: '操作',
        key: 'actions',
        width: '15%',
        fixed: 'right'
      }
    ])

    // 文件图标组件
    const FileIcon = {
      props: ['type'],
      setup(props) {
        return () => {
          const { type } = props;
          if (type === 'doc') return h('span', { style: { color: '#1890ff' } }, [h('file-text-outlined')]);
          if (type === 'image') return h('span', { style: { color: '#52c41a' } }, [h('file-image-outlined')]);
          if (type === 'video') return h('span', { style: { color: '#faad14' } }, [h('play-square-outlined')]);
          if (type === 'audio') return h('span', { style: { color: '#722ed1' } }, [h('music-outlined')]);
          return h('span', { style: { color: '#bfbfbf' } }, [h('file-outlined')]);
        };
      }
    };
    // 计算属性 - 过滤后的文件列表
    const filteredFiles = computed(() => {
      let result = files.value

      if (searchForm.fileName) {
        result = result.filter(file => file.name.includes(searchForm.fileName))
      }

      if (searchForm.uploader) {
        result = result.filter(file => file.uploader.includes(searchForm.uploader))
      }

      if (searchForm.fileType) {
        result = result.filter(file => file.type === searchForm.fileType)
      }

      if (searchForm.uploadDate) {
        const date = searchForm.uploadDate.format('YYYY-MM-DD')
        result = result.filter(file => file.uploadTime.includes(date))
      }

      pagination.total = result.length
      return result.slice(
        (pagination.current - 1) * pagination.pageSize,
        pagination.current * pagination.pageSize
      )
    })

    // 计算属性 - 上传文件列表
    const uploadFiles = computed(() => fileList.value)

    // 显示上传模态框
    const showUploadModal = () => {
      uploadModalVisible.value = true
      fileList.value = []
    }

    // Tika 支持的文件类型
    const SUPPORTED_EXTENSIONS = new Set([
      'pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx',
      'txt', 'md', 'html', 'htm', 'xml', 'json', 'csv',
      'rtf', 'odt', 'ods', 'odp', 'epub'
    ])

    // 上传前处理
    const beforeUpload = file => {
      const ext = file.name.split('.').pop().toLowerCase()
      if (!SUPPORTED_EXTENSIONS.has(ext)) {
        message.error(`不支持的文件类型：${file.name}，仅支持 PDF/Word/Excel/PPT/TXT/MD/HTML/XML/JSON/CSV/RTF/EPUB`)
        return false
      }
      fileList.value = [...fileList.value, file]
      return false
    }

    // 处理文件删除
    const handleRemove = file => {
      const index = fileList.value.indexOf(file)
      if (index > -1) {
        fileList.value.splice(index, 1)
      }
    }

    // 处理拖拽事件
    const handleDrop = e => {
      console.log('Dropped files', e.dataTransfer.files)
    }

    // 处理取消上传
    const handleCancel = file => {
      // 在实际应用中，这里应该取消上传请求
      handleRemove(file)
      message.info('取消上传: ' + file.name)
    }

    // 处理上传
    const handleUpload = async () => {
      const kbId = kbStore.currentKB?.id
      if (!kbId) {
        message.warning('未选择知识库')
        return
      }
      uploading.value = true
      try {
        await uploadFilesToKB(kbId, fileList.value)
        message.success('文件上传成功')
        fileList.value = []
        uploadModalVisible.value = false
        loadFiles()
      } catch (e) {
        message.error('上传失败')
      } finally {
        uploading.value = false
      }
    }

    // 获取文件类型
    const getFileType = fileName => {
      const ext = fileName.split('.').pop().toLowerCase()
      if (['doc', 'docx', 'pdf', 'txt', 'xls', 'xlsx'].includes(ext)) return 'doc'
      if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(ext)) return 'image'
      if (['mp4', 'avi', 'mov', 'wmv', 'flv'].includes(ext)) return 'video'
      if (['mp3', 'wav', 'ogg', 'flac'].includes(ext)) return 'audio'
      return 'other'
    }

    // 格式化文件大小
    const formatFileSize = bytes => {
      if (bytes === 0) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    }

    // 下载文件
    const downloadFile = file => {
      const url = endpoints.value.baseUrl + file.storage_path
      window.open(url, '_blank')
    }

    // 解析状态文本
    const parsingStatusText = (status) => {
      const map = { pending: '待解析', processing: '解析中', done: '已完成', error: '解析失败' }
      return map[status] || status
    }

    // 解析状态颜色
    const parsingStatusColor = (status) => {
      const map = { pending: 'default', processing: 'processing', done: 'success', error: 'error' }
      return map[status] || 'default'
    }

    // 触发解析
    const handleParseFile = async (file) => {
      try {
        await parseFileApi({ id: file.id })
        message.success('解析任务已提交')
        file.parsing_status = 'processing'
      } catch (e) {
        message.error('提交解析失败')
      }
    }

    // 切换启用状态
    const handleToggleEnabled = async (file, val) => {
      try {
        await toggleFileEnabledApi({ id: file.id, is_enabled: val ? 1 : 0 })
        file.is_enabled = val ? 1 : 0
        message.success(val ? '已启用' : '已禁用')
      } catch (e) {
        message.error('状态更新失败')
      }
    }

    // 删除文件
    const deleteFile = async file => {
      try {
        await deleteFileApi({ id: file.id })
        message.success('删除成功')
        loadFiles()
      } catch (e) {
        message.error('删除失败')
      }
    }

    // 加载文件列表
    const loadFiles = async () => {
      const kbId = kbStore.currentKB?.id
      if (!kbId) return
      loading.value = true
      try {
        const resdata = await loadFilesByKnowledgeBase({
          knowledge_base_id: kbId,
          page: pagination.current,
          pageSize: pagination.pageSize,
          original_name: searchForm.fileName || undefined,
        })
        if (resdata && resdata.data) {
          files.value = (resdata.data.page || []).map(item => ({
            id: item.id,
            name: item.original_name || item.name,
            size: item.size,
            type: getFileType(item.original_name || item.name || ''),
            uploader: item.uploader_name || item.uploaded_by || '',
            uploadTime: item.uploaded_at || '',
            storage_path: item.storage_path,
            mime_type: item.mime_type,
            chunk_count: item.chunk_count || 0,
            parsing_status: item.parsing_status || 'pending',
            is_enabled: item.is_enabled !== undefined ? item.is_enabled : 1,
          }))
          pagination.total = Number(resdata.data.total) || 0
        }
      } catch (e) {
        console.error(e)
      } finally {
        loading.value = false
      }
    }

    // 初始化加载文件列表
    onMounted(() => {
      loadFiles()
    })

    return {
      searchForm,
      uploadModalVisible,
      uploading,
      fileList,
      files,
      loading,
      pagination,
      columns,
      filteredFiles,
      uploadFiles,
      showUploadModal,
      beforeUpload,
      handleRemove,
      handleDrop,
      handleCancel,
      handleUpload,
      getFileType,
      formatFileSize,
      downloadFile,
      deleteFile,
      loadFiles,
      FileIcon,
      getFileIcon,
      parsingStatusText,
      parsingStatusColor,
      handleParseFile,
      handleToggleEnabled
    }
  }
}
</script>

<style scoped>
.file-manager {
  min-height: 100vh;
  padding: 20px;
  background-color: #f5f5f5;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-upload-section {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.upload-btn-col {
  display: flex;
  justify-content: flex-end;
}

.upload-btn {
  height: 40px;
  font-weight: 500;
}

.file-list-section {
  margin-top: 16px;
}

.file-name-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.file-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.upload-dragger {
  margin-bottom: 16px;
}

.upload-list {
  max-height: 300px;
  overflow-y: auto;
  margin-bottom: 16px;
}

.upload-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  margin-bottom: 8px;
  background: #fafafa;
}

.file-info {
  flex: 1;
  min-width: 0;
}

.file-name {
  font-weight: 500;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-status {
  width: 100%;
}

.success-text {
  color: #52c41a;
}

.error-text {
  color: #ff4d4f;
}

.upload-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    padding: 16px;
  }
  
  .upload-btn-col {
    justify-content: center;
    margin-top: 16px;
  }
  
  .upload-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .file-actions {
    align-self: flex-end;
  }
}

@media (max-width: 576px) {
  .file-manager {
    padding: 10px;
  }
  
  .container {
    padding: 12px;
  }
  
  .upload-dragger {
    padding: 16px 8px;
  }
  
  .ant-upload-text {
    font-size: 14px;
  }
  
  .ant-upload-hint {
    font-size: 12px;
  }
}
</style>