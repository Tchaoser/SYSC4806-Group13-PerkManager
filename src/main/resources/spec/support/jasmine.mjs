export default {
  spec_dir: "spec",
  spec_files: [
      "../static/**/*.js",
      "../templates/**/*.js"

  ],
  helpers: [
    "helpers/**/*.?(m)js",
      "support/dom-setup.js"
  ],
  env: {
    stopSpecOnExpectationFailure: false,
    random: true,
    forbidDuplicateNames: true
  }
}
