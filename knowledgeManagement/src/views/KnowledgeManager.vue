<template>
  <div >
    <div class="knowledge-container">
      <!-- 顶部区域 -->
      <div class="header-section">
        <a-row :gutter="[16, 16]">
          <a-col :xs="24" :sm="12" :md="12" :lg="12">
            <h1 class="page-title">
              <book-outlined />
              知识库管理
            </h1>
          </a-col>
          <a-col :xs="24" :sm="12" :md="12" :lg="12">
            <div class="search-create-container">
              <a-input-search
                v-model:value="searchKeyword"
                placeholder="搜索知识库..."
                style="width: 200px"
                @search="handleSearch"
                allow-clear
              />
              <a-button type="primary" @click="showCreateModal">
                <template #icon>
                  <plus-outlined />
                </template>
                新建知识库
              </a-button>
            </div>
          </a-col>
        </a-row>
      </div>
      
      <!-- 内容区域 -->
      <div class="content-section">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <a-spin size="large" />
          <div style="margin-top: 16px;">加载中...</div>
        </div>
        
        <!-- 空状态 -->
        <div v-else-if="knowledgeList.length === 0" class="empty-state">
          <folder-open-outlined />
          <div>暂无知识库，点击上方按钮创建第一个知识库</div>
        </div>
        
        <!-- 知识库列表 -->
        <div v-else class="knowledge-list">
          <div 
            v-for="item in knowledgeList" 
            :key="item.id" 
            class="knowledge-item"
            @click="viewKnowledge(item)"
          >
            <div class="knowledge-title">
              <folder-outlined />
              {{ item.title }}
            </div>
            <div class="knowledge-desc">{{ item.description }}</div>
            <div class="knowledge-meta">
              <span>
                <user-outlined />
                创建者: {{ item.creator }}
              </span>
              <span>
                <clock-circle-outlined />
                更新时间: {{ item.updateTime }}
              </span>
              <span>
                <file-text-outlined />
                文档数: {{ item.docCount }}
              </span>
            </div>
          </div>
        </div>
        
        <!-- 分页 -->
        <div v-if="knowledgeList.length > 0" class="pagination-container">
          <a-pagination
            v-model:current="currentPage"
            :total="total"
            :page-size="pageSize"
            show-less-items
            show-size-changer
            :page-size-options="['10', '20', '50', '100']"
            @change="handlePageChange"
            @showSizeChange="handleSizeChange"
          />
        </div>
      </div>
    </div>
    
    <!-- 创建知识库模态框 -->
    <a-modal
      v-model:visible="createModalVisible"
      title="新建知识库"
      :confirm-loading="creating"
      @ok="handleCreateOk"
      @cancel="handleCreateCancel"
      :width="600"
    >
      <a-form
        :model="createForm"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="知识库名称" required>
          <a-input v-model:value="createForm.title" placeholder="请输入知识库名称" />
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea 
            v-model:value="createForm.description" 
            :rows="4" 
            placeholder="请输入知识库描述"
            show-count 
            :maxlength="200"
          />
        </a-form-item>
        <a-form-item label="可见性">
          <a-radio-group v-model:value="createForm.visibility">
            <a-radio value="public">
              <template #radio="{ checked }">
                <a-badge :color="checked ? '#52c41a' : '#d9d9d9'" text="公开" />
              </template>
            </a-radio>
            <a-radio value="private">
              <template #radio="{ checked }">
                <a-badge :color="checked ? '#faad14' : '#d9d9d9'" text="私有" />
              </template>
            </a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
  
  </div>
</template>
<script  >
import { ref, reactive, computed, onMounted,watch  } from 'vue'


