<template>
        <div class="container">
            <div class="header">
                <h1>用户数据管理系统</h1>
                <p>多条件筛选与数据表格展示</p>
            </div>
            
            <div class="content">
                <div class="stats">
                    <div class="stat-card">
                        <div class="stat-number">{{ filteredUsers.length }}</div>
                        <div class="stat-label">总用户数</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number">{{ averageAge }}</div>
                        <div class="stat-label">平均年龄</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number">{{ maleCount }}</div>
                        <div class="stat-label">男性用户</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number">{{ femaleCount }}</div>
                        <div class="stat-label">女性用户</div>
                    </div>
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
                        
                        <div class="filter-item">
                            <div class="filter-label">年龄筛选（精确匹配）</div>
                            <a-input 
                                v-model:value="filters.age" 
                                placeholder="输入年龄" 
                                allow-clear
                                type="number"
                                min="0"
                            >
                                <template #prefix>
                                    <calendar-outlined />
                                </template>
                            </a-input>
                        </div>
                        
                        <div class="filter-item">
                            <div class="filter-label">性别筛选</div>
                            <a-select 
                                v-model:value="filters.gender" 
                                placeholder="选择性别" 
                                allow-clear
                            >
                                <a-select-option value="男">男</a-select-option>
                                <a-select-option value="女">女</a-select-option>
                            </a-select>
                        </div>
                        
                        <div class="filter-item">
                            <div class="filter-label">邮箱筛选（模糊匹配）</div>
                            <a-input 
                                v-model:value="filters.email" 
                                placeholder="输入邮箱关键词" 
                                allow-clear
                            >
                                <template #prefix>
                                    <mail-outlined />
                                </template>
                            </a-input>
                        </div>
                    </div>
                    
                    <div class="filter-actions">
                        <a-button @click="resetFilters">
                            <template #icon><redo-outlined /></template>
                            重置所有筛选
                        </a-button>
                        <a-button type="primary" @click="exportData" :disabled="filteredUsers.length === 0">
                            <template #icon><download-outlined /></template>
                            导出数据
                        </a-button>
                    </div>
                </div>
                
                <div class="table-section">
                    <a-table 
                        :dataSource="filteredUsers" 
                        :columns="columns" 
                        :pagination="{ pageSize: 5, showSizeChanger: false, showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条` }" 
                        rowKey="id"
                        :scroll="{ x: 800 }"
                    >
                        <template #bodyCell="{ column, record }">
                            <template v-if="column.key === 'name'">
                                <a-typography-text strong>{{ record.name }}</a-typography-text>
                            </template>
                            <template v-else-if="column.key === 'gender'">
                                <a-tag :class="record.gender === '男' ? 'tag-male' : 'tag-female'">
                                    {{ record.gender }}
                                </a-tag>
                            </template>
                            <template v-else-if="column.key === 'action'">
                                <a-space>
                                    <a-button type="link" size="small" @click="viewUser(record)">
                                        <template #icon><eye-outlined /></template>
                                        查看
                                    </a-button>
                                    <a-button type="link" size="small" @click="editUser(record)">
                                        <template #icon><edit-outlined /></template>
                                        编辑
                                    </a-button>
                                    <a-button type="link" danger size="small" @click="deleteUser(record)">
                                        <template #icon><delete-outlined /></template>
                                        删除
                                    </a-button>
                                </a-space>
                            </template>
                        </template>
                        
                        <template #emptyText>
                            <div class="empty-state">
                                <div class="empty-state-icon">
                                    <search-outlined />
                                </div>
                                <h3>未找到匹配的用户数据</h3>
                                <p>尝试调整筛选条件或重置筛选</p>
                            </div>
                        </template>
                    </a-table>
                </div>
                
                <div class="footer">
                    <p>© 2023 用户数据管理系统 - 基于 Vue 3 和 Ant Design Vue</p>
                </div>
            </div>
        </div>
</template>

