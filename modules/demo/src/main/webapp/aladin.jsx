import "./aladin.css";
import '../../../../ui/src/main/resources/lucuma-css/visualization.scss';
import './rgl.scss';

if (import.meta.env.DEV) {
  process.env = { CATS_EFFECT_TRACING_MODE: 'none' };
}

import { AladinDemo } from "@sjs/main.js";
AladinDemo.runIOApp()

if (import.meta.hot) {
  import.meta.hot.accept();
}
