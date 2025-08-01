<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row :gutter="15">
          <div :class="advanced ? null: 'fold'">
            <a-col :md="6" :sm="24">
              <a-form-item
                label="用户昵称"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.username"/>
              </a-form-item>
            </a-col>
<!--            <a-col :md="6" :sm="24">-->
<!--              <a-form-item-->
<!--                label="账户状态"-->
<!--                :labelCol="{span: 5}"-->
<!--                :wrapperCol="{span: 18, offset: 1}">-->
<!--                <a-select v-model="queryParams.status" allowClear>-->
<!--                  <a-select-option value="0">锁定</a-select-option>-->
<!--                  <a-select-option value="1">有效</a-select-option>-->
<!--                </a-select>-->
<!--              </a-form-item>-->
<!--            </a-col>-->
          </div>
          <span style="float: right; margin-top: 3px;">
            <a-button type="primary" @click="search">查询</a-button>
            <a-button style="margin-left: 8px" @click="reset">重置</a-button>
          </span>
        </a-row>
      </a-form>
    </div>
    <div>
      <div class="operator">
        <!--        <a-button type="primary" ghost @click="add">新增</a-button>-->
<!--        <a-button @click="batchDelete">删除</a-button>-->
      </div>
      <!-- 表格区域 -->
      <a-table ref="TableInfo"
               :columns="columns"
               :rowKey="record => record.userId"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               :scroll="{ x: 900 }"
               @change="handleTableChange">
        <template slot="avatarShow" slot-scope="text, record">
          <template>
            <img alt="头像" :src="'static/avatar/' + text">
          </template>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-icon v-if="record.status == 0" type="caret-up" @click="audit(record.userId, 1)" title="up" style="margin-right: 10px"></a-icon>
          <a-icon v-if="record.status == 1" type="caret-down" @click="audit(record.userId, 0)" title="down" style="margin-right: 10px"></a-icon>
        </template>
      </a-table>
    </div>
    <user-view
      @close="handleUserViewClose"
      :userShow="userView.visiable"
      :userData="userView.data">
    </user-view>
  </a-card>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import {mapState} from 'vuex'
import moment from 'moment'
import UserView from './UserView'
moment.locale('zh-cn')

export default {
  name: 'User',
  components: {UserView, RangeDate},
  data () {
    return {
      userView: {
        visiable: false,
        data: null
      },
      advanced: false,
      queryParams: {},
      filteredInfo: null,
      sortedInfo: null,
      paginationInfo: null,
      dataSource: [],
      selectedRowKeys: [],
      loading: false,
      pagination: {
        pageSizeOptions: ['10', '20', '30', '40', '100'],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      },
      userList: []
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    columns () {
      return [{
        title: '用户编号',
        dataIndex: 'code',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        }
      }, {
        title: '用户昵称',
        dataIndex: 'name',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        }
      }, {
        title: '密码',
        dataIndex: 'password',
        customRender: (text, row, index) => {
          return '******'
        }
      }, {
        title: '性别',
        dataIndex: 'sex',
        customRender: (text, row, index) => {
          switch (text) {
            case 1:
              return <a-tag>男</a-tag>
            case 2:
              return <a-tag>女</a-tag>
            default:
              return '- -'
          }
        }
      }, {
        title: '头像',
        dataIndex: 'images',
        customRender: (text, record, index) => {
          if (!record.images) return <a-avatar shape="square" icon="user" />
          return <a-popover>
            <template slot="content">
              <a-avatar shape="square" size={132} icon="user" src={ 'http://127.0.0.1:9527/imagesWeb/' + record.images } />
            </template>
            <a-avatar shape="square" icon="user" src={ 'http://127.0.0.1:9527/imagesWeb/' + record.images } />
          </a-popover>
        }
      }, {
        title: '创建时间',
        dataIndex: 'createTime'
      }]
    }
  },
  mounted () {
    this.fetch()
  },
  methods: {
    audit (userId, flag) {
      this.$post('/cos/post-info/user/audit', {userId, flag}).then((r) => {
        this.$message.success('修改成功！')
        this.fetch()
      })
    },
    view (row) {
      this.userView.data = row
      this.userView.visiable = true
    },
    handleUserViewClose () {
      this.userView.visiable = false
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    toggleAdvanced () {
      this.advanced = !this.advanced
    },
    handleDeptChange (value) {
      this.queryParams.deptId = value || ''
    },
    batchDelete () {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let ids = that.selectedRowKeys.join(',')
          that.$delete('/cos/user-info/' + ids).then(() => {
            that.$message.success('删除成功')
            that.selectedRowKeys = []
            that.search()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    search () {
      let {sortedInfo, filteredInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.fetch({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams,
        ...filteredInfo
      })
    },
    reset () {
      // 取消选中
      this.selectedRowKeys = []
      // 重置分页
      this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent
      if (this.paginationInfo) {
        this.paginationInfo.current = this.pagination.defaultCurrent
        this.paginationInfo.pageSize = this.pagination.defaultPageSize
      }
      // 重置列过滤器规则
      this.filteredInfo = null
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.queryParams = {}
      this.fetch()
    },
    handleTableChange (pagination, filters, sorter) {
      // 将这三个参数赋值给Vue data，用于后续使用
      this.paginationInfo = pagination
      this.filteredInfo = filters
      this.sortedInfo = sorter

      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      })
    },
    fetch (params = {}) {
      // 显示loading
      this.loading = true
      if (this.paginationInfo) {
        // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
        this.$refs.TableInfo.pagination.current = this.paginationInfo.current
        this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize
        params.size = this.paginationInfo.pageSize
        params.current = this.paginationInfo.current
      } else {
        // 如果分页信息为空，则设置为默认值
        params.size = this.pagination.defaultPageSize
        params.current = this.pagination.defaultCurrent
      }
      if (params.status === undefined) {
        delete params.status
      }
      this.$get('/cos/post-info/user/page', {
        ...params
      }).then((r) => {
        let data = r.data.data
        const pagination = {...this.pagination}
        pagination.total = data.total
        this.dataSource = data.records
        this.pagination = pagination
        // 数据加载完毕，关闭loading
        this.loading = false
      })
    }
  },
  watch: {}
}
</script>
<style lang="less" scoped>
@import "../../../../static/less/Common";
</style>
