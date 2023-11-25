<template>
  <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">
    <div style="flex: 1">
      <el-image @dragstart.prevent style="width: 100%;height: 100%;" fit="cover"
                src="http://img.netbian.com/file/2023/0714/191543TnXnr.jpg"></el-image>
    </div>
    <div class="welcome-title">
      <div style="font-size: 30px;font-weight: bold">欢迎来到我们的学习平台</div>
      <div style="margin-top: 10px">在这里你可以学习Java整套系列, 如何搭建网站</div>
      <div style="margin-top: 5px">0 基础也能轻松入门Java</div>
    </div>
    <div style="width: 400px; z-index: 1">
      <router-view v-slot:="{ Component}">
        <transition name="el-fade-in-linear" mode="out-in">
          <component :is="Component"/>
        </transition>
      </router-view>
    </div>
  </div>
</template>

<script setup>

</script>

<style scoped>
.welcome-title{
  position: absolute;
  bottom: 30px;
  left: 30px;
  color: white;
  text-shadow: 0 0 10px black;
}
</style>