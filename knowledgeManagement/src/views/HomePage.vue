<template>
  <div >
         <!--a-layout-->
          <!--a-layout-content-->
            <div class="container">
              <!-- 第一行：欢迎信息 -->
              <!-- <div class="welcome-section">
                <h1 class="welcome-title">欢迎使用知识库管理系统</h1>
                <p class="welcome-subtitle">亲爱的 {{ username  }}  ，祝您工作愉快！</p>
              </div> -->
              
              <!-- 第二行：文档管理 -->
              <div class="content-section">
                <div class="section-header">
                  <h2 class="section-title">
                    <i class="fa-solid fa-files"></i>
                    知识库
                  </h2>
                </div>
                
                <div class="cards-container">
                  <a-spin v-if="loading" />
                  <a-empty v-else-if="documents.length === 0" description="暂无知识库" />
                  <a-card 
                    v-else
                    v-for="doc in documents" 
                    :key="doc.id"
                    class="project-card"
                    @click="handleCardClick(doc)"
                  >
                    <div class="card-body">
                      <div class="card-icon">
                        <i class="fa-solid fa-database"></i>
                      </div>
                      <h3 class="card-title">{{ doc.name }}</h3>
                      <p class="card-description">{{ doc.description || '暂无描述' }}</p>
                    </div>
                    <div class="card-footer">
                      <a-tag class="tag">{{ doc.creatorName }}</a-tag>
                      <span>共 {{ doc.file_count }} 篇</span>
                    </div>
                  </a-card>
                  
                  <a-card 
                    class="project-card view-all-card"
                    @click="handleViewAll()"
                  >
                    <div class="card-body">
                      <div class="card-icon">
                        <i class="fa-solid fa-eye"></i>
                      </div>
                      <h3 class="card-title">查看所有知识库</h3>
                      <p class="card-description">浏览全部文档资源</p>
                    </div>
                  </a-card>
                </div>
              </div>
              
              <!-- 第三行：知识分类 -->
              <!-- <div class="content-section">
                <div class="section-header">
                  <h2 class="section-title">
                    <i class="fa-solid fa-tags"></i>
                    知识分类
                  </h2>
                </div>
                
                <div class="cards-container">
                  <a-card 
                    v-for="category in categories" 
                    :key="category.id"
                    class="project-card"
                    @click="handleCardClick(category, 'categories')"
                  >
                    <div class="card-body">
                      <div class="card-icon">
                        <i :class="category.icon"></i>
                      </div>
                      <h3 class="card-title">{{ category.title }}</h3>
                      <p class="card-description">{{ category.description }}</p>
                    </div>
                    <div class="card-footer">
                      <a-tag class="tag">{{ category.tag }}</a-tag>
                      <span>共 {{ category.count }} 篇</span>
                    </div>
                  </a-card>
                  
                  <a-card 
                    class="project-card view-all-card"
                    @click="handleViewAll('categories')"
                  >
                    <div class="card-body">
                      <div class="card-icon">
                        <i class="fa-solid fa-folder-open"></i>
                      </div>
                      <h3 class="card-title">查看所有分类</h3>
                      <p class="card-description">浏览全部分类目录</p>
                    </div>
                  </a-card>
                </div>
              </div> -->
            </div>
          <!--/a-layout-content-->
        <!--/a-layout-->
  </div>
</template>
<script setup>
import { createApp, ref, reactive, onMounted,computed } from 'vue';
import { useAuthStore } from '../utils/auth';
import { useRouter } from 'vue-router';
import { loadKnowledgeBaseByPage } from '../api/service';
const authStore = useAuthStore()
const router = useRouter()
// 用户信息
const username = computed(() =>{
  console.log('username:', authStore.user.data?.username)
  return authStore.user.data?.username || '游客'
} )

// 知识库列表数据
const documents = ref([]);
const loading = ref(false);

// 知识分类数据
const categories = ref([
    {
    id: 1,
    title: '项目文档',
    description: '各类项目相关的需求、设计和验收文档',
    icon: 'fa-solid fa-folder',
    count: 28,
    tag: '项目',
    updatedBy: '孙项目经理',
    updatedAt: '2023-06-14'
    },
    {
    id: 2,
    title: '政策法规',
    description: '公司政策、行业规范和国家法律法规',
    icon: 'fa-solid fa-scale-balanced',
    count: 57,
    tag: '法规',
    updatedBy: '周法务',
    updatedAt: '2023-06-08'
    },
    {
    id: 3,
    title: '最佳实践',
    description: '工作流程优化、经验总结和成功案例',
    icon: 'fa-solid fa-lightbulb',
    count: 39,
    tag: '实践',
    updatedBy: '吴专家',
    updatedAt: '2023-06-03'
    },
    {
    id: 4,
    title: '知识库',
    description: '核心知识资产和经验积累文档',
    icon: 'fa-solid fa-database',
    count: 215,
    tag: '核心',
    updatedBy: '郑知识官',
    updatedAt: '2023-06-18'
    }
]);

// 处理卡片点击，跳转到知识库详情
const handleCardClick = (item) => {
    router.push('/knowledgedetail');
};

// 处理查看所有点击
const handleViewAll = () => {
    router.push({ name: 'KnowledgeManager' });
};

