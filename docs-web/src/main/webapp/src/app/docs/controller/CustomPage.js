'use strict';

/**
 * Custom page controller.
 */
angular.module('docs').controller('CustomPage', function($scope, $rootScope) {
  // Set page title
  $rootScope.pageTitle = 'Custom Page';
  
  // Custom page variables
  $scope.pageTitle = 'Welcome to our Custom Page';
  $scope.pageDescription = 'This is a beautifully designed custom page with a violet theme. You can customize this content however you like!';
  
  // Custom functionality can be added here
  $scope.customAction = function() {
    alert('Custom action triggered!');
  };
});
