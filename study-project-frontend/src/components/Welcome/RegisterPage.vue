<template>
  <div style="text-align: center; padding: 0 35px;">
    <div style="font-size: 25px; color: black; margin-top: 150px; font-weight: bold">注册新用户</div>
    <div style="font-size: 14px; color: #757575;margin-top: 3px">欢迎注册我们的学习平台,请在下方填写相关信息</div>
    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef"> <!--Element内置表单校验-->
        <el-form-item prop="username">
          <el-input v-model="form.username" :maxlength="8" type="text" placeholder="用户名">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" type="email" placeholder="电子邮件地址">
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-row gutter="15">
            <el-col :span="16">
              <el-input v-model="form.code" :maxlength="6" type="text" placeholder="请输入验证码">
                <template #prefix>
                  <el-icon><EditPen /></el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="6">
              <el-button type="success"  @click="validateEmail"
                         :disabled="!isEmailValid || coldTime > 0">
                {{coldTime > 0 ? '请稍后' + coldTime + '秒' : '获取验证码'}}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" :maxlength="16" type="password" placeholder="密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password_repeat">
          <el-input v-model="form.password_repeat" :maxlength="16" type="password" placeholder="重复密码" >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
      </el-form>

    </div>
    <div style="margin-top: 50px">
      <el-button style="width: 270px" type="warning" plain @click="register">
        立即注册
      </el-button>
    </div>
    <div style="margin-top: 20px">
      <span style="font-size: 14px;line-height: 15px;color: #757575">已有账号? </span>
      <el-link type="primary" @click="router.push('/')">立即登录</el-link>
    </div>
  </div>

</template>

<script setup>
import {User, Lock, Message, EditPen} from '@element-plus/icons-vue'
import router from '../../router'
import {reactive, ref} from 'vue'
import {ElMessage} from 'element-plus'
import {post} from '../../net'

const form = reactive({
  username: '',
  password: '',
  password_repeat: '',
  email: '',
  code: ''
})

// 自定义表单校验
const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'))
  } else if (!/^[\u4e00-\u9fa5a-zA-Z0-9]+$/.test(value)) {
    callback(new Error('用户名不能包含特殊字符, 只能是中文/英文'))
  } else {
    callback()
  }
}
// 重复密码
const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error("两次输入的密码不一致!"))
  } else {
    callback()
  }
}

const rules = {
  username: [
    {validator: validateUsername, trigger: ['blur', 'change']},
    {min: 2, max: 8, message: '用户名的长度必须在2-8个字符之间', trigger: ['blur', 'change']},
  ],
  password: [
    {required: true, message: '请输入密码', trigger: ['blur', 'change']},
    {min: 6, max: 16, message: '用户名的长度必须在6-16个字符之间', trigger: ['blur', 'change']}
  ],
  password_repeat: [
    {validator: validatePassword, trigger: ['blur', 'change']},
  ],
  email: [
    {required: true, message: '请输入邮件地址', trigger: ['blur', 'change']},
    {type: 'email',
      message: '请输入合法的电子邮件地址',
      trigger: ['blur', 'change']}
  ],
  code: [
    {required: true, message: '请输入验证码', trigger: ['blur', 'change']}
  ]
}
// 邮箱必须正确才能获取验证码
const isEmailValid = ref(false) // 默认是false
const onValidate = (prop, isValid) => { // 任一表单项被校验后触发
  if (prop === 'email') {
    isEmailValid.value = isValid
  }
}

// 立即注册校验所有表单
const formRef = ref()
const register = () => {
  formRef.value.validate((isValid) => {
    if (isValid) {
      // 发送请求
      post('/api/auth/register',{
        username: form.username,
        password: form.password,
        email: form.email,
        code: form.code
      }, (message) => {
          ElMessage.success(message)
          router.push('/') // 登录界面
      })
    } else {
      ElMessage.warning('请完整填写注册表单内容! ')
    }
  })
}

const validateEmail = () => {
  coldTime.value = 60
  post('/api/auth/valid-register-email', {
    email: form.email
  }, (message) => {
    ElMessage.success(message)
    setInterval(() => coldTime.value--, 1000)
  }, (message) => {
    ElMessage.warning(message)
    coldTime.value = 0
  })
}

// 获取验证码60s不能点击
const coldTime = ref(0)

</script>
<style scoped>
</style>