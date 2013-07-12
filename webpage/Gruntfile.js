module.exports = function(grunt) {
  // Configuration goes here
  grunt.initConfig({

    pkg: grunt.file.readJSON('package.json'),
    // load all grunt tasks
    
    // Configure the copy task to move files from the development to production folders
    gitinfo: {

    },
    copy: {
      target: {
        files: [
        {expand: true,  src: ['./js/**'], dest: 'webdev/'},
        {expand: true,  src: ['./js/**'], dest: 'prod/'},
        {expand: true,  src: ['./img/**'], dest: 'webdev/'},
        {expand: true,  src: ['./**.png'], dest: 'prod/'},
        {expand: true,  src: ['./**.png'], dest: 'webdev/'},
        {expand: true,  src: ['./img/**'], dest: 'prod/'},
        {expand: true,  src: ['./css/**'], dest: 'webdev/'},
        {expand: true,  src: ['./*.html'], dest: 'webdev/'},
        {expand: true,  src: ['./*.html'], dest: 'prod/'},
        {expand: true,  src: ['./favicon.ico'], dest: 'webdev/'},
        {expand: true,  src: ['./favicon.ico'], dest: 'prod/'},
        {expand: true,  src: ['./*.txt'], dest: "webdev/"},
        {expand: true,  src: ['./*.txt'], dest: "prod/"},
        {expand: true,  src: ['./*.xml'], dest: "webdev/"},
        {expand: true,  src: ['./*.xml'], dest: "prod/"},
        ]
      }, 
      localwebserver: {
        files: [
        {expand: true, src: ["prod/**"], dest: "/usr/local/Cellar/nginx/1.2.8/html/"}
        ]
      }
    },
    jshint: {
      all: ['Gruntfile.js', "js/app.js"]
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
      src: ["js/**/*.js"],
      dest: "prod/diceapp.js"
    }]
  }
},
cssmin: {
 combine: {
  files: {
    "prod/app.css": [ './css/normalize.css','./css/foundation.css']
  }
}
},
"usemin": {
  html: ['prod/*.html'],
  css: ['*.css'],
  options: {
    dirs: ['prod/']
  }
},
htmlmin: {
  dist: {
    options: {
      removeCommentsFromCDATA: true,
      collapseWhitespace: true,
      collapseBooleanAttributes: true,
      removeAttributeQuotes: true,
      removeRedundantAttributes: true,
      useShortDoctype: true,
      removeEmptyAttributes: true,
      removeOptionalTags: true
    },
    files: [{
      expand: true,
      cwd: '.',
      src: '*.html',
      dest: 'prod'
    }]
  }
},
shell: {
  www: {
    command: "cp -r prod/ www/"
  },
  deploy: {
    command: 'tar -czf <%= gitinfo.local.branch.current.shortSHA %>.tar.gz ./www/'
  },
  copyover: {
    command: 'scp -i $SCPKEY <%= gitinfo.local.branch.current.shortSHA %>.tar.gz ubuntu@46.137.77.80:'
  }
},

});
  // Load plugins here
  grunt.loadNpmTasks('grunt-gitinfo');
  grunt.loadNpmTasks('grunt-shell');
  grunt.loadNpmTasks('grunt-contrib-compress');
  grunt.loadNpmTasks('grunt-contrib-htmlmin');
  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-usemin');
  grunt.loadNpmTasks('grunt-contrib');

  // Define your tasks here
  grunt.registerTask('default', ['gitinfo','copy:target', "jshint","useminPrepare",'uglify', "cssmin", "usemin", "shell:www", "shell:deploy", "shell:copyover", "copy:localwebserver"]);

};