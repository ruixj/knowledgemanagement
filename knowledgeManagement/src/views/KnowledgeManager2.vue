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
        <!-- 批量操作区域 -->
        <div v-if="selectedRowKeys.length > 0" class="batch-actions">
          <div class="selected-count">
            <check-circle-outlined />
            已选择 {{ selectedRowKeys.length }} 个知识库
          </div>
          <div>
            <a-button 
              type="danger" 
              @click="handleBatchDelete" 
              :disabled="selectedRowKeys.length === 0"
            >
              <template #icon>
                <delete-outlined />
              </template>
              批量删除
            </a-button>
            <a-button 
              @click="clearSelection"
              style="margin-left: 8px;"
            >
              取消选择
            </a-button>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <a-spin size="large" />
          <div style="margin-top: 16px;">加载中...</div>
        </div>
        
        <!-- 空状态 -->
        <div v-else-if="knowledgeList.length === 0" class="empty-state">
          <folder-open-outlined />
          <h3>暂无知识库</h3>
          <div>点击上方按钮创建第一个知识库</div>
        </div>
        
        <!-- 知识库网格 -->
        <div v-else class="knowledge-grid">
          <a-row :gutter="[16, 16]">
            <a-col 
              v-for="item in knowledgeList" 
              :key="item.id" 
              :xs="24" 
              :sm="12" 
              :md="8" 
              :lg="6"
            >
            <div class="knowledge-card-wrapper">
              <div class="knowledge-card" 
                  @click="handleCardClick(item, $event)"
                  :class="{ selected: selectedRowKeys.includes(item.id) }"
              >
                  <!-- 选择复选框 -->
                <div class="card-checkbox">
                    <a-checkbox 
                      :checked="selectedRowKeys.includes(item.id)"
                      @click.stop
                      @change="(e) => handleCheckboxChange(e, item.id)"
                    />
                </div>
                <div class="card-header">
                  <div class="card-title">
                    <folder-outlined />
                    {{ item.name }}
                  </div>
                </div>
                <div class="card-body">
                  <div class="card-desc">{{ item.description }}</div>
                  <div class="card-meta">
                    <div class="card-meta-item">
                      <user-outlined />
                      创建者: {{ item.creatorName }}
                    </div>
                    <div class="card-meta-item">
                      <clock-circle-outlined />
                      更新: {{ item.updated_at }}
                    </div>
                    <div class="card-meta-item">
                      <file-text-outlined />
                      文档: {{ item.file_count }} 个
                    </div>
                  </div>
                </div>
                <div class="card-footer">
                  <span :style="{ color: item.visibility === 'public' ? '#52c41a' : '#faad14' }">
                    {{ item.visibility === 'public' ? '公开' : '私有' }}
                  </span>
                  <div class="card-actions">
                    <a-button size="small" @click.stop="editKnowledge(item)">编辑</a-button>
                    <a-button size="small" type="primary" @click.stop="viewKnowledge(item)">查看</a-button>
                    <a-popconfirm
                        title="确定要删除这个知识库吗？"
                        ok-text="删除"
                        cancel-text="取消"
                        @confirm="() => deleteKnowledge(item.id)"
                      >
                        <a-button 
                          size="small" 
                          danger 
                          @click.stop
                        >
                          删除
                        </a-button>
                      </a-popconfirm>
                  </div>
                </div>
              </div>
            </div>
            </a-col>
          </a-row>
        </div>
        
        <!-- 分页 -->
        <div v-if="knowledgeList.length > 0" class="pagination-container">
          <a-pagination
            v-model:current="currentPage"
            :total="total"
            :page-size="pageSize"
            show-less-items
            show-size-changer
            :page-size-options="['8', '16', '24', '32']"
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
        :rules="formRules"
        ref="createFormRef"
      >
        <a-form-item label="知识库名称" required>
          <a-input v-model:value="createForm.name" placeholder="请输入知识库名称" />
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
        <!-- <a-form-item label="可见性">
          <a-radio-group v-model:value="createForm.visibility">
            <a-radio value="public">公开</a-radio>
            <a-radio value="private">私有</a-radio>
          </a-radio-group>
        </a-form-item> -->
      </a-form>
    </a-modal>

    <!-- 编辑知识库模态框 -->
    <a-modal
      v-model:visible="editModalVisible"
      title="编辑知识库"
      :confirm-loading="editing"
      @ok="handleEditOk"
      @cancel="handleEditCancel"
      :width="600"
    >
      <a-form
        ref="editFormRef"
        :model="editForm"
        :rules="formRules"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="知识库名称" name="name" required>
          <a-input 
            v-model:value="editForm.name" 
            placeholder="请输入知识库名称"
          />
        </a-form-item>
        <a-form-item label="描述" name="description">
          <a-textarea 
            v-model:value="editForm.description" 
            :rows="4" 
            placeholder="请输入知识库描述"
            show-count 
            :maxlength="200"
          />
        </a-form-item>
        <!-- <a-form-item label="可见性" name="visibility">
          <a-radio-group v-model:value="editForm.visibility">
            <a-radio value="public">公开</a-radio>
            <a-radio value="private">私有</a-radio>
          </a-radio-group>
        </a-form-item> -->
        <a-form-item label="文档数量" name="file_count" :disabled="true">
          <a-input-number 
            v-model:value="editForm.file_count" 
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="创建者" name="creatorName" :disabled="true">
          <a-input 
            v-model:value="editForm.creatorName" 
            style="width: 100%"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>
