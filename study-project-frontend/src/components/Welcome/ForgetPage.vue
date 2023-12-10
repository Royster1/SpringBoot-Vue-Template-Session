<template>
  <div>
    <div style="margin: 30px 20px">
      <el-steps :active="active" finish-status="success" align-center>
        <el-step title="验证电子邮件" />
        <el-step title="重新设定密码" />
      </el-steps>
    </div>
    <transition name="el-fade-in-linear" mode="out-in">
      <div style="text-align: center; padding: 0 35px; height: 100%" v-if="active === 0">
        <div style="margin-top: 80px">
          <div style="font-size: 25px; color: black;  font-weight: bold">重置密码</div>
          <div style="font-size: 14px; color: #757575;margin-top: 3px">请输入需要重置密码的电子邮件地址</div>
        </div>
        <div style="margin-top: 50px">
          <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef"> <!--Element内置表单校验-->
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
          </el-form>
        </div>
        <div style="margin-top: 65px">
          <el-button @click="startReset()" style="width: 270px;" type="danger" plain>开始重置密码</el-button>
        </div>
      </div>
    </transition>
    <transition name="el-fade-in-linear" mode="out-in">
      <div style="text-align: center; padding: 0 35px; height: 100%" v-if="active === 1">
        <div style="margin-top: 80px">
          <div style="font-size: 25px; color: black;  font-weight: bold">重置密码</div>
          <div style="font-size: 14px; color: #757575;margin-top: 3px">请填写您的新密码, 请务必牢记, 防止丢失</div>
        </div>

        <div style="margin-top: 50px">
          <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef"> <!--Element内置表单校验-->
            <el-form-item prop="password">
              <el-input v-model="form.password" :maxlength="16" type="password" placeholder="新密码">
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="password_repeat">
              <el-input v-model="form.password_repeat" :maxlength="16" type="password" placeholder="重复新密码" >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <div style="margin-top: 65px">
          <el-button @click="doReset()" style="width: 270px;" type="danger" plain>立即重置密码</el-button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
  import {reactive, ref} from 'vue'
  import {Lock, Message, EditPen} from '@element-plus/icons-vue'
  import {post} from '../../net'
  import {ElMessage} from 'element-plus'
  import router from '../../router'

  const active = ref(0);

  const form = reactive({
    email: '',
    code: '',
    password: '',
    password_repeat: '',
  })

  const rules = {
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

  // 获取验证码60s不能点击
  const coldTime = ref(0)
  // 邮箱必须正确才能获取验证码
  const isEmailValid = ref(false) // 默认是false
  // 立即注册校验所有表单
  const formRef = ref()

  const onValidate = (prop, isValid) => { // 任一表单项被校验后触发
    if (prop === 'email') {
      isEmailValid.value = isValid
    }
  }

  const validateEmail = () => {
    coldTime.value = 60
    post('/api/auth/valid-reset-email', {
      email: form.email
    }, (message) => {
      ElMessage.success(message)
      setInterval(() => coldTime.value--, 1000)
    }, (message) => {
      ElMessage.warning(message)
      coldTime.value = 0
    })
  }

//  开始重置密码
  const startReset = () => {
    formRef.value.validate((isValid) => {
      if (isValid) {
        // 发送请求
        post('/api/auth/start-reset', {
          email: form.email,
          code: form.code
        }, ()=>{
          active.value++
        })
      } else {
        ElMessage.warning('请填写电子邮箱地址和验证码 ')
      }
    })
  }

  // 重置密码操作
  const doReset = () => {
    formRef.value.validate((isValid) => {
      if (isValid) {
        // 发送请求
        post('/api/auth/do-reset', {
          password: form.password
        }, (message)=>{
          ElMessage.success(message)
          router.push('/')
        })
      } else {
        ElMessage.warning('请填写新的密码 ')
      }
    })
  }


</script>

<style scoped>

</style>