import '../../../node_modules/primereact/resources/primereact.min.css';
import '../../../../css/target/lucuma-css/light-theme.css';
import './styles.scss';
import '../../../../ui/src/main/resources/lucuma-css/lucuma-ui-sequence.scss';
import '../../../../ui/src/main/resources/lucuma-css/lucuma-ui-prime.scss';

import { Demo } from "@sjs/main.js";
Demo.runIOApp()

if (import.meta.hot) {
  import.meta.hot.accept();
}
