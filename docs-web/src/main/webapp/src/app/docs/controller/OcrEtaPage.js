'use strict';

/**
 * OCR ETA page controller.
 */
angular.module('docs').controller('OcrEtaPage', function($scope, $rootScope, $sce) {
  // Set page title
  $rootScope.pageTitle = 'OCR - ETA';
  
  // OCR ETA iframe URL
  $scope.ocrEtaUrl = $sce.trustAsResourceUrl('https://cube-ai.cubeworks.com.ph/form-extraction');
  
  // Custom functionality can be added here
  $scope.loadOcrEta = function() {
    // Additional functionality when loading the iframe (if needed)
    console.log('Loading OCR ETA iframe...');
  };
});
