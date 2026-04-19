import FileUpload from  '../views/FileUpload.vue'
import AppLayout from '../applayout/appLayout.vue'
import AppLayout2 from '../applayout/appLayout2.vue'
import SplitPanel from '../views/SplitPanel.vue'
import FileList from '../views/FileList.vue'
import ResponsiveTable from '../views/ResponsiveTable.vue'
import FileUpload2 from '../views/FileUpload2.vue'

import FileManager from '../views/FileManager.vue'
import Register from '../views/Register.vue'
import Login   from '../views/Login.vue'
import KnowledgeManager from '../views/KnowledgeManager2.vue'
import KnowledgeDetail from '../views/KnowledgeDetail.vue'
import HomePage from '../views/HomePage.vue'
import Configuration from '../components/Configuration.vue'


export const routes = [
    {
        path:'/',
        redirect:{
            name:'Login'
        }
    },
    { 
        path: '/app', 
        name: 'app',
        redirect: { name: 'homepage' },
        meta: {
            title: '首页',
            breadcrumb: true,
        },
 
        component: AppLayout2,
        children: [
            {
                // 当 /user/:id/profile 匹配成功
                path: 'homepage',
                component: HomePage,
                name:'homepage',
                meta: {
                    title: '首页',
                    breadcrumb: true,
                }
            },
            {
                // 当 /user/:id/profile 匹配成功
                path: 'fileupload',
                component: FileUpload,
                breadcrumbName:'上传文件',
                name:'FileUpload',
                meta: {
                    title: '上传文件',
                     breadcrumb: true,
                }
            },
            {
                // 当 /user/:id/profile 匹配成功
                path: 'fileList',
                component: FileList,
                breadcrumbName:'文件列表',
                meta: {
                    title: '文件列表',
                     breadcrumb: true,
                }
            },
            {
               
                path: 'fileManager',
                component: FileManager,
                breadcrumbName:'数据集管理',
                meta: {
                    title: '数据集管理',
                     breadcrumb: true,
                }
            },
 
            {
                
                path: 'fileupload2',
                component: FileUpload2,
                breadcrumbName:'上传文件2',
                meta: {
                    title: '上传文件2',
                     breadcrumb: true,
                }
            },
            {
                
                path: 'responsiveTable',
                component: ResponsiveTable,
                meta: {
                    title: '响应式表格',
                    breadcrumb: true,
                }
                 
            },
            {
                 
                path: 'knowledge',
                component: KnowledgeManager,
                name:'KnowledgeManager',
                meta: {
                    title: '知识库管理',
                    breadcrumb: true,
                    parent: 'app'
                }
            },
            {
                path:'/knowledgedetail',
                component:KnowledgeDetail,
                name:'KnowledgeDetail',
                meta: {
                    title: '知识库详情',
                    breadcrumb: true,
                    parent: 'KnowledgeManager'
                },
                redirect: { name: 'fileManager' },
                children: [
                    {
                        path:'/fileManager',
                        component:FileManager,
                        name:'fileManager',
                        meta: {
                            title: '数据集管理',
                            breadcrumb: true,
                            parent: 'KnowledgeDetail'
                        },
                    },
                    {
                        path:'/configuration',
                        component:Configuration,
                        name:'configuration',
                        meta: {
                            title: '数据集配置',
                            breadcrumb: true,
                            parent: 'KnowledgeDetail'
                        },
                    }

                ]
          },
        ]
    },
    // {
    //     path:'/knowledgedetail',
    //     component:KnowledgeDetail,
    //     name:'KnowledgeDetail'
    // },
    {
        path:'/login',
        component:Login,
        name:'Login'
    },
    {
         
        path:'/register',
        component:Register,
        name:'Register'
    }
];
