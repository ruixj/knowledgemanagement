<template>
    <div class="knowledge-base-container">
        <!-- 线索行 -->
        <div class="clue-row">
            <a-breadcrumb>
                <a-breadcrumb-item>首页</a-breadcrumb-item>
                <a-breadcrumb-item>知识库管理</a-breadcrumb-item>
                <a-breadcrumb-item>我的知识库</a-breadcrumb-item>
            </a-breadcrumb>
        </div>
        
        <!-- 主要内容区域 -->
        <div class="main-content">
            <!-- 左侧面板 -->
            <div class="left-panel">
                <div class="knowledge-title">
                    <h2>产品知识库</h2>
                </div>
                <div class="button-group">
                    <a-button type="primary" class="custom-btn">
                        <template #icon><AIcon type="database" /></template>
                        数据集
                    </a-button>
                    <a-button class="custom-btn">
                        <template #icon><AIcon type="setting" /></template>
                        配置
                    </a-button>
                    <a-button class="custom-btn">
                        <template #icon><AIcon type="search" /></template>
                        搜索测试
                    </a-button>
                </div>
            </div>
            
            <!-- 右侧面板 -->
            <div class="right-panel">
                <div class="file-header">
                    <div class="hint-text">共 {{fileList.length}} 个文件</div>
                    <div class="search-area">
                        <a-input-search 
                            placeholder="搜索文件名" 
                            style="width: 200px" 
                            @search="onSearch" 
                        />
                        <a-button type="primary" class="upload-btn" @click="showUploadModal">
                            <template #icon><AIcon type="upload" /></template>
                            上传文件
                        </a-button>
                    </div>
                </div>
                
                <a-table 
                    :columns="columns" 
                    :data-source="fileList"
                    :pagination="{pageSize: 10, showSizeChanger: true}"
                    row-key="id"
                />
            </div>
        </div>
        
        <!-- 上传文件模态框 -->
        <a-modal
            title="上传文件"
            :visible="uploadVisible"
            @ok="handleUploadOk"
            @cancel="handleUploadCancel"
            :confirm-loading="uploading"
        >
            <a-upload-dragger
                name="file"
                :multiple="true"
                action="https://www.mocky.io/v2/5cc8019d300000980a055e76"
                @change="handleUploadChange"
            >
                <p class="ant-upload-drag-icon">
                    <AIcon type="inbox" />
                </p>
                <p class="ant-upload-text">点击或拖拽文件到此处上传</p>
                <p class="ant-upload-hint">支持单个或批量上传，严禁上传公司数据或其他敏感文件</p>
            </a-upload-dragger>
        </a-modal>
    </div>
</template>

<script setup>
import { ref ,h} from 'vue';
import { message,Button,Popconfirm } from 'ant-design-vue';

// 响应式数据
const fileList = ref([
    {
        id: 1,
        name: '产品需求文档.pdf',
        type: 'PDF',
        size: '2.3 MB',
        uploadTime: '2023-04-15 10:30'
    },
    {
        id: 2,
        name: '用户手册.docx',
        type: '文档',
        size: '1.5 MB',
        uploadTime: '2023-04-16 14:20'
    },
    {
        id: 3,
        name: '设计规范.zip',
        type: '压缩包',
        size: '5.7 MB',
        uploadTime: '2023-04-17 09:45'
    },
    {
        id: 4,
        name: '培训材料.pptx',
        type: '演示文稿',
        size: '3.2 MB',
        uploadTime: '2023-04-18 16:10'
    },
    {
        id: 5,
        name: '技术白皮书.pdf',
        type: 'PDF',
        size: '4.1 MB',
        uploadTime: '2023-04-19 11:25'
    }
]);

const uploadVisible = ref(false);
const uploading = ref(false);
const searchValue = ref('');

// 表格列配置
const columns = [
    {
        title: '文件名',
        dataIndex: 'name',
        key: 'name',
        width: '30%',
    },
    {
        title: '类型',
        dataIndex: 'type',
        key: 'type',
        width: '15%',
    },
    {
        title: '大小',
        dataIndex: 'size',
        key: 'size',
        width: '15%',
    },
    {
        title: '上传时间',
        dataIndex: 'uploadTime',
        key: 'uploadTime',
        width: '20%',
    },
    {
        title: '操作',
        key: 'action',
        width: '20%',
    },
];

