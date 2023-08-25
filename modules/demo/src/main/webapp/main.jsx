import "./styles.css";

import { Demo } from "@sjs/main.js";
Demo.runIOApp()

if (import.meta.hot) {
  import.meta.hot.accept();
}
