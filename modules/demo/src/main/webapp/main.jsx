import '../../../node_modules/primereact/resources/primereact.min.css';
import './styles.scss';
import '../../../../ui/src/main/resources/lucuma-css/lucuma-ui-sequence.scss';
import '../../../../ui/src/main/resources/lucuma-css/lucuma-ui-prime.scss';

if (import.meta.env.DEV) {
  process.env = { CATS_EFFECT_TRACING_MODE: 'none' };
}

import { Demo } from "@sjs/main.js";
Demo.runIOApp()

if (import.meta.hot) {
  import.meta.hot.accept();
}
