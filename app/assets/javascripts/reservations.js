$(function() {

  // select first tab in reservations
  $('#reservationsTab a:first').tab('show');

  /**
   * Changing the all checkbox value
   */
  $('input.reservationsCheckAll').change(function() {
    var isChecked = $(this).is(":checked");
    var borrowerId = $(this).data('borrower');
    // get all sub and check or uncheck them
    $('#resTab'+borrowerId+' tbody input:checkbox').prop('checked', isChecked);

    reservationsUpdateButtons(borrowerId);
  });

  /**
   * changing a copy checkbox
   */
  $('input.reservationsCopyCheckBox').change(function() {
    var borrowerId = $(this).data('borrower');
    var checkedLenght = $('#resTab'+borrowerId+' tbody input:checkbox:checked').length;
    var totalLenght = $('#resTab'+borrowerId+' tbody input:checkbox').length;

    if(totalLenght == checkedLenght) {
      $('#resTab'+borrowerId+' thead input.reservationsCheckAll').prop('checked', true);
    } else {
      $('#resTab'+borrowerId+' thead input.reservationsCheckAll').prop('checked', false);
    }

    reservationsUpdateButtons(borrowerId);
  });
});

/**
 * Checks if any checkboxes are checked in the reservations borrower copy tab and enables or disables the buttons
 * @param borrowerId
 */
var reservationsUpdateButtons = function(borrowerId) {

  var checkedLenght = $('#resTab'+borrowerId+' tbody input:checkbox:checked').length;

  if(checkedLenght > 0) {
    $('#resTab'+borrowerId+' tfoot button').removeClass('disabled');
    return;
  }

  $('#resTab'+borrowerId+' tfoot button').addClass('disabled');
}

/**
 * Asks the user if he wants to delete the reservations
 * @param borrowerId
 */
var deleteReservations = function(borrowerId) {
  var ids = getIdsAsCommaString(borrowerId);
  if(ids == null || ids == "") {
    return;
  }

  displayDialog({
    title: '<i class="icon-trash"></i> Remove reservations',
    closeButton: true,
    content: 'Remove selected reservations ?',
    buttons : {
      "Delete" : {
        icon: 'icon-trash',
        cssClass: 'btn-danger',
        callback: function()  {
          window.location.href = jsRoutes.controllers.ReservationsController.deleteReservations(ids).absoluteURL();
        }
      }
    }
  });
}

/**
 * Asks the user if he wants to lend the reservations
 * @param borrowerId
 */
var borrowReservations = function(borrowerId) {
  var ids = getIdsAsCommaString(borrowerId);

  if(ids == null || ids == "") {
    return;
  }

  displayDialog({
    title: '<i class="icon-share"></i> Borrow reservations',
    closeButton: true,
    content: 'Borrow selected reservations ?',
    buttons : {
      "Borrow" : {
        icon: 'icon-share',
        cssClass: 'btn-warning',
        callback: function()  {
          window.location.href = jsRoutes.controllers.ReservationsController.borrowReservations(ids).absoluteURL();
        }
      }
    }
  });
}

/**
 * Gets the ids of teh reservations as comma seperated string
 * @param borrowerId
 * @returns {string}
 */
var getIdsAsCommaString = function(borrowerId) {
  var ids = [];
  $('#resTab'+borrowerId+' tbody input:checkbox:checked').each(function(i,obj) {
    ids.push($(obj).data('resid'));
  });

  return ids.join(',');
}

/**
 * Removes a reservation a user made himself
 * @param reservationId
 */
var removeReserved = function(reservationId) {
  displayDialog({
    title: 'Remove reservation ?',
    closeButton: true,
    content: 'Remove reservation ? <br />'+$('#ownReservation'+reservationId+' .media').html(),
    buttons : {
      "Ok" : {
        icon: 'icon-trash',
        cssClass: 'btn-danger',
        callback: function()  {
          window.location.href = jsRoutes.controllers.ReservationsController.deleteReserved(reservationId).absoluteURL();
        }
      }
    }
  });
}