<template>
  <div class="container">
    <div class="content">
      <div class="top">
        <div class="header">
          <a-row style="width: 368px;margin: 0 auto">
            <a-col :span="24">
              <div style="text-align: left;font-size: 23px;margin-bottom: 10px;font-family: SimHei;margin-top: 30px;color: #7a8fab;text-align: center">老年大学社区服务融合平台</div>
            </a-col>
          </a-row>
        </div>
        <div class="desc"></div>
      </div>
      <component :is="componentName" @regist="handleRegist" class="main-content" style="margin-top: 50px;"></component>
    </div>
    <global-footer :copyright="copyright" />
  </div>
</template>

<script>
import GlobalFooter from '../common/GlobalFooter'
import Login from './Login'
import Regist from './Regist'

export default {
  name: 'Common',
  components: {GlobalFooter, Login, Regist},
  data () {
    return {
      componentName: 'Login'
    }
  },
  computed: {
    systemName () {
      return this.$store.state.setting.systemName
    },
    copyright () {
      return this.$store.state.setting.copyright
    }
  },
  methods: {
    handleRegist (val) {
      this.componentName = val
    }
  }
}
</script>

<style lang="less" scoped>
  .container {
    display: flex;
    flex-direction: column;
    height: 100vh;
    overflow: auto;
    background: rgb(234, 234, 234) url(../../../static/img/house.png) no-repeat center ;
    background-size: cover;
    background-color: white;
    //background-color: #d9d9d6;
    //background-repeat: repeat-x, repeat;
    .content {
      padding: 32px 0;
      flex: 1;
      @media (min-width: 768px){
        padding: 116px 0 10px;
      }
      .top {
        text-align: center;
        .header {
          height: 50px;
          line-height: 50px;
          a {
            text-decoration: none;
          }
          .logo {
            width: 40px;
            height: 19px;
            vertical-align: center;
            margin-right: 16px;
          }
          .title {
            font-size: 28px;
            color: rgba(0,0,0,.85);
            font-family: 'Myriad Pro', 'Helvetica Neue', Arial, Helvetica, sans-serif;
            font-weight: 600;
            position: relative;
            top: 6px;
          }
        }
        .desc {
          font-size: 14px;
          color: rgba(0,0,0,.45);
          margin-top: 12px;
          margin-bottom: 40px;
        }
      }
      .main-content {
        width: 368px;
        margin: 0 auto;
        @media screen and (max-width: 576px) {
          width: 95%;
        }
        @media screen and (max-width: 320px) {
          .captcha-button{
            font-size: 14px;
          }
        }
      }
    }
  }
</style>
