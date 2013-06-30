module.exports = function(grunt) {
  // Configuration goes here
  grunt.initConfig({

    pkg: grunt.file.readJSON('package.json'),
    // load all grunt tasks
    
    // Configure the copy task to move files from the development to production folders
    copy: {
      target: {
        files: [
        {expand: true,  src: ['./js/**'], dest: 'webdev/'},
        {expand: true,  src: ['./img/**'], dest: 'webdev/'},
        {expand: true,  src: ['./img/**'], dest: 'prod/'},
        {expand: true,  src: ['./css/**'], dest: 'webdev/'},
        {expand: true,  src: ['./index.html'], dest: 'webdev/'},
        {expand: true,  src: ['./index.html'], dest: 'prod/'},
        {expand: true,  src: ['./nodice.js'], dest: 'webdev/'},
        {expand: true,  src: ['./*.txt'], dest: 'webdev/'},
        ]
      }
    },
    "useminPrepare": {
     html: 'prod/index.html',
     options: {
      dest: 'prod/'
    }
  },
  uglify: {
   dist: {
    files: [{
      src: ["nodice.js", "js/**/*.js"],
      dest: "prod/<%= pkg.name + '-' + pkg.version %>.js"
    }]
  }
},
cssmin: {
 combine: {
  files: {
    "prod/<%= pkg.name + '-' + pkg.version %>.css": [ './css/normalize.css','./css/foundation.css']
  }
}
},
"usemin": {
  html: ['*.html'],
  css: ['*.css'],
  options: {
    dirs: ['prod/']
  }
}


});
  // Load plugins here
  grunt.loadNpmTasks('grunt-usemin');
  grunt.loadNpmTasks('grunt-contrib');

  // Define your tasks here
  grunt.registerTask('default', ['copy', "useminPrepare",'uglify', "cssmin", "usemin"]);

};