import react from "@vitejs/plugin-react";
import path from "path";
import env from 'vite-plugin-env-compatible';

const scalaVersion = '3.7.3';

// https://vitejs.dev/config/
export default ({ command, mode }) => {
  const sjs =
    mode == "production"
      ? path.resolve(__dirname, `target/scala-${scalaVersion}/demo-opt/`)
      : path.resolve(__dirname, `target/scala-${scalaVersion}/demo-fastopt/`);
  return {
    root: "src/main/webapp",
    resolve: {
      alias: [
        {
          find: 'process',
          replacement: 'process/browser',
        },
        {
          find: "@sjs",
          replacement: sjs,
        },
        {
          find: "@lucuma-css",
          replacement: path.resolve(__dirname, "target/lucuma-css/"),
        },
      ],
    },
    server: {
      host: '0.0.0.0',
      allowedHosts: ['local.lucuma.xyz'],
      watch: {
        ignored: [
          function ignoreThisPath(_path) {
            const sjsIgnored =
              _path.includes("/target/stream") ||
              _path.includes("/zinc/") ||
              _path.includes("/classes");
            return sjsIgnored;
          },
        ],
      },
    },
    build: {
      minify: 'terser',
      // minify: false,
      terserOptions: {
        // sourceMap: false,
        toplevel: true
      },
      outDir: path.resolve(__dirname, "../docs"),
    },
    plugins: [env(), react()],
  };
};
