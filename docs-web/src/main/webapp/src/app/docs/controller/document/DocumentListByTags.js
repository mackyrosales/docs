/**
 * Documents by tags controller.
 */
angular.module('docs').controller('DocumentListByTags', function($scope, $http, $translate) {
    'use strict';

    $scope.tags = [];
    $scope.loading = true;

    /**
     * Load documents grouped by tags.
     */
    $scope.loadDocumentsByTags = function() {
        $scope.loading = true;
        
        $http.get('../api/document/by-tags').then(function(response) {
            $scope.tags = response.data.tags;
            $scope.loading = false;
        }, function(response) {
            console.error('Error loading documents by tags:', response);
            $scope.loading = false;
        });
    };

    // Load data on controller initialization
    $scope.loadDocumentsByTags();
}); 