<script  >
import { ref, reactive, computed, onMounted,watch  } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  createKnowledgeBase,
  loadKnowledgeBaseByPage
} from '../api/service'

import {useAuthStore} from '../utils/auth'
import { useCurrentKBStore } from '../state/appstate'

export default {
  name: 'KnowledgeManager',
  setup(){   
    const kbStore = useCurrentKBStore()
    // 响应式数据
    const knowledgeList = ref([]);
    const currentPage = ref(1);
    const total = ref(0); // 增加总数以适应网格布局
    const pageSize = ref(8); // 调整为8的倍数，适合网格布局
    const selectedRowKeys = ref([]); 

    const createModalVisible = ref(false);
    const creating = ref(false);
    const loading = ref(false);
    const searchKeyword = ref('');
  
    const editModalVisible = ref(false);
    const editing = ref(false);
    const authStore = useAuthStore()
    
    // 创建表单数据
    const createForm = reactive({
      name: '',
      description: '',
      visibility: 'public'
    });

    // 编辑表单数据
    const editForm = reactive({
      id: null,
      name: '',
      description: '',
      file_count: 0,
      creatorName: ''
    });

        // 表单引用
    const createFormRef = ref(null);
    const editFormRef = ref(null);
        
    // 表单验证规则
    const formRules = {
      title: [
        { required: true, message: '请输入知识库名称', trigger: 'blur' },
        { min: 2, max: 50, message: '知识库名称长度为2-50个字符', trigger: 'blur' }
      ],
      description: [
        { max: 200, message: '描述不能超过200个字符', trigger: 'blur' }
      ],
  
    };
    const router = useRouter()

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

      const data = {
        page: currentPage.value - 1,
        pageSize: pageSize.value,
        owner: authStore.user.data?.ryid,
      };
      
      try {
        let retData = await loadKnowledgeBaseByPage(data);
        const page  = retData.data || [];
        knowledgeList.value = page.page;
        total.value = page.total;
      }
      // catch(error){
      //   console.error('加载知识库列表失败:', error);
      // }
      finally{
        loading.value = false;
      }
      
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
      if (!createForm.name.trim()) {
        // 实际应用中应该使用antd的message组件提示
        alert('请输入知识库名称');
        return;
      }
      
      creating.value = true;    
      await createKnowledgeBase(createForm);
      console.log('创建知识库:', createForm);
      
      // 实际应用中这里应该调用API创建知识库
      creating.value = false;
      createModalVisible.value = false;
      
      // 重置表单
      Object.assign(createForm, {
        name: '',
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
        name: '',
        description: '',
        visibility: 'public'
      });
    };
    
    const viewKnowledge = (item) => {
      console.log('查看知识库:', item);
      kbStore.setCurrentKB({ id: item.id, name: item.name })
      router.push('/knowledgedetail')
    };
    
    const editKnowledge = (item) => {
          console.log('编辑知识库:', item);
          
          // 填充编辑表单数据
          Object.assign(editForm, {
            id: item.id,
            name: item.name,
            description: item.description,
            file_count: item.file_count,
            creatorName: item.creatorName
          });
          
          editModalVisible.value = true;
    };

    const handleEditOk = async () => {
      try {
        // 表单验证
        await editFormRef.value.validate();
        
        editing.value = true;
        
        // 模拟API调用延迟
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // 更新知识库数据
        const index = knowledgeList.value.findIndex(item => item.id === editForm.id);
        if (index !== -1) {
          knowledgeList.value[index] = {
            ...knowledgeList.value[index],
            name: editForm.name,
            description: editForm.description,
            file_count: editForm.file_count,
            creatorName: editForm.creatorName,
            updateTime: new Date().toISOString().split('T')[0].replace(/-/g, '-')
          };
          
          message.success('知识库更新成功');
        } else {
          message.error('未找到要编辑的知识库');
        }
        
        editing.value = false;
        editModalVisible.value = false;
        
        // 重置表单验证状态
        nextTick(() => {
          editFormRef.value.clearValidate();
        });
        
      } catch (error) {
        console.error('更新知识库失败:', error);
        editing.value = false;
      }
    };

    const handleEditCancel = () => {
      editModalVisible.value = false;
      
      // 重置表单验证状态
      nextTick(() => {
        if (editFormRef.value) {
          editFormRef.value.clearValidate();
        }
      });
    };

        // 删除单个知识库
    const deleteKnowledge = async (id) => {
      try {
        // 模拟API调用延迟
        await new Promise(resolve => setTimeout(resolve, 800));
        
        // 从列表中删除
        const index = knowledgeList.value.findIndex(item => item.id === id);
        if (index !== -1) {
          const deletedItem = knowledgeList.value[index];
          knowledgeList.value.splice(index, 1);
          
          // 从选中列表中移除
          selectedRowKeys.value = selectedRowKeys.value.filter(key => key !== id);
          
          message.success(`知识库 "${deletedItem.title}" 删除成功`);
          
          // 如果删除后列表为空且不是第一页，则跳转到前一页
          if (knowledgeList.value.length === 0 && currentPage.value > 1) {
            currentPage.value -= 1;
            loadKnowledgeList();
          }
        } else {
          message.error('未找到要删除的知识库');
        }
      } catch (error) {
        console.error('删除知识库失败:', error);
        message.error('删除失败，请重试');
      }
    };

    // 批量删除
    const handleBatchDelete = () => {
      if (selectedRowKeys.value.length === 0) {
        message.warning('请先选择要删除的知识库');
        return;
      }
      
      // 确认对话框
      if (confirm(`确定要删除选中的 ${selectedRowKeys.value.length} 个知识库吗？此操作不可撤销。`)) {
        // 模拟批量删除
        selectedRowKeys.value.forEach(id => {
          const index = knowledgeList.value.findIndex(item => item.id === id);
          if (index !== -1) {
            knowledgeList.value.splice(index, 1);
          }
        });
        
        message.success(`成功删除 ${selectedRowKeys.value.length} 个知识库`);
        selectedRowKeys.value = []; // 清空选择
        
        // 如果删除后列表为空且不是第一页，则跳转到前一页
        if (knowledgeList.value.length === 0 && currentPage.value > 1) {
          currentPage.value -= 1;
          loadKnowledgeList();
        }
      }
    };
    
    // 处理卡片点击
    const handleCardClick = (item, event) => {
      // 如果点击的是复选框或按钮，则不触发卡片选择
      if (event.target.closest('.card-checkbox') || 
          event.target.closest('.card-actions') ||
          event.target.closest('.visibility-badge')) {
        return;
      }
      
      // 切换选择状态
      const index = selectedRowKeys.value.indexOf(item.id);
      if (index === -1) {
        selectedRowKeys.value.push(item.id);
      } else {
        selectedRowKeys.value.splice(index, 1);
      }
    };
    
    // 处理复选框变化
    const handleCheckboxChange = (e, id) => {
      if (e.target.checked) {
        if (!selectedRowKeys.value.includes(id)) {
          selectedRowKeys.value.push(id);
        }
      } else {
        const index = selectedRowKeys.value.indexOf(id);
        if (index !== -1) {
          selectedRowKeys.value.splice(index, 1);
        }
      }
    };
    
    // 清空选择
    const clearSelection = () => {
      selectedRowKeys.value = [];
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
      editModalVisible,
      creating,
      editing,
      loading,
      searchKeyword,
      selectedRowKeys,
      createForm,
      editForm,
      createFormRef,
      editFormRef,
      formRules,
      handleSearch,
      showCreateModal,
      handleCreateOk,
      handleCreateCancel,
      viewKnowledge,
      editKnowledge,
      handleEditOk,
      handleEditCancel,
      deleteKnowledge,
      handleBatchDelete,
      handleCardClick,
      handleCheckboxChange,
      clearSelection,
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
    
    .batch-actions {
      margin-bottom: 16px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 0;
    }
    
    .selected-count {
      color: #1890ff;
      font-weight: 500;
    }

    .knowledge-grid {
      margin-top: 24px;
    }
    
    .knowledge-card {
      height: 100%;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.09);
      transition: all 0.3s;
      cursor: pointer;
      border: 1px solid #f0f0f0;
      display: flex;
      flex-direction: column;
    }
    .knowledge-card.selected {
      border-color: #1890ff;
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
    }
    
 
    .knowledge-card:hover {
      transform: translateY(-4px);
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
      border-color: #1890ff;
    }
    
    .card-header {
      padding: 16px;
      border-bottom: 1px solid #f0f0f0;
      background-color: #fafafa;
      border-radius: 8px 8px 0 0;
      flex-shrink: 0;
    }
    
    .card-title {
      font-size: 16px;
      font-weight: 500;
      margin-bottom: 0;
      color: #262626;
      display: flex;
      align-items: center;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    
    .card-title i {
      margin-right: 8px;
      color: #52c41a;
      flex-shrink: 0;
    }
    
    .card-body {
      padding: 16px;
      flex-grow: 1;
      display: flex;
      flex-direction: column;
    }
    
    .card-desc {
      color: #8c8c8c;
      font-size: 14px;
      line-height: 1.5;
      margin-bottom: 16px;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
      flex-grow: 1;
    }
    
    .card-meta {
      display: flex;
      flex-direction: column;
      gap: 8px;
      color: #8c8c8c;
      font-size: 12px;
      border-top: 1px dashed #f0f0f0;
      padding-top: 12px;
    }
    
    .card-meta-item {
      display: flex;
      align-items: center;
    }
    
    .card-meta i {
      margin-right: 6px;
    }
    
    .card-footer {
      padding: 12px 16px;
      border-top: 1px solid #f0f0f0;
      display: flex;
      justify-content: space-between;
      align-items: center;
      background-color: #fafafa;
      border-radius: 0 0 8px 8px;
      flex-shrink: 0;
    }
    
    .card-actions {
      display: flex;
      gap: 8px;
    }
    
    .pagination-container {
      display: flex;
      justify-content: center;
      margin-top: 24px;
    }
    
    .empty-state {
      text-align: center;
      padding: 60px 0;
      color: #8c8c8c;
    }
    
    .empty-state i {
      font-size: 64px;
      margin-bottom: 16px;
      color: #d9d9d9;
    }
    
    .empty-state h3 {
      font-size: 18px;
      margin-bottom: 8px;
      color: #595959;
    }
    
    .loading-state {
      text-align: center;
      padding: 60px 0;
    }
    
    .card-checkbox {
      position: absolute;
      top: 12px;
      left: 8px;
      z-index: 1;
    }
    
    .card-checkbox .ant-checkbox-wrapper {
      background-color: white;
      border-radius: 50%;
      padding: 4px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
    
    .knowledge-card-wrapper {
      position: relative;
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
      
      .card-meta {
        flex-direction: column;
      }

      .batch-actions {
        flex-direction: column;
        gap: 12px;
        align-items: flex-start;
      }
    }
    
    @media (max-width: 576px) {
      .knowledge-container {
        padding: 12px;
      }
      
      .header-section, .content-section {
        padding: 12px;
      }
      
      .card-footer {
        flex-direction: column;
        gap: 8px;
        align-items: flex-start;
      }
      
      .card-actions {
        width: 100%;
        justify-content: flex-end;
      }
    }
</style>