<script>
        import  {  ref, computed, watch } from "vue"
        import { 
            UserOutlined, CalendarOutlined, MailOutlined, FilterOutlined,
            RedoOutlined, DownloadOutlined, EyeOutlined, EditOutlined,
            DeleteOutlined, SearchOutlined
        } from '@ant-design/icons-vue';

        import { message } from "ant-design-vue";
        
        // 创建应用
        export default {
           name: 'ResponsiveTable',
            setup() {
                // 用户数据
                const users = ref([
                    { id: 1, name: '张三', age: 28, gender: '男', email: 'zhangsan@example.com', department: '技术部', joinDate: '2020-03-15' },
                    { id: 2, name: '李四', age: 32, gender: '男', email: 'lisi@company.com', department: '市场部', joinDate: '2019-05-22' },
                    { id: 3, name: '王五', age: 25, gender: '男', email: 'wangwu@test.com', department: '销售部', joinDate: '2021-01-10' },
                    { id: 4, name: '赵六', age: 29, gender: '男', email: 'zhaoliu@example.org', department: '技术部', joinDate: '2020-08-05' },
                    { id: 5, name: '钱七', age: 35, gender: '男', email: 'qianqi@company.net', department: '人事部', joinDate: '2018-11-30' },
                    { id: 6, name: '孙八', age: 27, gender: '男', email: 'sunba@test.org', department: '财务部', joinDate: '2021-06-18' },
                    { id: 7, name: '周九', age: 31, gender: '男', email: 'zhoujiu@example.com', department: '市场部', joinDate: '2019-09-12' },
                    { id: 8, name: '吴十', age: 26, gender: '男', email: 'wushi@company.com', department: '技术部', joinDate: '2022-02-28' },
                    { id: 9, name: '郑十一', age: 33, gender: '男', email: 'zhengshiyi@test.com', department: '销售部', joinDate: '2018-07-22' },
                    { id: 10, name: '王芳', age: 29, gender: '女', email: 'wangfang@example.com', department: '人事部', joinDate: '2020-04-17' },
                    { id: 11, name: '李娜', age: 24, gender: '女', email: 'lina@company.org', department: '市场部', joinDate: '2021-11-08' },
                    { id: 12, name: '张丽', age: 30, gender: '女', email: 'zhangli@test.net', department: '技术部', joinDate: '2019-02-25' },
                    { id: 13, name: '刘敏', age: 27, gender: '女', email: 'liumin@example.org', department: '财务部', joinDate: '2020-10-11' },
                    { id: 14, name: '陈静', age: 32, gender: '女', email: 'chenjing@company.com', department: '销售部', joinDate: '2018-12-03' },
                    { id: 15, name: '杨颖', age: 26, gender: '女', email: 'yangying@test.org', department: '人事部', joinDate: '2021-03-19' }
                ]);
                
                // 筛选条件
                const filters = ref({
                    name: '',
                    age: '',
                    gender: '',
                    email: ''
                });
                
                // 表格列定义
                const columns = ref([
                    {
                        title: 'ID',
                        dataIndex: 'id',
                        key: 'id',
                        width: 80,
                        sorter: (a, b) => a.id - b.id
                    },
                    {
                        title: '姓名',
                        dataIndex: 'name',
                        key: 'name',
                        width: 120,
                        sorter: (a, b) => a.name.localeCompare(b.name)
                    },
                    {
                        title: '年龄',
                        dataIndex: 'age',
                        key: 'age',
                        width: 100,
                        sorter: (a, b) => a.age - b.age
                    },
                    {
                        title: '性别',
                        dataIndex: 'gender',
                        key: 'gender',
                        width: 100,
                        filters: [
                            { text: '男', value: '男' },
                            { text: '女', value: '女' }
                        ],
                        onFilter: (value, record) => record.gender === value
                    },
                    {
                        title: '邮箱',
                        dataIndex: 'email',
                        key: 'email',
                        width: 200,
                        ellipsis: true
                    },
                    {
                        title: '部门',
                        dataIndex: 'department',
                        key: 'department',
                        width: 120,
                        filters: [
                            { text: '技术部', value: '技术部' },
                            { text: '市场部', value: '市场部' },
                            { text: '销售部', value: '销售部' },
                            { text: '人事部', value: '人事部' },
                            { text: '财务部', value: '财务部' }
                        ],
                        onFilter: (value, record) => record.department === value
                    },
                    {
                        title: '入职日期',
                        dataIndex: 'joinDate',
                        key: 'joinDate',
                        width: 120,
                        sorter: (a, b) => new Date(a.joinDate) - new Date(b.joinDate)
                    },
                    {
                        title: '操作',
                        key: 'action',
                        fixed: 'right',
                        width: 200
                    }
                ]);
                
                // 过滤后的用户数据
                const filteredUsers = computed(() => {
                    return users.value.filter(user => {
                        // 姓名筛选（模糊匹配）
                        if (filters.value.name && !user.name.toLowerCase().includes(filters.value.name.toLowerCase())) {
                            return false;
                        }
                        
                        // 年龄筛选（精确匹配）
                        if (filters.value.age && user.age !== parseInt(filters.value.age)) {
                            return false;
                        }
                        
                        // 性别筛选
                        if (filters.value.gender && user.gender !== filters.value.gender) {
                            return false;
                        }
                        
                        // 邮箱筛选（模糊匹配）
                        if (filters.value.email && !user.email.toLowerCase().includes(filters.value.email.toLowerCase())) {
                            return false;
                        }
                        
                        return true;
                    });
                });
                
                // 计算平均年龄
                const averageAge = computed(() => {
                    if (filteredUsers.value.length === 0) return 0;
                    const totalAge = filteredUsers.value.reduce((sum, user) => sum + user.age, 0);
                    return (totalAge / filteredUsers.value.length).toFixed(1);
                });
                
                // 计算男性数量
                const maleCount = computed(() => {
                    return filteredUsers.value.filter(user => user.gender === '男').length;
                });
                
                // 计算女性数量
                const femaleCount = computed(() => {
                    return filteredUsers.value.filter(user => user.gender === '女').length;
                });
                
                // 重置所有筛选条件
                const resetFilters = () => {
                    filters.value = {
                        name: '',
                        age: '',
                        gender: '',
                        email: ''
                    };
                    message.success('筛选条件已重置');
                };
                
                // 导出数据
                const exportData = () => {
                    message.info('模拟导出数据功能');
                    // 实际项目中这里会有真实的导出逻辑
                };
                
                // 查看用户详情
                const viewUser = (user) => {
                    modal.info({
                        title: '用户信息',
                        content: `姓名: ${user.name}\n年龄: ${user.age}\n性别: ${user.gender}\n邮箱: ${user.email}\n部门: ${user.department}\n入职日期: ${user.joinDate}`,
                        okText: '关闭'
                    });
                };
                
                // 编辑用户
                const editUser = (user) => {
                    message.info(`模拟编辑用户: ${user.name}`);
                    // 实际项目中这里会有真实的编辑逻辑
                };
                
                // 删除用户
                const deleteUser = (user) => {
                    modal.confirm({
                        title: '确认删除',
                        content: `您确定要删除用户 "${user.name}" 吗？此操作不可恢复。`,
                        okText: '确认',
                        okType: 'danger',
                        cancelText: '取消',
                        onOk() {
                            users.value = users.value.filter(u => u.id !== user.id);
                            message.success(`用户 "${user.name}" 已删除`);
                        }
                    });
                };
                
                return {
                    users,
                    filters,
                    columns,
                    filteredUsers,
                    averageAge,
                    maleCount,
                    femaleCount,
                    resetFilters,
                    exportData,
                    viewUser,
                    editUser,
                    deleteUser
                };
            }
        } ;
