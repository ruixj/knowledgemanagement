<template>
    <!-- 线索行 -->
    <div class="threadbar-row">
        <a-breadcrumb :routes="routes">
            <template #itemRender="{ route, params, routes, paths }">
            <span v-if="routes.indexOf(route) === routes.length - 1">{{route.breadcrumbName}}</span>
            <router-link v-else :to="paths.join('/')">{{route.breadcrumbName}}</router-link>
            </template>
        </a-breadcrumb>
    </div>
        
</template>

<script setup>
 import {ref} from 'vue'
 
   const routes =  ref([
//         { 
//             path: '/app', 
//             name: 'app',
//             component: AppLayout2 ,
//             children: [
//                 {
//                     // 当 /user/:id/profile 匹配成功
//                     path: 'fileupload',
//                     component: FileUpload,

//                 },
//                 {
//                     // 当 /user/:id/profile 匹配成功
//                     path: 'fileList',
//                     component: FileList,
//                 },
//                 {
                
//                     path: 'fileManager',
//                     component: FileManager,
//                 },
//                 {
                    
//                     path: 'splitter',
//                     component: SplitPanel,
//                 },
//                 {
                    
//                     path: 'fileupload2',
//                     component: FileUpload2,
//                 },
//                 {
                    
//                     path: 'responsiveTable',
//                     component: ResponsiveTable,
//                 },
//                 {
                    
//                     path: 'knowledge',
//                     component: KnowledgeManager,
//                 },
//             ]
//         } 
    
     ])

</script>

<style scoped>
.threadbar-row {
    padding: 16px 24px;
    border-bottom: 1px solid #f0f0f0;
    background-color: #fafafa;
}

</style>
    