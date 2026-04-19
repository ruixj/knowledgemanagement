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