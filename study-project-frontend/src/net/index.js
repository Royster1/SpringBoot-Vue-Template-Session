import axios from 'axios';
import {ElMessage} from 'element-plus';

const defaultError = () => ElMessage.error('发生了一些错误, 请联系管理员')
const defaultFailure = (message) => ElMessage.warning(message)

function post(url, data, success, failure = defaultFailure, error = defaultError){
    axios.post(url, data, {
        headers: {
            'Content-Type' : 'application/x-www-form-urlencoded'
        },
        withCredentials: true // 发起请求是否携带cookie
    }).then(({data}) => {
        if (data.success)
            success(data.message, data.status)
        else
            failure(data.message, data.status)
    }).catch(error)
}


function get(url, success, error = defaultError, failure = defaultFailure){
    axios.post(url, {
        withCredentials: true // 发起请求是否携带cookie
    }).then(({data}) => {
        if (data.success)
            success(data.message, data.status)
        else
            failure(data.message, data.status)
    }).catch(error)
}

// 暴露
export {get, post}