// 加载最新5个知识库
onMounted(async () => {
    loading.value = true;
    try {
        const retData = await loadKnowledgeBaseByPage({
            page: 0,
            pageSize: 5,
            owner: authStore.user.data?.ryid,
        });
        const page = retData?.data || {};
        documents.value = page.page || [];
    } catch (error) {
        console.error('加载知识库失败:', error);
    } finally {
        loading.value = false;
    }
});
</script>
<style scoped>
    .container {
      margin: 0 auto;
      padding: 20px;
      background: linear-gradient(180deg, #fff5f5 0%, #fff 100%);
      min-height: 100vh;
    }
    
    .welcome-section {
      background: linear-gradient(135deg, #c62f2f 0%, #8b1a1a 100%);
      padding: 50px 40px;
      border-radius: 8px;
      margin-bottom: 30px;
      text-align: center;
      box-shadow: 0 4px 20px rgba(198, 47, 47, 0.3);
      position: relative;
      overflow: hidden;
    }
    
    .welcome-section::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
      opacity: 0.5;
    }
    
    .welcome-title {
      font-size: 2.5rem;
      margin-bottom: 15px;
      font-weight: 700;
      color: #ffffff;
      text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
      position: relative;
      letter-spacing: 2px;
    }
    
    .welcome-subtitle {
      font-size: 1.2rem;
      color: rgba(255, 255, 255, 0.95);
      position: relative;
      font-weight: 400;
    }
    
    .content-section {
      background: white;
      border-radius: 8px;
      padding: 30px;
      margin-bottom: 30px;
      box-shadow: 0 2px 12px rgba(198, 47, 47, 0.08);
      border: 1px solid #f0e6e6;
    }
    
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 25px;
      padding-bottom: 20px;
      border-bottom: 2px solid #c62f2f;
      position: relative;
    }
    
    .section-header::after {
      content: '';
      position: absolute;
      bottom: -2px;
      left: 0;
      width: 80px;
      height: 2px;
      background: #c62f2f;
    }
    
    .section-title {
      font-size: 1.6rem;
      color: #c62f2f;
      font-weight: 700;
      display: flex;
      align-items: center;
      gap: 12px;
    }
    
    .section-title i {
      color: #c62f2f;
      font-size: 1.4rem;
    }
    
    .cards-container {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
      gap: 24px;
    }
    
    .project-card {
      border-radius: 8px;
      overflow: hidden;
      transition: all 0.3s ease;
      cursor: pointer;
      border: 2px solid #f0e6e6;
      height: 100%;
      display: flex;
      flex-direction: column;
      background: white;
    }
    
    .project-card:hover {
      transform: translateY(-8px);
      box-shadow: 0 8px 24px rgba(198, 47, 47, 0.2);
      border-color: #c62f2f;
    }
    
    .card-body {
      padding: 24px;
      flex-grow: 1;
      display: flex;
      flex-direction: column;
    }
    
    .card-icon {
      font-size: 2.5rem;
      margin-bottom: 18px;
      color: #c62f2f;
      text-align: center;
    }
    
    .card-title {
      font-size: 1.2rem;
      font-weight: 700;
      margin-bottom: 12px;
      color: #1f2d3d;
      text-align: center;
    }
    
    .card-description {
      color: #5e6d82;
      font-size: 0.95rem;
      line-height: 1.6;
      flex-grow: 1;
      text-align: center;
    }
    
    .card-footer {
      padding: 18px 24px;
      background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%);
      border-top: 1px solid #f0e6e6;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .view-all-card {
      background: linear-gradient(135deg, #c62f2f 0%, #8b1a1a 100%);
      color: white;
      border: none;
      box-shadow: 0 4px 16px rgba(198, 47, 47, 0.3);
    }
    
    .view-all-card:hover {
      box-shadow: 0 8px 24px rgba(198, 47, 47, 0.4);
    }
    
    .view-all-card .card-body {
      justify-content: center;
      align-items: center;
      text-align: center;
    }
    
    .view-all-card .card-icon {
      color: white;
      font-size: 2.8rem;
    }
    
    .view-all-card .card-title {
      color: white;
      font-size: 1.4rem;
    }
    
    .view-all-card .card-description {
      color: rgba(255, 255, 255, 0.9);
    }
    
    .tag {
      display: inline-block;
      padding: 4px 12px;
      border-radius: 4px;
      font-size: 0.8rem;
      background: linear-gradient(135deg, #c62f2f 0%, #8b1a1a 100%);
      color: white;
      font-weight: 600;
    }
    
    .user-info {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-top: 12px;
    }
    
    .user-avatar {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background: linear-gradient(135deg, #c62f2f 0%, #8b1a1a 100%);
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: bold;
      box-shadow: 0 2px 8px rgba(198, 47, 47, 0.3);
    }
    
    @media (max-width: 768px) {
      .cards-container {
        grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
      }
      
      .welcome-title {
        font-size: 2rem;
      }
      
      .welcome-section {
        padding: 40px 30px;
      }
    }
    
    @media (max-width: 576px) {
      .cards-container {
        grid-template-columns: 1fr;
      }
      
      .container {
        padding: 15px;
      }
      
      .welcome-section {
        padding: 35px 25px;
      }
      
      .welcome-title {
        font-size: 1.8rem;
      }
      
      .welcome-subtitle {
        font-size: 1rem;
      }
      
      .section-title {
        font-size: 1.4rem;
      }
    }
</style>