export default {
  spec_dir: "spec",
  spec_files: [
      "../static/js/**/*.js",
      "../templates/**/*.js"

  ],
  helpers: [
    "helpers/**/*.?(m)js",
  ],
  env: {
    stopSpecOnExpectationFailure: false,
    random: true,
    forbidDuplicateNames: true
  }
}
