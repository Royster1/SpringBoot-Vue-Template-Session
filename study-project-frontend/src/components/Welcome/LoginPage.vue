<template>
  <div style="text-align: center; padding: 0 35px;">
    <div style="font-size: 25px; color: black; margin-top: 150px; font-weight: bold">登录</div>
    <div style="font-size: 14px; color: #757575;margin-top: 3px">在进入系统之前请先输入用户名和密码进行登录</div>
    <div style="margin-top: 50px">
      <el-input v-model="form.username" type="text" placeholder="用户名/邮箱">
        <template #prefix>
          <el-icon><User /></el-icon>
        </template>
      </el-input>
      <el-input v-model="form.password" type="password" show-password placeholder="密码" style="margin-top: 15px">
        <template #prefix>
          <el-icon><Lock /></el-icon>
        </template>
      </el-input>
    </div>

    <el-row style="margin-top: 12px">
      <el-col :span="12" style="text-align: left">
        <el-checkbox v-model="form.remember" label="记住我" />
      </el-col>
      <el-col :span="12" style="text-align: right">
        <el-link style="font-size: 12px">忘记密码?</el-link>
      </el-col>
    </el-row>

    <div style="margin-top: 40px">
      <el-button @click="login"  style="width: 270px;" type="success" plain>立即登录</el-button>
    </div>
    <el-divider>
      <span style="color: #757575;font-size: 13px">没有账号</span>
    </el-divider>
    <div style="margin-top: 40px">
      <el-button style="width: 270px" @click="router.push('/register')" type="warning" plain>注册账号</el-button>
    </div>


  </div>
</template>

<script setup>
  import {User, Lock} from '@element-plus/icons-vue'
  import {reactive} from 'vue'
  import {ElMessage} from 'element-plus'
  import {post} from '../../net'
  import router from '../../router'

  const form = reactive({
    username: '',
    password: '',
    remember: false
  })

  const login = () => {
    if (!form.username || !form.password){
      ElMessage.warning('请填写用户名和密码! ')
    } else {
      post('/api/auth/login', {
        username: form.username,
        password: form.password,
        remember: form.remember
      },(message) => { // 登录成功
        ElMessage.success(message)
        router.push('/index')
      })
    }
  }

</script>

<style scoped>

</style>