import Vue from 'vue'
import Router from 'vue-router'
import Home from './components/HelloWorld.vue'

Vue.use(Router);

export default new Router({
    mode: 'history',
    base: process.env.BASE_URL,
    routes: [
        {
            path: '/',
            name: 'home',
            component: Home
        },
        {
            path: '/departments',
            name: 'department-list',
            component: () => import(/* webpackChunkName: "about" */ './components/DepartmentList.vue')
        },
        {
            path: '/departments/:name',
            name: 'department',
            component: () => import(/* webpackChunkName: "about" */ './components/Department.vue')
        }
    ]
})