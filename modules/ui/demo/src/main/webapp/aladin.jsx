import '@lucuma-css/visualization.scss';
import '../../../node_modules/primereact/resources/primereact.min.css';
import '@lucuma-css/lucuma-ui-sequence.scss';
import '@lucuma-css/lucuma-ui-prime.scss';
import './rgl.scss';
import "./aladin.css";
import './aladinstyles.scss';

if (import.meta.env.DEV) {
  process.env = { CATS_EFFECT_TRACING_MODE: 'none' };
}

import { AladinDemo } from "@sjs/main.js";
AladinDemo.runIOApp()

if (import.meta.hot) {
  import.meta.hot.accept();
}
