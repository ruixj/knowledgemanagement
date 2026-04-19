<template>
  <div class="container">
    <div class="header">
        <h1>文件管理系统</h1>
    </div>
    
    <div class="content">
        <div class="stats">
            <div class="stat-card">
                <div class="stat-number">{{ files.length }}</div>
                <div class="stat-label">总文件数</div>
            </div>
 
            <div class="stat-card">
                <div class="stat-number">86%</div>
                <div class="stat-label">剩余空间</div>
            </div>
        </div>
        
        <a-alert message="提示" description="仅支持上传不超过 50MB 的文件，支持常用文档、图片、视频格式。" type="info" show-icon style="margin-bottom: 24px;" />
        
        <div class="upload-section">
            <a-upload-dragger
                :multiple="true"
                :file-list="fileList"
                :before-upload="beforeUpload"
                @remove="handleRemove"
                :custom-request="dummyRequest"
            >
                <p class="ant-upload-drag-icon">
                    <inbox-outlined></inbox-outlined>
                </p>
                <p class="ant-upload-text">点击或拖拽文件到此处上传</p>
                <p class="ant-upload-hint">
                    支持单个或批量上传，严禁上传公司数据或其他敏感文件
                </p>
            </a-upload-dragger>
        </div>
        
        <div class="filter-section">
            <div class="filter-title">
                <filter-outlined />
                <span>数据筛选条件</span>
            </div>
        
            <div class="filter-grid">
                <div class="filter-item">
                    <div class="filter-label">姓名筛选（模糊匹配）</div>
                    <a-input 
                        v-model:value="filters.name" 
                        placeholder="输入姓名关键词" 
                        allow-clear
                    >
                        <template #prefix>
                            <user-outlined />
                        </template>
                    </a-input>
                </div>                      
            </div>
        
            <div class="filter-actions">
                <a-button @click="resetFilters">
                    <template #icon><redo-outlined /></template>
                    重置所有筛选
                </a-button>
            </div>
        </div>
                
        <div class="table-section">
            <a-table :dataSource="files" :columns="columns" :pagination="{ pageSize: 5 }" rowKey="id">
                <template #bodyCell="{ column, record }">
                    <template v-if="column.key === 'name'">
                        <div class="file-name">
                            <component :is="getFileIcon(record.type)" class="file-icon" />
                            <div>
                                <div>{{ record.name }}</div>
                                <div class="file-size">{{ formatFileSize(record.size) }}</div>
                            </div>
                        </div>
                    </template>
                    <template v-else-if="column.key === 'status'">
                        <a-tag :color="getStatusColor(record.status)">
                            {{ getStatusText(record.status) }}
                        </a-tag>
                    </template>
                    <template v-else-if="column.key === 'action'">
                        <a-button type="link" danger @click="showDeleteConfirm(record)" :disabled="record.status === 'uploading'">
                        <template #icon><delete-outlined/></template>
                            删除
                        </a-button>
                    </template>
                </template>
            </a-table>
        </div>
    </div>
  </div>
    
</template>

<script>
import { ref ,computed} from 'vue';
import { 
    FileTextOutlined, 
    FileImageOutlined,
    FilePdfOutlined, 
    FileWordOutlined, 
    FileExcelOutlined, 
    FileZipOutlined, 
    DeleteOutlined,
    FilterOutlined,
    UserOutlined,

} from '@ant-design/icons-vue';
import { Table,Alert,Tag,Button,Modal,message,} from 'ant-design-vue';

