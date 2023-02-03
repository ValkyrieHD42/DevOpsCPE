module.exports = {
  presets: [
    '@vue/cli-plugin-babel/preset'
  ],
  "plugins": [
      "transform-vue-jsx",
      ["import", { "libraryName": "ant-design-vue", "libraryDirectory": "es", "style": "css" }]
   ]
}
