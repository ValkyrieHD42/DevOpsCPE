<template>
  <div class="department-list">
    <a-row type="flex" justify="center">
      <a-col :xs="24" :sm="22" :md="12" :lg="8" :xl="5">
        <a-row class="department" v-for="department in departments" :key="department.id">
          <router-link :to="`/departments/${department.name}`">
            <a-col :span="12">{{ department.id }}</a-col>
            <a-col :span="12">{{ department.name }}</a-col>
          </router-link>
        </a-row>
      </a-col>
    </a-row>
  </div>
</template>

<script>
export default {
  name: "DepartmentList",
  props: {
    msg: String
  },
  data: function() {
    return {
      departments: []
    };
  },
  mounted: function() {
    fetch(`http://${process.env.VUE_APP_API_URL}/departments/`)
      .then(response => response.json())
      .then(data => (this.departments = data));
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
a {
  color: #232323;
}
a:hover {
  color: #232323;
}
.department:hover {
  background-color: #face53;
}
</style>