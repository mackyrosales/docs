'use strict';

/**
 * Filter to sum values by a specific key in an array of objects.
 */
angular.module('docs').filter('sumByKey', function() {
  return function(data, key) {
    if (!data || !key) return 0;
    var sum = 0;
    angular.forEach(data, function(item) {
      sum += item[key] || 0;
    });
    return sum;
  };
});

/**
 * Dashboard page controller.
 */
angular.module('docs').controller('CustomPage', function($scope, $rootScope, $q, $filter, Restangular, User) {
  // Set page title
  $rootScope.pageTitle = 'Dashboard';
  
  // Dashboard variables
  $scope.pageTitle = 'Document Management Dashboard';
  $scope.pageDescription = 'Welcome to your centralized document management dashboard. View statistics, recent documents, and system performance at a glance.';
  
  // Initialize stats
  $scope.stats = {
    totalDocuments: 0,
    totalStorage: '0 B',
    processedToday: 0,
    pendingReviews: 0
  };
  
  // Initialize document type distribution
  $scope.documentTypeData = [];
  
  // Initialize recent activity
  $scope.recentActivity = [];
  
  // Initialize processing status
  $scope.processingStatus = {
    completed: 0,
    inProgress: 0,
    queued: 0
  };
  
  /**
   * Format file size for display.
   */
  function formatFileSize(size) {
    if (size < 1024) return size + ' B';
    else if (size < 1048576) return Math.round(size / 1024) + ' KB';
    else if (size < 1073741824) return (size / 1048576).toFixed(1) + ' MB';
    else return (size / 1073741824).toFixed(1) + ' GB';
  }
  
  /**
   * Load dashboard data.
   */
  function loadDashboardData() {
    // Get total documents count and statistics
    Restangular.one('document/list')
      .get({
        limit: 0,
        sort_column: 3,
        asc: false
      })
      .then(function(data) {
        $scope.stats.totalDocuments = data.total;
        
        // Process document types if documents are available
        if (data.total > 0 && data.documents.length > 0) {
          var mimeTypes = {};
          
          // Count different document types
          angular.forEach(data.documents, function(doc) {
            if (doc.files && doc.files.length > 0) {
              angular.forEach(doc.files, function(file) {
                var type = file.mimetype.split('/')[0];
                var subType = file.mimetype.split('/')[1];
                
                // Categorize by general type
                if (type === 'application') {
                  if (subType === 'pdf') {
                    type = 'PDF';
                  } else if (subType.indexOf('word') > -1 || subType.indexOf('document') > -1 || subType.indexOf('doc') > -1) {
                    type = 'Word';
                  } else if (subType.indexOf('xls') > -1 || subType.indexOf('sheet') > -1) {
                    type = 'Excel';
                  } else {
                    type = 'Other';
                  }
                } else if (type === 'image') {
                  type = 'Images';
                } else if (type === 'text') {
                  type = 'Text';
                }
                
                if (!mimeTypes[type]) {
                  mimeTypes[type] = 1;
                } else {
                  mimeTypes[type]++;
                }
              });
            }
          });
          
          // Convert to array for chart
          var typeArray = [];
          angular.forEach(mimeTypes, function(count, type) {
            typeArray.push({ label: type, value: count });
          });
          
          $scope.documentTypeData = typeArray;
        } else {
          // Default document type data if no documents available
          $scope.documentTypeData = [
            { label: 'No Documents', value: 100 }
          ];
        }
      });
    
    // Get storage information from user info
    User.userInfo(true).then(function(data) {
      if (data.storage_current !== undefined) {
        $scope.stats.totalStorage = formatFileSize(data.storage_current);
        
        // Calculate usage percentage
        if (data.storage_quota) {
          var usedPercent = Math.round((data.storage_current / data.storage_quota) * 100);
          $scope.processingStatus.completed = Math.min(usedPercent, 100);
          $scope.processingStatus.inProgress = Math.max(Math.min(100 - usedPercent, 100), 0);
          $scope.processingStatus.queued = 0;
        }
      }
    });
    
    // Get recent activity from audit logs
    Restangular.one('auditlog').get().then(function(data) {
      var recentActivity = [];
      var today = new Date();
      today.setHours(0, 0, 0, 0);
      var processedToday = 0;
      
      // Convert audit logs to recent activity format
      angular.forEach(data.logs, function(log, index) {
        if (index < 5) {  // Only show latest 5 activities
          var logDate = new Date(log.create_date);
          
          // Count documents processed today
          if (logDate >= today && (log.type === 'FILE' || log.type === 'OCR')) {
            processedToday++;
          }
          
          // Format for the activity feed
          var activityType = log.type.toLowerCase();
          if (activityType === 'file') activityType = 'upload';
          else if (activityType === 'tag') activityType = 'tag';
          else if (activityType === 'acl') activityType = 'share';
          else if (activityType === 'ocr') activityType = 'ocr';
          else if (activityType === 'comment') activityType = 'comment';
          
          recentActivity.push({
            type: activityType,
            user: log.username || 'System',
            document: log.message || (log.entity_id ? log.entity_id : 'Document'),
            time: $filter('timeAgo')(log.create_date)
          });
        }
      });
      
      $scope.recentActivity = recentActivity;
      $scope.stats.processedToday = processedToday;
    });
    
    // Get pending reviews (documents with workflow)
    Restangular.one('document/list').get({
      asc: false,
      sort_column: 3,
      limit: 100,
      search: 'workflow:me'
    }).then(function(data) {
      $scope.stats.pendingReviews = data.total;
    });
  }
  
  // Load dashboard data initially
  loadDashboardData();
  
  // Refresh dashboard functionality
  $scope.refreshDashboard = function() {
    loadDashboardData();
  };
});
