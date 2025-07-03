'use strict';

/**
 * OCR ETA page controller.
 */
angular.module('docs').controller('OcrEtaPage', function($scope, $rootScope, $sce, $timeout) {
  // Set page title
  $rootScope.pageTitle = 'OCR - Form Extraction';
  
  // Set loading state
  $scope.loading = true;
  
  // OCR ETA iframe URL
  $scope.ocrEtaUrl = $sce.trustAsResourceUrl('https://cube-ai.cubeworks.com.ph/form-extraction');
  
  // Function to load the OCR ETA iframe
  $scope.loadOcrEta = function() {
    console.log('Loading OCR ETA iframe...');
    
    // Set a timeout to hide loading indicator if iframe onload doesn't trigger
    $timeout(function() {
      $scope.loading = false;
    }, 5000);
  };
  
  // Handle iframe load event
  $scope.iframeLoaded = function() {
    $scope.loading = false;
  };
  
  // Refresh OCR iframe
  $scope.refreshOcr = function() {
    $scope.loading = true;
    var iframe = document.querySelector('.ocr-iframe');
    iframe.src = iframe.src; // Reload the iframe
  };
});