export default {
  name: 'FileList',
  setup() {
    // 文件数据
    const files = ref([
        {
            id: '1',
            name: '项目报告.pdf',
            size: 2048576,
            type: 'application/pdf',
            uploadTime: '2023-10-15 14:30:25',
            status: 'done'
        },
        {
            id: '2',
            name: '产品设计图.jpg',
            size: 3451220,
            type: 'image/jpeg',
            uploadTime: '2023-10-16 09:12:47',
            status: 'done'
        },
        {
            id: '3',
            name: '财务数据.xlsx',
            size: 1126400,
            type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
            uploadTime: '2023-10-17 16:45:33',
            status: 'done'
        },
        {
            id: '4',
            name: '会议记录.docx',
            size: 512000,
            type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
            uploadTime: '2023-10-18 11:20:15',
            status: 'done'
        },
        {
            id: '5',
            name: '项目源码.zip',
            size: 15623424,
            type: 'application/zip',
            uploadTime: '2023-10-19 15:40:22',
            status: 'done'
        }
    ]);
    
    const fileList = ref([]);
    
    // 表格列定义
    const columns = ref([
        {
            title: '文件名',
            dataIndex: 'name',
            key: 'name',
        },
        {
            title: '类型',
            dataIndex: 'type',
            key: 'type',
            width: '20%',
        },
        {
            title: '上传时间',
            dataIndex: 'uploadTime',
            key: 'uploadTime',
            width: '20%',
        },
        {
            title: '状态',
            key: 'status',
            width: '12%',
        },
        {
            title: '操作',
            key: 'action',
            width: '10%',
        }
    ]);

        // 筛选条件
    const filters = ref({
        name: '',
        age: '',
        gender: '',
        email: ''
    });

    // 重置所有筛选条件
    const resetFilters = () => {
        filters.value = {
            name: '',
        };
        message.success('筛选条件已重置');
    };
            

    const totalSize = computed(() => {
        const totalBytes = files.value.reduce((sum, file) => sum + file.size, 0);
        return (totalBytes / (1024 * 1024)).toFixed(1);
    });
    
    // 格式化文件大小
    const formatFileSize = (bytes) => {
        if (bytes === 0) return '0 B';
        const k = 1024;
        const sizes = ['B', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    };
    
    // 获取文件图标
    const getFileIcon = (type) => {
        if (type.includes('image')) return FileImageOutlined;
        if (type.includes('pdf')) return FilePdfOutlined;
        if (type.includes('word')) return FileWordOutlined;
        if (type.includes('excel') || type.includes('sheet')) return FileExcelOutlined;
        if (type.includes('zip') || type.includes('rar')) return FileZipOutlined;
        return FileTextOutlined;
    };
    
    // 获取状态颜色
    const getStatusColor = (status) => {
        const statusColors = {
            done: 'green',
            uploading: 'blue',
            error: 'red'
        };
        return statusColors[status] || 'default';
    };
    
    // 获取状态文本
    const getStatusText = (status) => {
        const statusTexts = {
            done: '已完成',
            uploading: '上传中',
            error: '错误'
        };
        return statusTexts[status] || '未知';
    };
    
    // 上传前的验证
    const beforeUpload = (file) => {
        const isLt50M = file.size / 1024 / 1024 < 50;
        if (!isLt50M) {
            message.error('文件大小不能超过 50MB!');
            return false;
        }
        return false;
    };
    
    // 模拟上传请求
    const dummyRequest = (options) => {
        const { file, onProgress, onSuccess, onError } = options;
        
        // 生成模拟上传进度
        let progress = 0;
        const interval = setInterval(() => {
            progress += Math.random() * 15;
            if (progress > 100) progress = 100;
            
            onProgress({ percent: progress });
            
            if (progress === 100) {
                clearInterval(interval);
                // 添加到文件列表
                const newFile = {
                    id: Date.now().toString(),
                    name: file.name,
                    size: file.size,
                    type: file.type,
                    uploadTime: new Date().toLocaleString(),
                    status: 'done'
                };
                files.value.push(newFile);
                onSuccess("OK");
            }
        }, 300);
    };
    
    // 处理文件删除
    const handleRemove = (file) => {
        files.value = files.value.filter(f => f.uid !== file.uid);
    };
    
    // 显示删除确认对话框
    const showDeleteConfirm = (file) => {
        Modal.confirm({
            title: '确认删除',
            content: `您确定要删除文件 "${file.name}" 吗？此操作不可恢复。`,
            okText: '确认',
            okType: 'danger',
            cancelText: '取消',
            onOk() {
                files.value = files.value.filter(f => f.id !== file.id);
                message.success(`文件 "${file.name}" 已删除`);
            }
        });
    };
    
    return {
        files,
        fileList,
        filters,
        resetFilters,
        columns,
        totalSize,
        formatFileSize,
        getFileIcon,
        getStatusColor,
        getStatusText,
        beforeUpload,
        dummyRequest,
        handleRemove,
        showDeleteConfirm
    };
  },
};
</script>

<style scoped>
    @media (max-width: 768px) {
        .stats {
            flex-direction: column;
        }
        .stat-card {
            min-width: 100%;
        }
        .filter-grid {
            grid-template-columns: 1fr;
        }
    }
  .container {
      max-width: 1200px;
      margin: 0 auto;
      background: white;
      border-radius: 12px;
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
      overflow: hidden;
  }
  .header {
      background: linear-gradient(90deg, #1890ff 0%, #096dd9 100%);
      color: white;
      padding: 24px;
      text-align: center;
  }
  .header h1 {
      font-size: 28px;
      margin-bottom: 8px;
  }
  .header p {
      opacity: 0.9;
  }
  .content {
      padding: 24px;
  }
  .stats {
      display: flex;
      gap: 16px;
      margin-bottom: 24px;
      flex-wrap: wrap;
  }
  .stat-card {
      flex: 1;
      min-width: 200px;
      padding: 16px;
      background: #f9f9f9;
      border-radius: 8px;
      text-align: center;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }
  .stat-number {
      font-size: 28px;
      font-weight: bold;
      color: #1890ff;
      margin-bottom: 4px;
  }
  .stat-label {
      color: #666;
      font-size: 14px;
  }
  .upload-section {
      margin-bottom: 32px;
      padding: 24px;
      background: #fafafa;
      border-radius: 8px;
      border: 1px dashed #d9d9d9;
      transition: all 0.3s;
  }
  .upload-section:hover {
      border-color: #1890ff;
      background: #f0f8ff;
  }

   .filter-section {
        background: #fafafa;
        border-radius: 8px;
        padding: 20px;
        margin-bottom: 24px;
        border: 1px solid #e8e8e8;
    }
    .filter-title {
        font-size: 16px;
        font-weight: 600;
        margin-bottom: 16px;
        color: #262626;
        display: flex;
        align-items: center;
    }
    .filter-title svg {
        margin-right: 8px;
    }
    .filter-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
        gap: 16px;
    }
    .filter-item {
        display: flex;
        flex-direction: column;
    }
    .filter-label {
        margin-bottom: 6px;
        font-weight: 500;
        color: #595959;
    }
    .filter-actions {
        display: flex;
        justify-content: flex-start;
        gap: 12px;
        margin-top: 16px;
    }

    .table-section {
        margin-top: 24px;
    }
  .file-icon {
      margin-right: 8px;
      color: #1890ff;
  }
  .file-name {
      display: flex;
      align-items: center;
  }
  .file-size {
      color: #666;
      font-size: 12px;
      margin-top: 4px;
  }
  .action-delete {
      color: #ff4d4f;
      cursor: pointer;
  }
  .action-delete:hover {
      color: #d9363e;
  }
 
  @media (max-width: 768px) {
      .stats {
          flex-direction: column;
      }
      .stat-card {
          min-width: 100%;
      }
  }
</style>