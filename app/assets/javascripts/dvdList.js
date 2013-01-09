$(function() {
	
	$('.coverwrapper em').live('mouseenter',function() {
	      $('.dvdInfo',this).fadeIn(150);
	    }).live('mouseleave',function() {
	      $('.dvdInfo',this).fadeOut(150);
	    });
	
	/**
	 * INFO DIALOG
	 */
	// open the info dialog when the user clicks on the info button
	$('.coverwrapper em').live('click',function(event){
		displayAjaxDialog({
			route: jsRoutes.controllers.Dashboard.displayDvd($(this).data('dvdId')),
        	title: 'Is set from the displaydvdSacla',
        	cssClass:	'dvdInfoModal'
		});
	  return false;		
	});
	/**
	 * EO INFO DIALOG
	 */
	
	/**
	 * LEND DIALOG
	 */
	$('.lendDvdBtn').live('click',function(event){
		displayAjaxDialog({
			route: jsRoutes.controllers.Dashboard.lendDialogContent($(this).data('dvdId')),
	    	title: 'Delete DVD',
	    	cssClass:	'smallModal',
	    	closeButton: true,
	    	buttons: {
	    		"Lend" : {
	    			icon: "icon-trash",
	    			cssClass: "btn-danger",
	    			callback: function() {
	    				var userVal = $('#lendToUser').val();
	    				var freeVal = $('#freeName').val();
	    				var lendOtherInHull = ($('#alsoOthersInHull').attr('checked') == 'checked') ? "true" : "false" ;
	    				if((userVal == null || userVal == "") && (freeVal == null || freeVal == "")) {
	    					return;
	    				}
	    				if(userVal != null && userVal != "" && freeVal != null && freeVal != "") {
	    					return;
	    				}
	    				pAjax(jsRoutes.controllers.Dashboard.lendDvd(
	    				  $("#lendDvdId").val()),
	    				  {"userName" : userVal, "freeName" : freeVal, "alsoOthersInHull" :  lendOtherInHull},
	    			      function(data){
	    				    alert('aww');
	    				   },
	    				   function(err) {
	    				  });
	    				closeDialog();
					}
	    		}
	    	}
		});
	});
	
	$('.unlendDvdBtn').live('click',function(event){	
		displayAjaxDialog({
			route: jsRoutes.controllers.Dashboard.unLendDialogContent($(this).data('dvdId')),
	    	title: 'Return DVD',
	    	cssClass:	'smallModal',
	    	closeButton: true,
	    	buttons: {
	    		"Unlend" : {
	    			icon: "icon-trash",
	    			cssClass: "btn-danger",
	    			callback: function() {
	    				var lendOtherInHull = ($('#alsoOthersInHull').attr('checked') == 'checked') ? "true" : "false" ;
						pAjax(jsRoutes.controllers.Dashboard.unlendDvd(
						  $("#unlendDvdId").val()),{"alsoOthersInHull" :  lendOtherInHull},
					      function(data){
						    alert('aww');
						   },
						   function(err) {
						  });
	    				closeDialog();
					}
	    		}
	    	}
		});
		
	});
	/**
	 * EO LEND DIALOG
	 */
	
	/**
	 * DELETE DIALOG
	 */		
	$('.deleteDvdBtn').live('click',function(event){
		displayAjaxDialog({
			route: jsRoutes.controllers.Dashboard.deleteDialogContent($(this).data('dvdId')),
	    	title: 'Delete DVD',
	    	cssClass:	'smallModal',
	    	closeButton: true,
	    	buttons: {
	    		"Delete" : {
	    			icon: "icon-trash",
	    			cssClass: "btn-danger",
	    			callback: function() {
					  var deleteDvdId = $('#deleteDvdId').val();
					  pAjax(jsRoutes.controllers.Dashboard.deleteDvd(deleteDvdId),null,
					    function(data){
						  closeDialog();	
						  window.location.reload();
						}, function(err) {
							closeDialog();
						});
					}
	    		}
	    	}
		});
	});
	/**
	 * EO DELETE DIALOG
	 */
	
	
	/**
	 * SEARCH FORM
	 */
	
	// check if to display the advanced search from
	if(displayAdvancedOrder === true) {
		$('#advancedSearchForm').show();
	}
	
	createSelect2Deselect('#searchGenre',avaibleGenres);
	createSelect2Deselect('#searchOwner',avaibleUsers);
	createSelect2Deselect('#searchAgeRating',avaibleAgeRatings,function(item) { 
		if(item == null || item == "") {
			return item;
		}
		return "<img class='flag' src='/assets/images/agerating/" + item.id + ".gif'/>";
	});
	createSelect2Deselect('#searchOrderBy',listOrderBy,null,false);
	createSelect2Deselect('#searchCopyType',avaibleCopyTypes,function(item) { 
		if(item == null || item == "") {
			return item;
		}
		return "<img class='flag' src='/assets/images/copy_type/" + item.id + ".png'/>";
	});
	/**
	 * EO SEARCH FORM
	 */
	
});