</script>

<style scoped>
        .container {
            max-width: 1400px;
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
            justify-content: flex-end;
            gap: 12px;
            margin-top: 16px;
        }
        .stats {
            display: flex;
            gap: 16px;
            margin-bottom: 24px;
            flex-wrap: wrap;
        }
        .stat-card {
            flex: 1;
            min-width: 180px;
            padding: 16px;
            background: #f9f9f9;
            border-radius: 8px;
            text-align: center;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        }
        .stat-number {
            font-size: 24px;
            font-weight: bold;
            color: #1890ff;
            margin-bottom: 4px;
        }
        .stat-label {
            color: #666;
            font-size: 14px;
        }
        .table-section {
            margin-top: 24px;
        }
        .footer {
            text-align: center;
            padding: 16px;
            margin-top: 24px;
            color: #666;
            border-top: 1px solid #f0f0f0;
        }
        .tag-male {
            background-color: #e6f7ff;
            color: #1890ff;
            border-color: #91d5ff;
        }
        .tag-female {
            background-color: #fff0f6;
            color: #eb2f96;
            border-color: #ffadd2;
        }
        .empty-state {
            text-align: center;
            padding: 40px 0;
            color: #999;
        }
        .empty-state-icon {
            font-size: 48px;
            margin-bottom: 16px;
            color: #d9d9d9;
        }
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
</style>
