import httpService from '../utils/request';


export const createKnowledgeBase = async (data) => {
   try{
        const resdata =  await httpService.post('/api/process', data,{
            headers: { 
                'action': 'createKnowledgeBase', 
                'Content-Type': 'application/json' 
            } 
        });
        return resdata;
    }catch(error){
        console.log(error);
    }
    
 
};


export const loadKnowledgeBaseByPage = async (data) => {
    try{
        const resdata = await httpService.post('/api/process', data,{
            headers: { 
                'action': 'loadKnowledgeBaseByPage', 
                'Content-Type': 'application/json' 
            } 
        });    
        return resdata;
    }catch(error){
        console.log(error);
    }
};


export const loadFilesByKnowledgeBase = async (data) => {
    try {
        const resdata = await httpService.post('/api/process', data, {
            headers: {
                'action': 'loadFilesByKnowledgeBase',
                'Content-Type': 'application/json'
            }
        });
        return resdata;
    } catch (error) {
        console.log(error);
    }
};


export const deleteFile = async (data) => {
    try {
        const resdata = await httpService.post('/api/process', data, {
            headers: {
                'action': 'deleteFile',
                'Content-Type': 'application/json'
            }
        });
        return resdata;
    } catch (error) {
        console.log(error);
    }
};


export const uploadFilesToKB = async (knowledgeBaseId, files) => {
    try {
        const formData = new FormData();
        files.forEach(file => {
            formData.append('file', file.originFileObj || file);
        });
        const resdata = await httpService.post(
            `/api/process/file?knowledge_base_id=${knowledgeBaseId}`,
            formData,
            { headers: { 'Content-Type': 'multipart/form-data' } }
        );
        return resdata;
    } catch (error) {
        console.log(error);
    }
};