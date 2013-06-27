module.exports = function(grunt) {

  // Configuration goes here
  grunt.initConfig({
    
    // Configure the copy task to move files from the development to production folders
    copy: {
      target: {
        files: [
        {expand: true,  src: ['./js/**'], dest: 'webdev/'},
        {expand: true,  src: ['./css/**'], dest: 'webdev/'},
        ]
      }
    },
  });

  // Load plugins here
  grunt.loadNpmTasks('grunt-contrib');

  // Define your tasks here
  grunt.registerTask('default', ['copy']);

};