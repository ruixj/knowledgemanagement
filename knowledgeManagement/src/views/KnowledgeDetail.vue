<template>
    <div class="knowledge-base-container">
 
        <!-- 主要内容区域 -->
        <div class="main-content">
            <!-- 左侧面板 -->
            <div class="left-panel">
                <div class="knowledge-title">
                    <h2>产品知识库</h2>
                </div>
                <div class="button-group">
                    <a-button type="primary" class="custom-btn" @click="gotoKnowledgeManager('fileManager' )">
                        <template #icon><AIcon type="database" /></template>
                        数据集
                    </a-button>
                    <a-button class="custom-btn" @click="gotoKnowledgeManager('configuration' )">
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
                <router-view />
            </div>
        </div>
        

    </div>
</template>

<script setup>
import router from '../router';
import FileManager from './FileManager.vue';

function gotoKnowledgeManager(routerName) {  
    router.push({name:routerName}) 
}
 
             
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