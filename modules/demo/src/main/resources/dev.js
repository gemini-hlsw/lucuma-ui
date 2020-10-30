import "./theme/semantic.less";
import App from "sjs/demo-fastopt.js";

if (module.hot) {
  module.hot.accept();
  App.Demo.runIOApp();
}