export default {
  name: 'KnowledgeManager',
  setup(){
    // 响应式数据
    const knowledgeList = ref([]);
    const currentPage = ref(1);
    const total = ref(50);
    const pageSize = ref(10);
    const createModalVisible = ref(false);
    const creating = ref(false);
    const loading = ref(false);
    const searchKeyword = ref('');

    // 创建表单数据
    const createForm = reactive({
        title: '',
        description: '',
        visibility: 'public'
    });

    // 计算属性：过滤后的知识库列表
    const filteredKnowledgeList = computed(() => {
        if (!searchKeyword.value) {
        return knowledgeList.value;
        }
        
        const keyword = searchKeyword.value.toLowerCase();
        return knowledgeList.value.filter(item => 
        item.title.toLowerCase().includes(keyword) || 
        item.description.toLowerCase().includes(keyword)
        );
    });

    // 方法
    const loadKnowledgeList = async () => {
        loading.value = true;
        
        // 模拟API调用延迟
        await new Promise(resolve => setTimeout(resolve, 800));
        
        const startIndex = (currentPage.value - 1) * pageSize.value;
        const mockData = [];
        
        for (let i = 1; i <= pageSize.value; i++) {
        mockData.push({
            id: startIndex + i,
            title: `知识库 ${startIndex + i}`,
            description: `这是知识库 ${startIndex + i} 的描述信息，包含各种相关文档和资料。`,
            creator: `用户${(startIndex + i) % 5 + 1}`,
            updateTime: `2023-${String((startIndex + i) % 12 + 1).padStart(2, '0')}-${String((startIndex + i) % 28 + 1).padStart(2, '0')}`,
            docCount: Math.floor(Math.random() * 100) + 10
        });
        }
        
        knowledgeList.value = mockData;
        loading.value = false;
    };

    const handleSearch = (value) => {
        searchKeyword.value = value;
        currentPage.value = 1;
        loadKnowledgeList();
    };

    const showCreateModal = () => {
        createModalVisible.value = true;
    };

    const handleCreateOk = async () => {
        if (!createForm.title.trim()) {
        // 实际应用中应该使用antd的message组件提示
        alert('请输入知识库名称');
        return;
        }
        
        creating.value = true;
        
        // 模拟API调用延迟
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        console.log('创建知识库:', createForm);
        
        // 实际应用中这里应该调用API创建知识库
        creating.value = false;
        createModalVisible.value = false;
        
        // 重置表单
        Object.assign(createForm, {
        title: '',
        description: '',
        visibility: 'public'
        });
        
        // 刷新列表
        loadKnowledgeList();
    };

    const handleCreateCancel = () => {
        createModalVisible.value = false;
        
        // 重置表单
        Object.assign(createForm, {
        title: '',
        description: '',
        visibility: 'public'
        });
    };

    const viewKnowledge = (item) => {
        console.log('查看知识库:', item);
        // 实际应用中这里应该跳转到知识库详情页面
        alert(`查看知识库: ${item.title}`);
    };

    const handlePageChange = (page) => {
        currentPage.value = page;
        loadKnowledgeList();
    };

    const handleSizeChange = (current, size) => {
        pageSize.value = size;
        currentPage.value = 1;
        loadKnowledgeList();
    };

    // 生命周期
    onMounted(() => {
        loadKnowledgeList();
    });

    // 监听搜索关键词变化
    watch(searchKeyword, (newVal, oldVal) => {
        if (newVal !== oldVal) {
        currentPage.value = 1;
        loadKnowledgeList();
        }
    });

    // 返回模板中需要使用的数据和方法
    return {
        knowledgeList: filteredKnowledgeList,
        currentPage,
        total,
        pageSize,
        createModalVisible,
        creating,
        loading,
        searchKeyword,
        createForm,
        handleSearch,
        showCreateModal,
        handleCreateOk,
        handleCreateCancel,
        viewKnowledge,
        handlePageChange,
        handleSizeChange
    };
  }
}
</script>

<style scoped>
    .knowledge-container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 20px;
      min-height: 100vh;
    }
    
    .header-section {
      background: white;
      padding: 24px;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      margin-bottom: 24px;
    }
    
    .content-section {
      background: white;
      padding: 24px;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
    
    .page-title {
      font-size: 20px;
      font-weight: 600;
      color: #262626;
      margin: 0;
      display: flex;
      align-items: center;
    }
    
    .page-title i {
      margin-right: 8px;
      color: #1890ff;
    }
    
    .search-create-container {
      display: flex;
      justify-content: flex-end;
      gap: 16px;
    }
    
    .knowledge-list {
      margin-top: 24px;
    }
    
    .knowledge-item {
      padding: 16px;
      border-bottom: 1px solid #f0f0f0;
      cursor: pointer;
      transition: all 0.3s;
      border-radius: 4px;
      display: flex;
      flex-direction: column;
    }
    
    .knowledge-item:hover {
      background-color: #f0f8ff;
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
    
    .knowledge-item:last-child {
      border-bottom: none;
    }
    
    .knowledge-title {
      font-size: 16px;
      font-weight: 500;
      margin-bottom: 8px;
      color: #262626;
      display: flex;
      align-items: center;
    }
    
    .knowledge-title i {
      margin-right: 8px;
      color: #52c41a;
    }
    
    .knowledge-desc {
      color: #8c8c8c;
      font-size: 14px;
      margin-bottom: 8px;
      line-height: 1.5;
    }
    
    .knowledge-meta {
      display: flex;
      gap: 16px;
      color: #8c8c8c;
      font-size: 12px;
    }
    
    .knowledge-meta span {
      display: flex;
      align-items: center;
    }
    
    .knowledge-meta i {
      margin-right: 4px;
    }
    
    .pagination-container {
      display: flex;
      justify-content: center;
      margin-top: 24px;
    }
    
    .empty-state {
      text-align: center;
      padding: 40px 0;
      color: #8c8c8c;
    }
    
    .empty-state i {
      font-size: 48px;
      margin-bottom: 16px;
      color: #d9d9d9;
    }
    
    .loading-state {
      text-align: center;
      padding: 40px 0;
    }
    
    /* 响应式样式 */
    @media (max-width: 768px) {
      .knowledge-container {
        padding: 16px;
      }
      
      .header-section, .content-section {
        padding: 16px;
      }
      
      .search-create-container {
        flex-direction: column;
        margin-top: 16px;
      }
      
      .page-title {
        justify-content: center;
        margin-bottom: 16px;
      }
      
      .knowledge-meta {
        flex-direction: column;
        gap: 4px;
      }
    }
    
    @media (max-width: 576px) {
      .knowledge-container {
        padding: 12px;
      }
      
      .header-section, .content-section {
        padding: 12px;
      }
    }
</style>