// 方法
const onSearch = (value) => {
    searchValue.value = value;
    message.info(`搜索: ${value}`);
};

const showUploadModal = () => {
    uploadVisible.value = true;
};

const handleUploadOk = () => {
    uploading.value = true;
    setTimeout(() => {
        uploadVisible.value = false;
        uploading.value = false;
        message.success('文件上传成功！');
        
        // 模拟添加新文件
        const newFile = {
            id: fileList.value.length + 1,
            name: `新文件_${Date.now()}.pdf`,
            type: 'PDF',
            size: '1.2 MB',
            uploadTime: new Date().toLocaleDateString()
        };
        fileList.value.push(newFile);
    }, 1500);
};

const handleUploadCancel = () => {
    uploadVisible.value = false;
};

const handleUploadChange = (info) => {
    if (info.file.status === 'done') {
        message.success(`${info.file.name} 文件上传成功。`);
    } else if (info.file.status === 'error') {
        message.error(`${info.file.name} 文件上传失败。`);
    }
};

const previewFile = (record) => {
    message.info(`预览文件: ${record.name}`);
};

const downloadFile = (record) => {
    message.success(`开始下载: ${record.name}`);
};

const deleteFile = (id) => {
    fileList.value = fileList.value.filter(file => file.id !== id);
    message.success('文件删除成功');
};

// 自定义渲染操作列
const renderAction = (record) => {
    return h('span', [
        h(Button, {
            type: 'link',
            size: 'small',
            onClick: () => previewFile(record)
        }, '预览'),
        h(Button, {
            type: 'link',
            size: 'small',
            onClick: () => downloadFile(record)
        }, '下载'),
        h(Popconfirm, {
            title: '确定要删除这个文件吗？',
            okText: '确定',
            cancelText: '取消',
            onConfirm: () => deleteFile(record.id)
        }, {
            default: () => h(Button, {
                type: 'link',
                size: 'small',
                style: { color: '#ff4d4f' }
            }, '删除')
        })
    ]);
};

// 处理表格列
const processedColumns = columns.map(col => {
    if (col.key === 'action') {
        return {
            ...col,
            customRender: ({ record }) => renderAction(record)
        };
    }
    return col;
});

 
             
</script>

<style scoped>
        .knowledge-base-container {
            max-width: 1400px;
            margin: 0 auto;
            background: white;
            border-radius: 6px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }
        .clue-row {
            padding: 16px 24px;
            border-bottom: 1px solid #f0f0f0;
            background-color: #fafafa;
        }
        .main-content {
            display: flex;
            min-height: 600px;
        }
        .left-panel {
            width: 280px;
            border-right: 1px solid #f0f0f0;
            display: flex;
            flex-direction: column;
        }
        .knowledge-title {
            padding: 24px;
            border-bottom: 1px solid #f0f0f0;
            background: white;
        }
        .knowledge-title h2 {
            margin: 0;
            font-size: 20px;
            font-weight: 600;
        }
        .button-group {
            padding: 24px;
            flex: 1;
            display: flex;
            flex-direction: column;
            gap: 16px;
            background: white;
        }
        .right-panel {
            flex: 1;
            padding: 24px;
            background: white;
        }
        .file-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 16px;
        }
        .search-area {
            display: flex;
            gap: 8px;
        }
        .ant-table-wrapper {
            margin-top: 16px;
        }
        .hint-text {
            color: rgba(0, 0, 0, 0.45);
            font-size: 14px;
        }
        .upload-btn {
            margin-left: 8px;
        }
        .custom-btn {
            height: 40px;
            font-size: 15px;
            display: flex;
            align-items: center;
            justify-content: flex-start;
            padding: 0 16px;
        }
        .custom-btn .anticon {
            margin-right: 8px;
        }
        @media (max-width: 768px) {
            .main-content {
                flex-direction: column;
            }
            .left-panel {
                width: 100%;
                border-right: none;
                border-bottom: 1px solid #f0f0f0;
            }
            .file-header {
                flex-direction: column;
                align-items: flex-start;
                gap: 12px;
            }
            .search-area {
                width: 100%;
                justify-content: space-between;
            }
        }
</style>