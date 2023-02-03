import Vue from 'vue'
import App from './App.vue'
import router from './router'

import Col from 'ant-design-vue/lib/col';
import 'ant-design-vue/lib/col/style/css';
Vue.use(Col);

import Row from 'ant-design-vue/lib/row';
import 'ant-design-vue/lib/row/style/css';
Vue.use(Row);

import Form from 'ant-design-vue/lib/form'
import 'ant-design-vue/lib/form/style/css';
Vue.use(Form);

import Input from 'ant-design-vue/lib/input'
import 'ant-design-vue/lib/input/style/css';
Vue.use(Input);

import Button from 'ant-design-vue/lib/button';
import 'ant-design-vue/lib/button/style/css';
Vue.use(Button)


Vue.config.productionTip = false

new Vue({
  beforeCreate: function() {
    if (!String.format) {
      String.format = function(format) {
        var args = Array.prototype.slice.call(arguments, 1);
        return format.replace(/{(\d+)}/g, function(match, number) {
          return typeof args[number] != 'undefined'
              ? args[number]
              : match
              ;
        });
      };
    }
  },
  router,
  render: h => h(App),
}).$mount('#app')
