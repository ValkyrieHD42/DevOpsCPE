<template>
  <div class="department">
    <a-row type="flex" justify="center">
      <a-col span="24">
        <h1>Department: {{ name }}</h1>
      </a-col>

      <a-col span="24">
        <h2>Students:</h2>
      </a-col>

      <a-col :xs="24" :sm="22" :md="12" :lg="8" :xl="5">
        <a-row class="student" v-for="student in students" :key="student.id">
          <a-col span="24">{{ student.firstname }} - {{ student.lastname }}</a-col>
        </a-row>
      </a-col>

      <a-col span="24">
          <a-row type="flex" justify="center">
          <div class="hr" />
          </a-row>
      </a-col>

      <a-col span="24">
        <a-form layout="inline">
          <a-form-item>
            <a-input placeholder="first name" v-model="firstname"></a-input>
          </a-form-item>
          <a-form-item>
            <a-input placeholder="last name" v-model="lastname"></a-input>
          </a-form-item>
          <br />
          <a-form-item>
            <a-button @click="addStudent" type="primary">add</a-button>
          </a-form-item>
        </a-form>
      </a-col>
    </a-row>
  </div>
</template>

<script>
export default {
  name: "Department",
  data: function() {
    return {
      firstname: "",
      lastname: "",
      students: [],
      currentDepartment: {}
    };
  },
  computed: {
    name() {
      return this.$route.params.name;
    }
  },
  mounted: function() {
    if (this.name) {
      fetch(`http://${process.env.VUE_APP_API_URL}/departments/${this.name}/students`)
        .then(response => response.json())
        .then(data => (this.students = data));
    }
    // get department id
    fetch(`http://${process.env.VUE_APP_API_URL}/departments/${this.name}`)
      .then(response => response.json())
      .then(data => (this.currentDepartment = data));
  },
  methods: {
    async addStudent() {
      await fetch(`http://${process.env.VUE_APP_API_URL}/students/`, {
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          firstname: this.firstname,
          lastname: this.lastname,
          department: {
            id: this.currentDepartment.id,
            name: this.currentDepartment.name
          }
        })
      });
      fetch(`http://${process.env.VUE_APP_API_URL}/departments/${this.name}/students`)
        .then(response => response.json())
        .then(data => (this.students = data));
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.hr {
    margin-top: 5px;
    margin-bottom: 5px;
    height: 1px;
    width: 70%;
    background-color: #2323232c;
}
</style>