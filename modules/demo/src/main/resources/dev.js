import "primeicons/primeicons.css"
import "primereact/resources/themes/lara-dark-blue/theme.css"
import "primereact/resources/primereact.min.css"
import "./styles.css";
import App from "sjs/demo-fastopt.js";

if (module.hot) {
  module.hot.accept();
  App.Demo.runIOApp();
}
